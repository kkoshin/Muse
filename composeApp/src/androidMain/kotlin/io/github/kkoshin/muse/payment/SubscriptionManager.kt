package io.github.kkoshin.muse.payment

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Subscriptions automatically renew until they are canceled. A subscription can go through the following states:
 *
 * - Active: User is in good standing and has access to the subscription.
 * - Cancelled: User has cancelled but still has access until expiration.
 * - In grace period: User experienced a payment issue but still has access while Google is retrying the payment method.
 * - On hold: User experienced a payment issue and no longer has access while Google is retrying the payment method.
 * - Paused: User paused their access and does not have access until they resume.
 * - Expired: User has cancelled and lost access to the subscription. The user is considered churned at expiration.
 *
 * Handle errors: [https://developer.android.com/google/play/billing/errors](https://developer.android.com/google/play/billing/errors)
 */
class SubscriptionManager(context: Context) {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            // 展示最终的支付结果,这里仅限刚刚完成支付的时候，还可以延后查询支付状态
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    if (!purchases.isNullOrEmpty()) {
                        purchases.forEach {
                            // 支付成功后，得到此次支付的唯一凭证：purchaseToken
                            it.purchaseToken
                        }
                    }
                }
                BillingResponseCode.USER_CANCELED -> {
                    // user cancelled
                }
                else -> {
                    // other error code
                }
            }
        }
        .build()

    // 调用任何接口之前需要确保已经连接上
    private suspend fun BillingClient.requireConnectionSetup(): Boolean =
        suspendCoroutine { continuation ->
            if (isReady) {
                continuation.resume(true)
                return@suspendCoroutine
            }
            startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    continuation.resume(false)
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    continuation.resume(billingResult.responseCode == BillingResponseCode.OK)
                }
            })
        }

    private suspend inline fun withConnection(block: BillingClient.() -> Unit) {
        if (billingClient.requireConnectionSetup()) {
            block(billingClient)
        }
    }

    // 检查当前设备所安装的 GMS 是否支持订阅，避免版本过低
    fun checkGooglePlayServiceAvailable(): Boolean {
        return listOf(FeatureType.SUBSCRIPTIONS, FeatureType.SUBSCRIPTIONS_UPDATE)
            .all {
                billingClient.isFeatureSupported(it).responseCode == BillingResponseCode.OK
            }
    }

    suspend fun queryProductDetails(productId: String): ProductDetails? {
        withConnection {
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    persistentListOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(ProductType.SUBS)
                            .build()
                    )
                ).build()
            val productDetailsResult = withContext(Dispatchers.IO) {
                queryProductDetails(queryProductDetailsParams)
            }
            if (productDetailsResult.billingResult.responseCode == BillingResponseCode.OK) {
                return productDetailsResult.productDetailsList?.get(0)
            }
        }
        return null
    }

    /**
     * 最终结果是
     */
    suspend fun purchase(activity: Activity, productDetails: ProductDetails) {
        withConnection {
            val productDetailsParamsListBuilder =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let {
                productDetailsParamsListBuilder.setOfferToken(it)
            }

            val billingFlow = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    persistentListOf(
                        productDetailsParamsListBuilder.build()
                    )
                )
                .build()
            launchBillingFlow(activity, billingFlow)
        }
    }

    // 对于在非App内进行购买，可以主动进行对当前用户的订阅进行查询
    suspend fun queryPurchases(): Result<List<Purchase>> {
        withConnection {
            val param = QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()
            queryPurchasesAsync(param).let {
                return when (it.billingResult.responseCode) {
                    BillingResponseCode.OK -> {
                        Result.success(it.purchasesList)
                    }

                    else -> {
                        Result.failure(Exception("queryPurchases failed: ${it.billingResult.debugMessage}"))
                    }
                }
            }
        }
        return Result.failure(Exception("connection failed"))
    }
}