@file:OptIn(ExperimentalFoundationApi::class)

package com.defince.featuremain.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.tools.inset
import com.defince.coreuicompose.tools.insetAll
import com.defince.coreuitheme.compose.AppTheme
import com.defince.featuremain.domain.Rank
import com.defince.featuremain.presentation.ScreenNavigator
import com.defince.featuremain.presentation.viewmodel.RankSelectionViewModel

@Composable
fun RankSelectionScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<RankSelectionViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerState.collectAsState()

    RankSelectionScreen(
        uiState = uiState,
        headerState = headerState.value,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankSelectionScreen(
    uiState: RankSelectionViewModel.UiState,
    headerState: MainHeaderState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(uiState = headerState)
            Header()
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.ranks) { Item(it) }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        Modifier.padding(12.dp)
    ) {
        Text(text = "Choose a rank for your points", style = AppTheme.specificTypography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "What is a rank",
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textLink,
            )
            Icon(
                painter = AppTheme.specificIcons.question.get(),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun Item(item: Rank) {
    Card(
        modifier = Modifier
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = AppTheme.shapes.medium
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(item.image.get(), null, modifier = Modifier.size(100.dp))
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(item.type)
                    WorthWidget(ImageValue.ResImage(R.drawable.img_coin_silver) to item.price)
                }
                @Composable
                fun Line(name: String, value: String) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = name,
                            style = AppTheme.specificTypography.bodySmall,
                            color = AppTheme.specificColorScheme.textSecondary,
                        )
                        Text(text = value, style = AppTheme.specificTypography.bodySmall)
                    }
                }
                Line(name = "Daily reward", value = item.dailyReward)
                Line(name = "Daily unlock", value = item.dailyUnlock)
                Line(name = "Daily consumption", value = item.dailyConsumption)
                Line(name = "Dosage per day/month", value = item.dosagePerDayMonth)
                Line(name = "Spending on gas", value = item.spendingOnGas)
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
}