@file:OptIn(ExperimentalAnimationApi::class)

package com.defince.featuremain.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuitheme.compose.AppTheme
import com.defince.featuremain.data.demoReels
import com.defince.featuremain.domain.Icon
import com.defince.featuremain.domain.Reel
import com.defince.featuremain.domain.ReelInfo
import com.defince.featuremain.presentation.ScreenNavigator
import com.defince.featuremain.presentation.viewmodel.ReelsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ReelsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ReelsViewModel>()

    val pagerState = rememberPagerState()
    var isMuted by remember { mutableStateOf(false) }
    val onLiked = remember {
        { index: Int, liked: Boolean ->
            demoReels[index] =
                demoReels[index].copy(reelInfo = demoReels[index].reelInfo.copy(isLiked = liked))
        }
    }
    val isFirstItem by remember(pagerState) {
        derivedStateOf { pagerState.currentPage == 0 }
    }

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { page ->
            pagerState.animateScrollToPage(page)
        }
    }

    Box {
        VerticalPager(
            count = demoReels.size,
            state = pagerState,
            horizontalAlignment = Alignment.CenterHorizontally,
            itemSpacing = 10.dp,
        ) { index ->
            val shouldPlay by remember(pagerState) {
                derivedStateOf {
                    (abs(currentPageOffset) < .5 && currentPage == index) || (abs(
                        currentPageOffset
                    ) > .5 && pagerState.currentPage == index)
                }
            }
            ReelPlayer(
                reel = demoReels[index],
                shouldPlay = shouldPlay,
                isMuted = isMuted,
                isScrolling = pagerState.isScrollInProgress,
                onMuted = { isMuted = it },
                onDoubleTap = { onLiked(index, it) },
            )
            ReelItem(
                reel = demoReels[index],
                onIconClicked = { icon ->
                    when (icon) {
                        Icon.CAMERA -> {}
                        Icon.SHARE -> {}
                        Icon.MORE_OPTIONS -> {}
                        Icon.AUDIO -> {}
                        Icon.LIKE -> {
                            onLiked(index, !demoReels[index].reelInfo.isLiked)
                        }
                        Icon.COMMENT -> {}
                    }
                }
            )
//            ReelHeader(
//                isFirstItem = isFirstItem,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.TopCenter)
//            ) {
//
//            }
        }
    }
}

@Composable
private fun ReelHeader(
    modifier: Modifier = Modifier,
    isFirstItem: Boolean,
    onCameraIconClicked: (Icon) -> Unit
) {
    Box(
        modifier = modifier
            .padding(PaddingValues(8.dp, 16.dp)),
    ) {
        AnimatedVisibility(
            visible = isFirstItem,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "Reels",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )
        }

        IconButton(
            onClick = { onCameraIconClicked(Icon.CAMERA) },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}

@Composable
private fun ReelItem(
    reel: Reel,
    onIconClicked: (Icon) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(0.5f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            ReelsInfoItems(
                reel.reelInfo
            ) {
                onIconClicked(it)
            }
        }
    }
}

@Composable
private fun ReelsInfoItems(
    reelInfo: ReelInfo,
    onIconClicked: (Icon) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            ReelsBottomItems(
                modifier = Modifier.fillMaxWidth(.8f),
                reelInfo = reelInfo,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
        ) {
            ReelsColumnIcons(
                reelInfo = reelInfo,
                onIconClicked = onIconClicked
            )
        }
    }
}

@Composable
private fun ReelsBottomItems(
    modifier: Modifier = Modifier,
    reelInfo: ReelInfo
) {

    var isFollowed by remember {
        mutableStateOf(false)
    }

    var expandedDesc by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Image(
//                painter = rememberAsyncImagePainter(reelInfo.profilePicUrl),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(30.dp)
//                    .clip(CircleShape),
//            )
//            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = reelInfo.username,
                style = AppTheme.specificTypography.bodyLarge,
                color = Color.White,
                overflow = TextOverflow.Ellipsis
            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Box(
//                modifier = Modifier
//                    .border(
//                        BorderStroke(
//                            1.dp,
//                            Color.White
//                        ),
//                        shape = MaterialTheme.shapes.small
//                    )
//                    .clickable {
//                        isFollowed = !isFollowed
//                    }
//                    .animateContentSize()
//
//            ) {
//                Text(
//                    text = if (isFollowed) "Followed" else "Follow",
//                    fontSize = 10.sp,
//                    color = Color.White,
//                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
//                )
//            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        val scrollState = rememberScrollState()
        val interactionSource = remember { MutableInteractionSource() }
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            reelInfo.description?.let { desc ->
                Text(
                    text = desc,
                    style = AppTheme.specificTypography.bodySmall,
                    maxLines = if (expandedDesc) Int.MAX_VALUE else 2,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) {
                            expandedDesc = !expandedDesc
                        }
                        .animateContentSize()
                )
            }

//            Spacer(modifier = Modifier.height(8.dp))
//
//            ReelsExtraBottomItems(
//                modifier = Modifier.fillMaxWidth(),
//                reelInfo
//            )
        }
    }
}

@Composable
private fun ReelsExtraBottomItems(
    modifier: Modifier = Modifier,
    reelInfo: ReelInfo
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReelsExtraBottomItem(
            modifier = Modifier.weight(1f),
            value = reelInfo.audio,
            R.drawable.ic_share
        )
        Spacer(modifier = Modifier.width(8.dp))
        reelInfo.filter?.let {
            ReelsExtraBottomItem(
                modifier = Modifier.weight(1f),
                value = it,
                R.drawable.ic_share
            )
            Spacer(modifier = Modifier.width(8.dp))
        } ?: run {
            reelInfo.location?.let {
                ReelsExtraBottomItem(
                    modifier = Modifier.weight(1f),
                    value = it,
                    Icons.Default.LocationOn
                )
                Spacer(modifier = Modifier.width(8.dp))
            } ?: run {
                if (reelInfo.taggedPeople?.isNotEmpty() == true) {
                    if (reelInfo.taggedPeople.size == 1) {
                        ReelsExtraBottomItem(
                            modifier = Modifier.weight(1f),
                            value = reelInfo.taggedPeople[0],
                            Icons.Default.Person
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        ReelsExtraBottomItem(
                            modifier = Modifier.weight(1f),
                            value = reelInfo.taggedPeople.size.toString(),
                            iconVector = Icons.Default.Person
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReelsExtraBottomItem(
    modifier: Modifier = Modifier,
    value: String,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null
) {

    val scrollState = rememberScrollState()
    var shouldAnimated by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = shouldAnimated) {

        scrollState.animateScrollTo(
            scrollState.maxValue,
            animationSpec = tween(10000, easing = CubicBezierEasing(0f, 0f, 0f, 0f))
        )
        scrollState.scrollTo(0)
        shouldAnimated = !shouldAnimated
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 10.sp,
            maxLines = 1,
            modifier = Modifier.horizontalScroll(scrollState, false)
        )
    }
}

@Composable
private fun ReelsExtraBottomItem(
    modifier: Modifier = Modifier,
    value: String,
    iconVector: ImageVector,
    contentDescription: String? = null
) {

    val scrollState = rememberScrollState()
    var shouldAnimated by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = shouldAnimated) {
        scrollState.animateScrollTo(
            scrollState.maxValue,
            animationSpec = tween(10000, easing = CubicBezierEasing(0f, 0f, 0f, 0f))
        )
        scrollState.scrollTo(0)
        shouldAnimated = !shouldAnimated
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { }
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = value,
            fontSize = 10.sp,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.horizontalScroll(scrollState)
        )
    }
}

@Composable
private fun ReelsColumnIcons(
    reelInfo: ReelInfo,
    onIconClicked: (Icon) -> Unit
) {
    Box {
        Card(
            shape = CircleShape,
            modifier = Modifier.padding(10.dp),
        ) {
            Image(
                painter = ImageValue.Url("https://picsum.photos/60").get(),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Icon(
            painter = ImageValue.ResVector(R.drawable.ic_add).get(),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.BottomCenter),
        )
    }

    TextedIcon(
        icon = if (!reelInfo.isLiked) {
            ImageValue.Vector(Icons.Outlined.FavoriteBorder)
        } else {
            ImageValue.Vector(Icons.Filled.Favorite)
        },
        text = reelInfo.likes.toString(),
        modifier = Modifier.size(36.dp),
        tint = Color.White,
        onIconClicked = {
            onIconClicked(Icon.LIKE)
//            reelInfo.isLiked = !reelInfo.isLiked
        }
    )

    IconButton(onClick = { onIconClicked(Icon.SHARE) }) {
        Icon(
            painter = ImageValue.ResVector(R.drawable.ic_comment).get(),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp),
        )
    }

    IconButton(onClick = { onIconClicked(Icon.SHARE) }) {
        Icon(
            painter = ImageValue.ResVector(R.drawable.ic_share).get(),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
private fun TextedIcon(
    modifier: Modifier = Modifier,
    icon: ImageValue,
    text: String,
    tint: Color = Color.White,
    contentDescription: String? = null,
    onIconClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onIconClicked) {
            Icon(
                painter = icon.get(),
                contentDescription = contentDescription,
                tint = tint,
                modifier = modifier,
            )
        }
        Text(
            text = text,
            color = Color.White,
        )
    }
}