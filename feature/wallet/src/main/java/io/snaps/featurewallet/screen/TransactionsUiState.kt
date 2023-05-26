package io.snaps.featurewallet.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurewallet.domain.TransactionModel
import io.snaps.featurewallet.domain.TransactionPageModel
import io.snaps.featurewallet.toTransactionList

data class TransactionsUiState(
    val transactions: List<TransactionTileState>? = null,
    val errorState: MessageBannerState? = null,
    val emptyState: EmptyListTileState? = null,
    val onListEndReaching: (() -> Unit)? = null,
) {
    companion object {
        fun shimmer() = List(6) {
            TransactionTileState.Shimmer(it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.transactionsItems(
    uiState: TransactionsUiState,
    modifier: Modifier = Modifier,
) {
    uiState.transactions?.let { state ->
        items(
            items = state,
            key = { it.key }
        ) {
            it.Content(modifier = modifier.animateItemPlacement())
        }
    }
    uiState.errorState?.let { state ->
        item { state.Content(modifier = modifier) }
    }
    uiState.emptyState?.let { state ->
        item {
            state.Content(
                modifier = modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.medium,
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            )
        }
    }
}

fun TransactionPageModel.toTransactionsUiState(
    onClicked: (TransactionModel) -> Unit,
    onReloadClicked: () -> Unit,
    onListEndReaching: () -> Unit,
): TransactionsUiState {
    return when {
        isLoading && loadedPageItems.isEmpty() -> TransactionsUiState(transactions = TransactionsUiState.shimmer())
        error != null -> TransactionsUiState(errorState = MessageBannerState.defaultState(onReloadClicked))
        loadedPageItems.isEmpty() -> TransactionsUiState(
            emptyState = EmptyListTileState.defaultState(
                title = StringKey.WalletTitleTransactionsEmpty.textValue(),
                message = StringKey.WalletMessageTransactionsEmpty.textValue(),
            ),
        )
        else -> TransactionsUiState(
            transactions = loadedPageItems.toTransactionList(onClicked).run {
                if (nextPageId == null) this
                else this.plus(TransactionTileState.Progress)
            },
            onListEndReaching = onListEndReaching,
        )
    }
}

@Composable
private fun TransactionHeader() {
    val titles = listOf(
        StringKey.WalletTitleName.textValue(),
        StringKey.WalletTitleQuantity.textValue(),
        StringKey.WalletTitleDateTransfer.textValue(),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        titles.forEach {
            Text(
                text = it.get(),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.bodySmall,
            )
        }
    }
}