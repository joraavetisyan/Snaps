@file:OptIn(ExperimentalFoundationApi::class)

package io.snaps.featuretasks.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.EnergyWidget
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.domain.Task
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.featuretasks.viewmodel.TasksViewModel

@Composable
fun TasksScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<TasksViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    TasksScreen(
        uiState = uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(
    uiState: TasksViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            var isFirst by remember { mutableStateOf(true) }
            val title1 = StringKey.TasksTitleSlideCurrent.textValue()
            val title2 = StringKey.TasksTitleSlideHistory.textValue()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Image(
                        painter = ImageValue.Url("https://picsum.photos/44").get(),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                /*TitleSlider(
                    modifier = Modifier.align(Alignment.Center),
                    title1 = title1,
                    title2 = title2,
                    isFirst = isFirst,
                ) { isFirst = !it }*/
            }
            if (isFirst) {
                Titles(
                    StringKey.TasksTitleCurrent.textValue(),
                    StringKey.TasksTitleMessageCurrent.textValue(),
                )
            } else {
                Titles(
                    StringKey.TasksTitleHistory.textValue(),
                    StringKey.TasksTitleMessageHistory.textValue(),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (isFirst) {
                    items(uiState.current, key = { it.id }) { Item(it) }
                } else {
                    items(uiState.history, key = { it.id }) { Item(it) }
                }
            }
        }
    }
}

@Composable
private fun Titles(title1: TextValue, title2: TextValue) {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title1.get(), style = AppTheme.specificTypography.titleLarge)
        Text(title2.get(), style = AppTheme.specificTypography.titleSmall)
    }
}

@Composable
private fun Item(item: Task) {
    SimpleCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(item.title)
                Text(item.description)
            }
            EnergyWidget(value = item.result, isFull = item.isCompleted)
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    TasksScreen(uiState = TasksViewModel.UiState())
}