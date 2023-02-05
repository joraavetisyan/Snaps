package io.snaps.featureinitialisation.screen.createwallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureinitialisation.ScreenNavigator
import io.snaps.featureinitialisation.viewmodel.CreateViewModel
import io.snaps.featureinitialisation.viewmodel.Phrase

@Composable
fun PhraseListScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<CreateViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    PhraseListScreen(
        uiState = uiState,
        onContinueButtonClicked = router::toVerificationScreen,
        onBackClicked = router::back,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhraseListScreen(
    uiState: CreateViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topBarTitle = LocalStringHolder.current(StringKey.PhraseListTitle)
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
        floatingActionButton = {
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onContinueButtonClicked,
            ) {
                SimpleButtonContent(text = StringKey.PhraseListActionContinue.textValue())
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier.padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = LocalStringHolder.current(StringKey.PhraseListMessage),
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AppTheme.specificColorScheme.textSecondary,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(uiState.phrases) { item ->
                    Item(phrase = item)
                }
            }
        }
    }
}

@Composable
private fun Item(
    phrase: Phrase,
) {
    val text = buildAnnotatedString {
        val textFormatted = "${phrase.orderNumber}. ${phrase.text}"
        append(textFormatted)
        val ordinalNumberText = "${phrase.orderNumber}."
        val startIndex = textFormatted.indexOf(ordinalNumberText)
        val endIndex = startIndex + ordinalNumberText.length
        addStyle(
            style = SpanStyle(
                color = AppTheme.specificColorScheme.textSecondary,
                textDecoration = TextDecoration.None
            ),
            start = startIndex,
            end = endIndex,
        )
    }
    Text(
        text = text,
        color = AppTheme.specificColorScheme.textPrimary,
        style = AppTheme.specificTypography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, CircleShape)
            .background(AppTheme.specificColorScheme.lightGrey, CircleShape)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    )
}