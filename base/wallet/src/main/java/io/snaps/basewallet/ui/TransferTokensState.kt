package io.snaps.basewallet.ui

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
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.CryptoAddress
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

sealed class TransferTokensState {

    abstract val title: TextValue

    data class Data(
        override val title: TextValue,
        val from: CryptoAddress,
        val to: CryptoAddress,
        val summary: CoinValue,
        val gas: CoinValue,
        val total: CoinValue,
        val onConfirmClick: () -> Unit,
        val onCancelClick: () -> Unit,
    ) : TransferTokensState()

    data class Shimmer(
        override val title: TextValue,
    ) : TransferTokensState()

    data class Error(
        override val title: TextValue,
        val message: MessageBannerState? = null,
        val onClick: () -> Unit,
    ) : TransferTokensState()
}

@Composable
fun TransferTokensUi(
    modifier: Modifier = Modifier,
    data: TransferTokensState,
) {
    when (data) {
        is TransferTokensState.Shimmer -> Shimmer(data)
        is TransferTokensState.Error -> Error(data)
        is TransferTokensState.Data -> Data(data)
    }
}

@Composable
private fun Data(
    data: TransferTokensState.Data,
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
    summary: CoinValue? = null,
    gas: CoinValue? = null,
    total: CoinValue? = null,
    onConfirmClick: (() -> Unit)? = null,
    onCancelClick: (() -> Unit)? = null,
) {
    AddressLine(title = "From:".textValue(), value = from) // todo localize
    Spacer(modifier = Modifier.height(12.dp))
    AddressLine(title = "To:".textValue(), value = to) // todo localize
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
    )
    Text(text = StringKey.DialogSummaryTitle.textValue().get())
    Spacer(modifier = Modifier.height(8.dp))
    if (summary != null) {
        Text(
            text = summary.getFormatted(),
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
        PriceLine(title = StringKey.DialogSummaryTitleGasPrice.textValue(), value = gas?.getFormatted()?.textValue())
        Spacer(modifier = Modifier.height(12.dp))
        PriceLine(title = StringKey.DialogSummaryTitleTotal.textValue(), value = total?.getFormatted()?.textValue())
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
    value: CryptoAddress?,
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
    data: TransferTokensState.Error,
) {
    Container(
        state = data,
    ) {
        (data.message ?: MessageBannerState.defaultState(onClick = data.onClick)).Content(modifier = Modifier)
    }
}

@Composable
private fun Shimmer(
    state: TransferTokensState.Shimmer,
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
    state: TransferTokensState,
    content: @Composable ColumnScope.() -> Unit,
) {
    SimpleBottomDialogUI(
        modifier = modifier,
        header = state.title,
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
// @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewData() {
    TransferTokensUi(
        modifier = Modifier,
        data = TransferTokensState.Data(
            title = "Newbie".textValue(),
            from = "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3",
            to = "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3",
            summary = CoinBNB(0.0005),
            gas = CoinBNB(0.000982324324245),
            total = CoinBNB(0.011982324324245),
            onConfirmClick = {},
            onCancelClick = {},
        ),
    )
}

@Preview(showBackground = true)
// @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewError() {
    TransferTokensUi(
        modifier = Modifier,
        data = TransferTokensState.Error(title = "Newbie".textValue(), onClick = {}),
    )
}

@Preview(showBackground = true)
// @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShimmer() {
    TransferTokensUi(
        modifier = Modifier,
        data = TransferTokensState.Shimmer(title = "Newbie".textValue()),
    )
}