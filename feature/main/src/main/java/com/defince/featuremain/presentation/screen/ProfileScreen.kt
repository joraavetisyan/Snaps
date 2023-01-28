@file:OptIn(ExperimentalFoundationApi::class)

package com.defince.featuremain.presentation.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.duplicate.ActionIconData
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.coreuitheme.compose.AppTheme
import com.defince.featuremain.presentation.ScreenNavigator
import com.defince.featuremain.presentation.viewmodel.Photo
import com.defince.featuremain.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ProfileViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        uiState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    uiState: ProfileViewModel.UiState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text("My Profile")
                },
                titleTextStyle = AppTheme.specificTypography.titleLarge,
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    ActionIconData(
                        icon = AppTheme.specificIcons.settings,
                        color = AppTheme.specificColorScheme.onSurfaceVariant,
                        onClick = {},
                    ),
                    ActionIconData(
                        icon = AppTheme.specificIcons.share,
                        color = AppTheme.specificColorScheme.onSurfaceVariant,
                        onClick = {},
                    ),
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            Info(uiState)
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.gallery.get(),
                    contentDescription = null,
                    tint = AppTheme.specificColorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(28.dp),
                )
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Icon(
                    painter = AppTheme.specificIcons.like.get(),
                    contentDescription = null,
                    tint = AppTheme.specificColorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(28.dp),
                )
            }
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.images) { item ->
                    Item(item)
                }
            }
        }
    }
}

@Composable
private fun Info(
    uiState: ProfileViewModel.UiState,
) {
    Box(
        Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Card(
            shape = AppTheme.shapes.medium,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 16.dp)
                    .padding(top = 32.dp)
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PieceOfInfo(uiState.likes, "Likes")
                VerticalDivider()
                PieceOfInfo(uiState.subscribers, "Subscribers")
                VerticalDivider()
                PieceOfInfo(uiState.subscriptions, "Subscriptions")
                VerticalDivider()
                PieceOfInfo(uiState.publication, "Publication")
            }
        }
        Card(
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 76.dp),
        ) {
            Image(
                painter = uiState.profileImage.get(),
                contentDescription = null,
                modifier = Modifier.size(76.dp),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxHeight()
            .width(1.dp)
    )
}

@Composable
private fun PieceOfInfo(
    value: String,
    name: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = AppTheme.specificTypography.titleMedium)
        Text(text = name, style = AppTheme.specificTypography.bodySmall)
    }
}

@Composable
private fun Item(item: Photo) {
    Box(
        modifier = Modifier
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(color = AppTheme.specificColorScheme.surface, shape = AppTheme.shapes.medium),
    ) {
        Card(
            shape = AppTheme.shapes.small,
        ) {
            Image(
                painter = item.image.get(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(width = 116.dp, height = 162.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            Icon(AppTheme.specificIcons.play.get(), null, tint = AppTheme.specificColorScheme.white)
            Text(item.views, color = AppTheme.specificColorScheme.white)
        }
    }
}