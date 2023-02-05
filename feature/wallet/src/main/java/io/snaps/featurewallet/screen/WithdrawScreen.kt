package io.snaps.featurewallet.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurewallet.ScreenNavigator
import io.snaps.featurewallet.viewmodel.WithdrawViewModel

@Composable
fun WithdrawScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WithdrawViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerState.collectAsState()

    SocialNetworksScreen(
        uiState = uiState,
        headerState = headerState.value,
        onBackClicked = router::back,
        onAddressValueChanged = viewModel::onAddressValueChanged,
        onAmountValueChanged = viewModel::onAmountValueChanged,
        onConfirmTransactionClicked = viewModel::onConfirmTransactionClicked,
        onMaxButtonClicked = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SocialNetworksScreen(
    uiState: WithdrawViewModel.UiState,
    headerState: MainHeaderState,
    onAddressValueChanged: (String) -> Unit,
    onAmountValueChanged: (String) -> Unit,
    onConfirmTransactionClicked: () -> Unit,
    onMaxButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(uiState = headerState)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.back.get(),
                    tint = AppTheme.specificColorScheme.darkGrey,
                    contentDescription = null,
                    modifier = Modifier.clickable { onBackClicked() }
                )
                Text(
                    text = StringKey.WithdrawTitle.textValue().get(),
                    style = AppTheme.specificTypography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onValueChange = onAddressValueChanged,
                value = uiState.addressValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.WithdrawHintAddress),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                }
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                onValueChange = onAmountValueChanged,
                value = uiState.amountValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.WithdrawHintAmount),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                trailingIcon = {
                    SimpleButtonActionS(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = onMaxButtonClicked,
                    ) {
                        SimpleButtonContent(
                            text = StringKey.WithdrawActionMax.textValue()
                        )
                    }
                }
            )
            Text(
                text = StringKey.WithdrawFieldAvailable.textValue(uiState.availableAmount.getFormattedMoneyWithCurrency())
                    .get(),
                style = AppTheme.specificTypography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.End,
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider(
                color = AppTheme.specificColorScheme.darkGrey,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = StringKey.WithdrawFieldTransactionFee.textValue().get(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                Text(
                    text = uiState.transactionFee.getFormattedMoneyWithCurrency(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.End,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = StringKey.WithdrawFieldTotal.textValue().get(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textSecondary,
                    modifier = Modifier.padding(12.dp),
                )
                Text(
                    text = uiState.totalAmount.getFormattedMoneyWithCurrency(),
                    style = AppTheme.specificTypography.bodyMedium,
                    color = AppTheme.specificColorScheme.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.End,
                )
            }
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onConfirmTransactionClicked,
            ) {
                SimpleButtonContent(
                    text = StringKey.WithdrawActionConfirmTransaction.textValue(),
                )
            }
        }
    }
}