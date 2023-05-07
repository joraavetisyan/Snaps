package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addressEllipsized
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineM
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class PurchaseWithBnbState {

    abstract val nftType: NftType

    data class Data(
        override val nftType: NftType,
        val from: WalletAddress,
        val to: WalletAddress,
        val summary: String,
        val gas: String,
        val total: String,
        val onConfirmClick: () -> Unit,
        val onCancelClick: () -> Unit,
    ) : PurchaseWithBnbState()

    data class Shimmer(
        override val nftType: NftType,
    ) : PurchaseWithBnbState()

    data class Error(
        override val nftType: NftType,
        val message: MessageBannerState? = null,
        val onClick: () -> Unit,
    ) : PurchaseWithBnbState()
}

@Composable
fun PurchaseWithBnb(
    modifier: Modifier = Modifier,
    data: PurchaseWithBnbState,
) {
    when (data) {
        is PurchaseWithBnbState.Shimmer -> Shimmer(data)
        is PurchaseWithBnbState.Error -> Error(data)
        is PurchaseWithBnbState.Data -> Data(data)
    }
}

@Composable
private fun Data(
    data: PurchaseWithBnbState.Data,
) {
    Container(
        state = data,
    ) {
        Content(
            from = data.from,
            to = data.to,
            summary = data.summary,
            gas = data.gas,
            total = data.total,
            onConfirmClick = data.onConfirmClick,
            onCancelClick = data.onCancelClick,
        )
    }
}

@Composable
private fun Content(
    from: String? = null,
    to: String? = null,
    summary: String? = null,
    gas: String? = null,
    total: String? = null,
    onConfirmClick: (() -> Unit)? = null,
    onCancelClick: (() -> Unit)? = null,
) {
    AddressLine(title = "From:".textValue(), value = from)
    Spacer(modifier = Modifier.height(12.dp))
    AddressLine(title = "To:".textValue(), value = to)
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
    )
    Text(text = "Summary".textValue().get())
    Spacer(modifier = Modifier.height(8.dp))
    if (summary != null) {
        Text(
            text = summary,
            style = AppTheme.specificTypography.displaySmall,
        )
    } else {
        ShimmerTileLine(width = 160.dp, height = 40.dp)
    }
    Spacer(modifier = Modifier.height(12.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppTheme.specificColorScheme.lightGrey,
                shape = AppTheme.shapes.medium,
            )
            .padding(8.dp),
    ) {
        PriceLine(title = "Gas price:".textValue(), value = gas?.textValue())
        Spacer(modifier = Modifier.height(12.dp))
        PriceLine(title = "Total:".textValue(), value = total?.textValue())
    }
    Spacer(modifier = Modifier.height(24.dp))
    SimpleButtonActionM(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onConfirmClick?.invoke() },
        enabled = onConfirmClick != null
    ) {
        SimpleButtonContent(text = StringKey.ActionConfirm.textValue())
    }
    Spacer(modifier = Modifier.height(8.dp))
    SimpleButtonInlineM(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onCancelClick?.invoke() },
        enabled = onCancelClick != null
    ) {
        SimpleButtonContent(text = StringKey.ActionCancel.textValue())
    }
}

@Composable
private fun AddressLine(
    title: TextValue,
    value: WalletAddress?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title.get(), color = AppTheme.specificColorScheme.textSecondary)
        if (value != null) {
            Text(
                text = value.addressEllipsized,
                modifier = Modifier.weight(1f),
            )
        } else {
            ShimmerTileLine(width = 120.dp)
        }
    }
}

@Composable
private fun PriceLine(
    title: TextValue,
    value: TextValue?,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title.get())
        if (value != null) {
            Text(text = value.get())
        } else {
            ShimmerTileLine(width = 120.dp)
        }
    }
}

@Composable
private fun Error(
    data: PurchaseWithBnbState.Error,
) {
    Container(
        state = data,
    ) {
        (data.message ?: MessageBannerState.defaultState(onClick = data.onClick)).Content(
            modifier = Modifier,
        )
    }
}

@Composable
private fun Shimmer(
    state: PurchaseWithBnbState.Shimmer,
) {
    Container(
        state = state,
    ) {
        Content()
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    state: PurchaseWithBnbState,
    content: @Composable ColumnScope.() -> Unit,
) {
    SimpleBottomDialogUI(
        modifier = modifier,
        header = "${state.nftType.name} NFT Minting".textValue(),
    ) {
        item {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
            )
        }
    }
}

@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewData() {
    PurchaseWithBnb(
        modifier = Modifier,
        data = PurchaseWithBnbState.Data(
            nftType = NftType.Newbie,
            from = "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3",
            to = "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3",
            summary = "0.0005 BNB",
            gas = "0.000982324324245 BNB",
            total = "0.011982324324245 BNB",
            onConfirmClick = {},
            onCancelClick = {},
        ),
    )
}

@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewError() {
    PurchaseWithBnb(
        modifier = Modifier,
        data = PurchaseWithBnbState.Error(nftType = NftType.Newbie, onClick = {}),
    )
}

@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShimmer() {
    PurchaseWithBnb(
        modifier = Modifier,
        data = PurchaseWithBnbState.Shimmer(nftType = NftType.Newbie),
    )
}