@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featurefeed.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonLightS
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurefeed.presentation.viewmodel.CreateVideoViewModel
import io.snaps.featurefeed.ScreenNavigator

@Composable
fun CreateVideoScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<CreateVideoViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {}

    CreateVideoScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateVideoScreen(
    uiState: CreateVideoViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .inset(insetAll())
                .padding(12.dp),
        ) {
            Progress(modifier = Modifier.fillMaxWidth(), progress = 0.5f, isDashed = false)
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = AppTheme.specificIcons.close.get(),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopStart),
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = AppTheme.specificIcons.question.get(),
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painter = AppTheme.specificIcons.cameraTimer.get(),
                            contentDescription = null,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SimpleButtonLightS(onClick = { /*TODO*/ }) {
                            SimpleButtonContent(text = "3 min".textValue())
                        }
                        Spacer(Modifier.width(16.dp))
                        SimpleButtonLightS(onClick = { /*TODO*/ }) {
                            SimpleButtonContent(text = "60 s".textValue())
                        }
                        Spacer(Modifier.width(16.dp))
                        SimpleButtonLightS(onClick = { /*TODO*/ }) {
                            SimpleButtonContent(text = "15 s".textValue())
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = AppTheme.specificIcons.flipCamera.get(),
                                    contentDescription = null,
                                )
                            }
                            Text("Flip")
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = AppTheme.specificIcons.play.get(),
                                contentDescription = null,
                                tint = AppTheme.specificColorScheme.white,
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(AppTheme.specificColorScheme.uiAccent, CircleShape)
                                    .padding(8.dp),
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = AppTheme.specificIcons.chooseImage.get(),
                                    contentDescription = null,
                                )
                            }
                            Text("Choose")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    CreateVideoScreen(uiState = CreateVideoViewModel.UiState)
}