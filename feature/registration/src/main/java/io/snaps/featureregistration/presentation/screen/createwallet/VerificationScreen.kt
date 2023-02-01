package io.snaps.featureregistration.presentation.screen.createwallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureregistration.presentation.ScreenNavigator
import io.snaps.featureregistration.presentation.screen.SelectorTile
import io.snaps.featureregistration.presentation.screen.SelectorTileData
import io.snaps.featureregistration.presentation.viewmodel.CreateViewModel
import io.snaps.featureregistration.presentation.viewmodel.Phrase

@Composable
fun VerificationScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<CreateViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when(it) {
            CreateViewModel.Command.OpenCreatedWalletScreen -> router.toCreatedWalletScreen()
        }
    }

    VerificationScreen(
        uiState = uiState,
        onContinueButtonClicked = viewModel::onContinueButtonClicked,
        onBackClicked = router::back,
        onPhraseItemClicked = viewModel::onPhraseItemClicked,
        onAnimationFinished = viewModel::onAnimationFinished,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationScreen(
    uiState: CreateViewModel.UiState,
    onContinueButtonClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onPhraseItemClicked: (Phrase) -> Unit,
    onAnimationFinished: (Phrase) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val topBarTitle = LocalStringHolder.current(StringKey.VerificationTitle)
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
                enabled = uiState.isContinueButtonEnabled,
            ) {
                SimpleButtonContent(text = StringKey.VerificationActionContinue.textValue())
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = LocalStringHolder.current(StringKey.VerificationMessage),
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = AppTheme.specificColorScheme.textSecondary,
            )

            for(i in 0..uiState.shuffledPhrases.lastIndex step 3) {
                PhraseBlock(
                    phrases = uiState.shuffledPhrases.subList(i, i + 3),
                    onPhraseItemClicked = onPhraseItemClicked,
                    onAnimationFinished = onAnimationFinished,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PhraseBlock(
    phrases: List<Phrase>,
    onPhraseItemClicked: (Phrase) -> Unit,
    onAnimationFinished: (Phrase) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppTheme.specificColorScheme.white, shape = AppTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey,
                shape = AppTheme.shapes.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "3.",
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.titleSmall,
            modifier = Modifier.padding(start = 24.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            items(phrases) { item ->
                SelectorTile(
                    data = SelectorTileData(
                        text = item.text.textValue(),
                        status = item.status,
                        clickListener = { onPhraseItemClicked(item) },
                        onAnimationFinished = { onAnimationFinished(item) },
                    )
                )
            }
        }
    }
}