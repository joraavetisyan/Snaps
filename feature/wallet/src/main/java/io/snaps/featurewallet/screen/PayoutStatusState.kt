package io.snaps.featurewallet.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.PayoutOrderStatus
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonRedActionS
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.coreuitheme.compose.colors
import io.snaps.coreuitheme.compose.icons

sealed class PayoutStatusState {

    sealed class Data : PayoutStatusState() {

        abstract val dto: PayoutOrderResponseDto
        abstract val onCopyClick: (Uuid) -> Unit

        data class Processing(
            override val dto: PayoutOrderResponseDto,
            override val onCopyClick: (Uuid) -> Unit,
        ) : Data()

        data class Rejected(
            override val dto: PayoutOrderResponseDto,
            override val onCopyClick: (Uuid) -> Unit,
            val onContactSupportClick: () -> Unit,
        ) : Data()
    }

    object Success : PayoutStatusState()

    object Shimmer : PayoutStatusState()
}

@Composable
fun PayoutStatus(
    modifier: Modifier = Modifier,
    data: PayoutStatusState,
) {
    when (data) {
        is PayoutStatusState.Data -> Data(modifier, data)
        PayoutStatusState.Success -> Unit
        PayoutStatusState.Shimmer -> Shimmer()
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Container(modifier = modifier, backgroundColor = AppTheme.specificColorScheme.white) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerTileLine(width = 100.dp)
            ShimmerTileLine(width = 64.dp)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerTileLine(width = 64.dp)
            ShimmerTileLine(width = 100.dp)
        }
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: PayoutStatusState.Data,
) {
    Container(
        modifier = modifier,
        backgroundColor = colors {
            when (data) {
                is PayoutStatusState.Data.Processing -> Color(0xFFF2F3FE) // todo colors
                is PayoutStatusState.Data.Rejected -> Color(0xFFFEF2F2) // todo colors
            }
        },
    ) {
        val color = colors {
            when (data) {
                is PayoutStatusState.Data.Processing -> textLink
                is PayoutStatusState.Data.Rejected -> uiSystemRed
            }
        }
        val style = AppTheme.specificTypography.bodySmall
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = LocalStringHolder.current(StringKey.WalletTitlePaymentStatus),
                color = color,
                style = style,
            )
            Text(
                text = when (data.dto.status) {
                    PayoutOrderStatus.InProcess -> StringKey.WalletFieldPaymentStatusInProcess.textValue()
                    PayoutOrderStatus.Success -> "".textValue()
                    PayoutOrderStatus.Rejected -> StringKey.WalletFieldPaymentStatusRejected.textValue()
                }.get(),
                color = color,
                style = style,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = LocalStringHolder.current(StringKey.WalletTitlePaymentTransactionId),
                    color = color,
                    style = style,
                )
                Text(
                    text = data.dto.id,
                    color = color,
                    style = style,
                )
            }
            IconButton(onClick = { data.onCopyClick(data.dto.id) }) {
                Icon(painter = icons { copy }.get(), contentDescription = null, tint = colors { darkGrey })
            }
        }
        if (data is PayoutStatusState.Data.Rejected) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = LocalStringHolder.current(StringKey.WalletMessagePaymentRejected),
                    color = color,
                    style = style,
                )
                SimpleButtonRedActionS(onClick = data.onContactSupportClick) {
                    SimpleButtonContent(text = StringKey.WalletActionPaymentRejected.textValue())
                }
            }
        }
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit,
) {
    SimpleCard(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewShimmer() {
    PayoutStatus(modifier = Modifier.padding(0.dp), data = PayoutStatusState.Shimmer)
}

@Preview(showBackground = true)
@Composable
private fun PreviewRejected() {
    PayoutStatus(
        modifier = Modifier.padding(16.dp),
        data = PayoutStatusState.Data.Rejected(previewDto(PayoutOrderStatus.Rejected), {}, {})
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewProcessing() {
    PayoutStatus(
        modifier = Modifier.padding(16.dp),
        data = PayoutStatusState.Data.Processing(previewDto(PayoutOrderStatus.InProcess), {})
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSuccess() {
    PayoutStatus(
        modifier = Modifier.padding(16.dp),
        data = PayoutStatusState.Success,
    )
}

private fun previewDto(status: PayoutOrderStatus) = PayoutOrderResponseDto(
    id = "124uo24dsklafmlk",
    entityId = "124uo24dsklafmlk",
    status = status,
)