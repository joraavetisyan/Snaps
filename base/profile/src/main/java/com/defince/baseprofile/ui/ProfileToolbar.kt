//@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
//
//package com.defince.baseprofile.ui
//
//import android.content.res.Configuration
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.material3.TopAppBarScrollBehavior
//import androidx.compose.material3.rememberTopAppBarState
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.defince.corecommon.container.IconValue
//import com.defince.corecommon.model.AppName
//import com.defince.corecommon.model.CurrencyType
//import com.defince.corecommon.model.Effect
//import com.defince.corecommon.model.Loading
//import com.defince.corecommon.model.Money
//import com.defince.corecommon.model.State
//import com.defince.coreuicompose.tools.defaultTileRipple
//import com.defince.coreuicompose.tools.get
//import com.defince.coreuicompose.tools.insetAllExcludeBottom
//import com.defince.coreuicompose.uikit.other.AvatarByName
//import com.defince.coreuicompose.uikit.other.AvatarByNameConfig
//import com.defince.coreuicompose.uikit.other.ShimmerTileCircle
//import com.defince.coreuicompose.uikit.other.ShimmerTileConfig
//import com.defince.coreuicompose.uikit.other.ShimmerTileLine
//import com.defince.coreuicompose.uikit.toolbar.ActionIconData
//import com.defince.coreuicompose.uikit.toolbar.SimpleTopAppBarColors
//import com.defince.coreuicompose.uikit.toolbar.SimpleTopAppBarConfig
//import com.defince.coreuicompose.uikit.toolbar.SingleRowTopAppBar
//import com.defince.coreuicompose.uikit.toolbar.TopAppBarActionIcon
//import com.defince.coreuitheme.compose.AppSpecificTheme
//import com.defince.coreuitheme.compose.LocalAppName
//import com.defince.coreuitheme.compose.PreviewTheme
//
//@Composable
//fun ProfileToolbar(
//    name: State<String>,
//    navigateIcon: IconValue = AppSpecificTheme.specificIcons.navigateNext,
//    scrollBehavior: TopAppBarScrollBehavior,
//    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentColors(),
//    onNameClicked: (() -> Unit)? = null,
//    firstAction: ActionIconData? = null,
//    secondAction: ActionIconData? = null,
//) {
//    val nameValue = (name as? Effect)?.data
//    ProfileToolbarContainer(scrollBehavior = scrollBehavior, colors = colors) {
//        Row(
//            modifier = Modifier
//                .defaultTileRipple(enable = nameValue != null, onClick = onNameClicked)
//                .weight(1f),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            ProfileToolbarAvatar(nameValue)
//            ProfileToolbarName(nameValue)
//            if (onNameClicked != null && nameValue != null) ProfileToolbarNavigateIcon(navigateIcon)
//        }
//        if (nameValue != null && (firstAction != null || secondAction != null)) {
//            firstAction?.let { TopAppBarActionIcon(it) }
//            secondAction?.let { TopAppBarActionIcon(it) }
//        }
//    }
//}
//
//@Composable
//fun ProfileToolbar(
//    name: State<String>,
//    scrollBehavior: TopAppBarScrollBehavior,
//    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentColors(),
//    onNameClicked: (() -> Unit)? = null,
//    firstAction: ActionIconData? = null,
//    secondAction: ActionIconData? = null,
//    content: @Composable RowScope.() -> Unit,
//) {
//    val nameValue = (name as? Effect)?.data
//    val (leftWidth, rightWidth) = when {
//        firstAction == null && secondAction == null -> when {
//            nameValue != null -> 0.dp to ProfileToolbarConfig.AvatarSize
//            else -> 0.dp to 0.dp
//        }
//        firstAction != null && secondAction != null -> {
//            (SimpleTopAppBarConfig.ActionSize * 2 - ProfileToolbarConfig.AvatarSize) to 0.dp
//        }
//        else -> {
//            0.dp to 0.dp
//        }
//    }
//    ProfileToolbarContainer(scrollBehavior = scrollBehavior, colors = colors) {
//        Box(modifier = Modifier.defaultTileRipple(enable = nameValue != null, onClick = onNameClicked)) {
//            ProfileToolbarAvatar(nameValue)
//        }
//        Spacer(modifier = Modifier.width(leftWidth))
//        content()
//        Spacer(modifier = Modifier.width(rightWidth))
//        firstAction?.let { TopAppBarActionIcon(it) }
//        secondAction?.let { TopAppBarActionIcon(it) }
//    }
//}
//
//@Composable
//fun ProfileToolbarContainer(
//    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.whiteColors(),
//    scrollBehavior: TopAppBarScrollBehavior,
//    windowInsets: WindowInsets = insetAllExcludeBottom(),
//    content: @Composable RowScope.() -> Unit,
//) {
//    SingleRowTopAppBar(
//        title = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                content = content,
//            )
//        },
//        windowInsets = windowInsets,
//        colors = colors,
//        scrollBehavior = scrollBehavior,
//        additional = {},
//    )
//}
//
//@Composable
//private fun ProfileToolbarAvatar(name: String?) {
//    when (name) {
//        null -> ShimmerTileCircle(size = ProfileToolbarConfig.AvatarSize)
//        else -> AvatarByName(
//            name = name,
//            colors = when (LocalAppName.current) {
//                AppName.Dengi -> AvatarByNameConfig.yellowColors()
//                AppName.Bank131 -> AvatarByNameConfig.defaultColors()
//            },
//            modifier = Modifier.size(ProfileToolbarConfig.AvatarSize),
//        )
//    }
//}
//
//@Composable
//private fun ProfileToolbarName(name: String?) {
//    val modifier = Modifier.padding(start = 12.dp)
//
//    when (name) {
//        null -> Box(modifier = modifier) {
//            ShimmerTileLine(width = ShimmerTileConfig.WidthMedium)
//        }
//        else -> Text(
//            text = name,
//            modifier = modifier,
//            style = AppSpecificTheme.specificTypography.body1,
//            color = AppSpecificTheme.specificColorScheme.textPrimary,
//        )
//    }
//}
//
//@Composable
//private fun ProfileToolbarNavigateIcon(navigateIcon: IconValue) {
//    Icon(
//        painter = navigateIcon.get(),
//        contentDescription = null,
//        tint = AppSpecificTheme.specificColorScheme.textSecondary,
//        modifier = Modifier.padding(top = 2.dp, start = 12.dp),
//    )
//}
//
//object ProfileToolbarConfig {
//    val AvatarSize = 40.dp
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun Preview() {
//    PreviewTheme {
//        Column {
//            ProfileToolbar(
//                name = Loading(),
//                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
//                onNameClicked = {},
//                firstAction = null,
//            )
//            ProfileToolbar(
//                name = Effect.success("Name"),
//                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
//                onNameClicked = {},
//            )
//            ProfileToolbar(
//                name = Effect.success("Name"),
//                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
//                onNameClicked = {},
//                firstAction = ActionIconData(AppSpecificTheme.specificIcons.settings) {},
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.weight(1f),
//                ) {
//                    Text(
//                        text = "В кошельке рублей",
//                        style = AppSpecificTheme.specificTypography.caption1,
//                        color = AppSpecificTheme.specificColorScheme.textSecondary,
//                    )
//                    val money = Money(CurrencyType.RUB, "12345600")
//                    Text(
//                        text = "${money.getFormattedMoney()} ${money.currency.symbol}",
//                        style = AppSpecificTheme.specificTypography.body2,
//                        color = AppSpecificTheme.specificColorScheme.textPrimary,
//                    )
//                }
//            }
//        }
//    }
//}