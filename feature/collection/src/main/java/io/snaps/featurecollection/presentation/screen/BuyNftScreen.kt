package io.snaps.featurecollection.presentation.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator

@Composable
fun BuyNftScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }

    BuyNftScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuyNftScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    val skuList = listOf("android.test.purchased")
    val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->

        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // todo
            }
        }
    }
    val billingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(purchasesUpdatedListener)
        .build()
    val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build()
                billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    for (skuDetails in skuDetailsList.orEmpty()) {
                        val flowPurchase = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                        billingClient.launchBillingFlow(context as Activity, flowPurchase)
                    }
                }
            }
        }
        override fun onBillingServiceDisconnected() = Unit
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = AppTheme.specificColorScheme.uiContentBg)
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            SimpleButtonActionL(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    billingClient.startConnection(billingClientStateListener)
                }
            ) {
                SimpleButtonContent(text = "Buy".textValue())
            }
        }
    }
}
