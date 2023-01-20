@file:OptIn(ExperimentalFoundationApi::class)

package com.defince.featuremain.presentation.screen

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.coreuicompose.tools.get
import com.defince.coreuitheme.compose.AppTheme
import com.defince.featuremain.domain.Nft
import com.defince.featuremain.presentation.ScreenNavigator
import com.defince.featuremain.presentation.viewmodel.ItemListViewModel

@Composable
fun ItemListScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ItemListViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ItemListScreen(
        uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemListScreen(
    uiState: ItemListViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
        },
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Header()
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.items) { item ->
                    Item(item)
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Column(
        Modifier.padding(12.dp)
    ) {
        Text(text = "My Collection", style = AppTheme.specificTypography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "All your inventory is displayed here",
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.text,
        )
    }
}

@Composable
private fun Item(item: Nft) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(color = AppTheme.specificColorScheme.surface, shape = AppTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 16.dp),
    ) {
        Card(
            shape = AppTheme.shapes.small,
        ) {
            Image(
                painter = item.image.get(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(101.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Free",
            style = AppTheme.specificTypography.bodyMedium,
        )
        Line("Reward", item.reward)
        Line("Daily unlock", item.dailyUnlock)
        Line("Daily costs", item.dailyCosts)
    }
}

@Composable
private fun Line(name: String, value: String) {
    Row {
        Text(text = name, color = AppTheme.specificColorScheme.text)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value)
    }
}