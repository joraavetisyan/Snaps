@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.featuretasks.presentation.ui.TaskToolbar
import io.snaps.featuretasks.presentation.viewmodel.FindPointsViewModel

@Composable
fun FindPointsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<FindPointsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    FindPointsScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onPointIdChanged = viewModel::onPointIdChanged,
        onConnectButtonClicked = viewModel::onConnectButtonClicked,
        onVerifyButtonClicked = viewModel::onVerifyButtonClicked,
        onPointsNotFoundButtonClicked = viewModel::onPointsNotFoundButtonClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FindPointsScreen(
    uiState: FindPointsViewModel.UiState,
    onBackClicked: () -> Boolean,
    onPointIdChanged: (String) -> Unit,
    onConnectButtonClicked: () -> Unit,
    onVerifyButtonClicked: () -> Unit,
    onPointsNotFoundButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TaskToolbar(
                title = StringKey.TaskFindPointsTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                progress = uiState.energy,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SimpleCard(
                modifier = Modifier.border(
                    width = 1.dp,
                    color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                    shape = AppTheme.shapes.medium,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Card(
                        Modifier.background(Color.Unspecified, AppTheme.shapes.medium),
                    ) {
                        Image(
                            painter = ImageValue.Url("https://picsum.photos/66").get(),
                            contentDescription = null,
                            modifier = Modifier.size(66.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = StringKey.TaskFindPointsTitleSponsor.textValue().get(),
                            style = AppTheme.specificTypography.titleSmall,
                        )
                        Spacer(Modifier.height(4.dp))
                        Progress(Modifier.fillMaxWidth(), progress = 1f)
                        Spacer(Modifier.height(4.dp))
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = AppTheme.specificIcons.heart.get(),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = AppTheme.specificColorScheme.uiSystemRed,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "99/100",
                                style = AppTheme.specificTypography.bodySmall,
                                color = AppTheme.specificColorScheme.uiSystemRed,
                            )
                        }
                    }
                    ValueWidget(null to "LVL 1")
                }
            }

            Text(
                text = StringKey.TaskFindPointsTitle.textValue().get(),
                style = AppTheme.specificTypography.titleMedium,
            )

            Text(
                text = StringKey.TaskFindPointsMessage.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textSecondary,
            )

            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.medium,
                    )
                    .padding(16.dp),
            ) {
                CellTile(
                    data = CellTileState.Data(
                        leftPart = LeftPart.Logo(AppTheme.specificIcons.instagram.toImageValue()) {
                            transformations(CircleCropTransformation())
                        },
                        middlePart = MiddlePart.Data(
                            value = StringKey.TaskFindPointsTitleConnectInstagram.textValue(),
                        ),
                        rightPart = RightPart.ButtonData(
                            text = StringKey.TaskFindPointsActionConnect.textValue(),
                            onClick = onConnectButtonClicked,
                        )
                    )
                )
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                )
                Text(
                    text = StringKey.TaskFindPointsTitlePointId.textValue().get(),
                    style = AppTheme.specificTypography.titleSmall,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                SimpleTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.pointId,
                    onValueChange = onPointIdChanged,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))

            SimpleButtonActionM(
                onClick = onVerifyButtonClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SimpleButtonContent(text = StringKey.TaskFindPointsActionVerify.textValue())
            }
            SimpleButtonGreyM(
                onClick = onPointsNotFoundButtonClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                SimpleButtonContent(text = StringKey.TaskFindPointsActionNotFound.textValue())
            }
        }
    }
}