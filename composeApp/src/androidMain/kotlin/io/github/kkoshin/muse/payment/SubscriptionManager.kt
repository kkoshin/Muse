package io.github.kkoshin.muse.payment

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

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

    private val helper = BillingHelper(context)

    // 检查当前设备所安装的 GMS 是否支持订阅，避免版本过低
    fun checkGooglePlayServiceAvailable(): Boolean = helper.checkSubscriptionsSupported()

    suspend fun queryProductDetails(productId: String): Result<ProductDetails> =
        helper.queryProductDetails(productId, ProductType.SUBS).mapCatching { it.firstOrNull()!! }

    /**
     * 最终结果是
     */
    suspend fun launchPurchase(
        activity: Activity,
        productDetails: ProductDetails,
        onResult: (Result<Purchase>) -> Unit
    ) = helper.purchase(activity, productDetails) {
        onResult(it.mapCatching { list -> list.firstOrNull()!! })
    }

    // 对于在非App内进行购买，可以主动进行对当前用户的订阅进行查询
    suspend fun queryPurchases(): Result<List<Purchase>> = helper.queryPurchases(ProductType.SUBS)
}