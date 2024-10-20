package io.github.kkoshin.muse.payment

import android.app.Activity
import android.app.Application
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.Builder
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingConfig
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

sealed class BillingException(override val message: String) : Exception(message) {
    class UserCancelled : BillingException("User cancelled")
    class ConnectionError(val errorCode: Int, message: String) : BillingException(message)
    class PlayBillingError(val errorCode: Int, message: String) : BillingException(message) {
        constructor(result: BillingResult) : this(result.responseCode, result.debugMessage)
    }
}

private suspend fun BillingClient.connect(): Result<Unit> =
    suspendCoroutine { continuation ->
        if (isReady) {
            continuation.resume(Result.success(Unit))
            return@suspendCoroutine
        }
        startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resumeWithException(
                        BillingException.ConnectionError(
                            billingResult.responseCode,
                            billingResult.debugMessage
                        )
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

private suspend fun createConnectedBillingClient(builder: Builder): Result<BillingClient> {
    val client = builder.build()
    return client.connect().map { client }
}

class BillingHelper(private val applicationContext: Context) {

    init {
        check(applicationContext is Application)
    }

    // check SUBSCRIPTIONS is supported
    fun checkSubscriptionsSupported(): Boolean {
        val billingClient = BillingClient.newBuilder(applicationContext).build()
        return listOf(FeatureType.SUBSCRIPTIONS, FeatureType.SUBSCRIPTIONS_UPDATE)
            .all {
                billingClient.isFeatureSupported(it).responseCode == BillingResponseCode.OK
            }
    }

    /**
     * @param productType ProductType.INAPP or ProductType.SUBS
     */
    suspend fun queryProductDetails(
        productId: String,
        productType: String
    ): Result<List<ProductDetails>> {
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(productType)
                            .build()
                    )
                )
                .build()
        return createConnectedBillingClient(BillingClient.newBuilder(applicationContext)).mapCatching { client ->
            withContext(Dispatchers.IO) {
                client.queryProductDetails(queryProductDetailsParams).let {
                    if (it.billingResult.responseCode == BillingResponseCode.OK) {
                        it.productDetailsList ?: emptyList()
                    } else {
                        throw BillingException.PlayBillingError(
                            it.billingResult.responseCode,
                            it.billingResult.debugMessage
                        )
                    }
                }
            }
        }
    }

    /**
     * @param productDetails retrieve a value for "productDetails" by calling queryProductDetailsAsync()
     */
    suspend fun purchase(
        activity: Activity,
        productDetails: ProductDetails,
        onResult: (Result<List<Purchase>>) -> Unit,
    ) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply {
                    // For One-time product, "setOfferToken" method shouldn't be called.
                    // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                    // for a list of offers that are available to the user
                    if (productDetails.productType == ProductType.SUBS) {
                        setOfferToken(productDetails.subscriptionOfferDetails!!.first().offerToken)
                    }
                }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingClientBuilder = BillingClient.newBuilder(applicationContext)
            .setListener { billingResult, purchases ->
                when (billingResult.responseCode) {
                    BillingResponseCode.OK -> {
                        onResult(Result.success(purchases ?: emptyList()))
                    }

                    BillingResponseCode.USER_CANCELED -> {
                        onResult(Result.failure(BillingException.UserCancelled()))
                    }

                    else -> onResult(Result.failure(BillingException.PlayBillingError(billingResult)))
                }
            }

        // Launch the billing flow
        createConnectedBillingClient(billingClientBuilder).mapCatching { client ->
            client.launchBillingFlow(activity, billingFlowParams).let {
                if (it.responseCode == BillingResponseCode.OK) {
                    Unit
                } else {
                    throw BillingException.PlayBillingError(it)
                }
            }
        }.onFailure {
            onResult(Result.failure(it))
        }
    }

    suspend fun queryPurchases(productType: String): Result<List<Purchase>>  {
        val billingClientBuilder = BillingClient.newBuilder(applicationContext)
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(productType)
            .build()
        return createConnectedBillingClient(billingClientBuilder).mapCatching { client ->
            client.queryPurchasesAsync(params).let {
                if (it.billingResult.responseCode == BillingResponseCode.OK) {
                    it.purchasesList
                } else {
                    throw BillingException.PlayBillingError(it.billingResult)
                }
            }
        }
    }

    suspend fun queryCountryCode(): Result<String> {
        val billingClientBuilder = BillingClient.newBuilder(applicationContext)
        return createConnectedBillingClient(billingClientBuilder).map { client ->
            client.getBillingConfig().getOrThrow().countryCode
        }
    }

    private suspend fun BillingClient.getBillingConfig(): Result<BillingConfig> = suspendCoroutine {
        val getBillingConfigParams = GetBillingConfigParams.newBuilder().build()
        getBillingConfigAsync(getBillingConfigParams
        ) { billingResult, billingConfig ->
            if (billingResult.responseCode == BillingResponseCode.OK
                && billingConfig != null
            ) {
                it.resume(Result.success(billingConfig))
            } else {
                it.resumeWithException(BillingException.PlayBillingError(billingResult))
            }
        }
    }
}