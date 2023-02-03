@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuremain.presentation.screen.tasks

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.ui.WorthWidget
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyL
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.listtile.CellTile
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuremain.presentation.ScreenNavigator
import io.snaps.featuremain.presentation.screen.SimpleCard
import io.snaps.featuremain.presentation.viewmodel.TaskViewModel

@Composable
fun FindPointsTaskScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TaskViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    FindPointsTaskScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FindPointsTaskScreen(
    uiState: TaskViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.TaskFindPointsTitle.textValue(),
                titleTextStyle = AppTheme.specificTypography.titleLarge,
                navigationIcon = AppTheme.specificIcons.back to { false },
                scrollBehavior = scrollBehavior,
                titleHorizontalArrangement = Arrangement.Center,
                actions = listOf(
                    ActionIconData(AppTheme.specificIcons.share) {},
                ),
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
            SimpleCard {
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
                        Text("Sponsor")
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
                    WorthWidget(null to "LVL 1")
                }
            }

            Text(
                text = StringKey.TaskFindPointsTitle.textValue().get(),
                style = AppTheme.specificTypography.titleLarge,
            )

            Text(
                text = StringKey.TaskFindPointsMessage.textValue().get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
            )

            Column(
                Modifier.border(
                    width = 1.dp,
                    color = AppTheme.specificColorScheme.grey,
                    shape = AppTheme.shapes.medium,
                )
            ) {
                CellTile(
                    data = CellTileState.Data(
                        leftPart = LeftPart.Logo(AppTheme.specificIcons.instagram.toImageValue()) {
                            transformations(CircleCropTransformation())
                        },
                        middlePart = MiddlePart.Data(
                            value = StringKey.TaskFindPointsTitleConnectInstagram.textValue(),
                        ),
                        rightPart = RightPart.ButtonData(StringKey.TaskFindPointsActionConnect.textValue())
                    )
                )
                Divider(modifier = Modifier.fillMaxWidth())
                Text(
                    text = StringKey.TaskFindPointsTitlePointId.textValue().get(),
                    style = AppTheme.specificTypography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                SimpleTextField(value = "", onValueChange = {})
            }

            SimpleButtonActionL(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                SimpleButtonContent(text = StringKey.TaskFindPointsActionVerify.textValue())
            }
            SimpleButtonGreyL(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                SimpleButtonContent(text = StringKey.TaskFindPointsActionNotFound.textValue())
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    FindPointsTaskScreen(uiState = TaskViewModel.UiState())
}