package com.defince.featureregistration.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.corecommon.strings.StringKey
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.coreuicompose.uikit.input.SimpleTextField
import com.defince.coreuicompose.uikit.input.formatter.OrdinalNumberFormatter
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.LocalStringHolder
import com.defince.featureregistration.presentation.ScreenNavigator
import com.defince.featureregistration.presentation.viewmodel.WalletImportViewModel

@Composable
fun WalletImportScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletImportViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    WalletImportScreen(
        uiState = uiState,
        onContinueButtonClicked = viewModel::onContinueButtonClicked,
        onPhraseValueChanged = viewModel::onPhraseValueChanged,
        onBackClicked = router::back
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletImportScreen(
    uiState: WalletImportViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onPhraseValueChanged: (String, Int) -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topBarTitle = LocalStringHolder.current(StringKey.WalletImportTitle)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(topBarTitle)
                },
                titleTextStyle = AppTheme.specificTypography.titleMedium,
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
            ) {
                item {
                    Text(
                        text = LocalStringHolder.current(StringKey.WalletImportMessageEnterPhrases),
                        style = AppTheme.specificTypography.titleSmall,
                        color = AppTheme.specificColorScheme.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 24.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                itemsIndexed(uiState.phrases) { index, phrase ->
                    SimpleTextField(
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { value ->
                            onPhraseValueChanged(value, index)
                        },
                        value = phrase,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = if (index != uiState.phrases.lastIndex) {
                                ImeAction.Next
                            } else ImeAction.Done,
                        ),
                        placeholder = {
                            Text(
                                text = "${index + 1}. Enter here",
                                style = AppTheme.specificTypography.titleSmall,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        },
                        visualTransformation = OrdinalNumberFormatter(index + 1),
                    )
                    if (index != uiState.phrases.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}