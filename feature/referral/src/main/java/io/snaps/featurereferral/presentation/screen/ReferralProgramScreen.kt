package io.snaps.featurereferral.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonContentLoader
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.dialog.DiamondDialogButtonData
import io.snaps.coreuicompose.uikit.dialog.DiamondDialog
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.EmptyListTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.text.MiddleEllipsisText
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurereferral.ScreenNavigator
import io.snaps.featurereferral.presentation.viewmodel.ReferralProgramViewModel
import io.snaps.featurereferral.presentation.viewmodel.ReferralsUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReferralProgramScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ReferralProgramViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }.collect {
            if (it == ModalBottomSheetValue.Hidden) {
                viewModel.onBottomSheetHidden()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    viewModel.command.collectAsCommand {
        when (it) {
            ReferralProgramViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            ReferralProgramViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is ReferralProgramViewModel.Command.OpenUserInfoScreen -> router.toProfileScreen(it.userId)
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialog) {
                ReferralProgramViewModel.BottomDialog.ReferralCode -> ReferralCodeDialog(
                    referralCode = uiState.inviteCodeValue,
                    isInviteUserButtonEnabled = uiState.isReferralCodeValid,
                    isLoading = uiState.isLoading,
                    onInviteUserClicked = viewModel::onReferralCodeDialogButtonClicked,
                    onInviteCodeValueChanged = viewModel::onInviteCodeValueChanged,
                )
                ReferralProgramViewModel.BottomDialog.ReferralQr -> TODO()
                ReferralProgramViewModel.BottomDialog.ReferralProgram -> {
                    FootnoteBottomDialog(
                        FootnoteBottomDialogItem(
                            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
                            title = "Share your referral code/link".textValue(),
                            text = "Use your referral code or referral link to invite friends to Snaps. The more referrals you have, the more you earn every day! For a direct referral, you get 5% of the level of his rewards every day! For an indirect referral, you get 3% of the level of his rewards every day!".textValue(),
                        ),
                        FootnoteBottomDialogItem(
                            image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
                            title = "Get an increase in daily earnings".textValue(),
                            text = "As soon as you have an impressive number of referrals, you will receive a tangible increase in your daily income in addition to the increase from the earnings of the referrals themselves.".textValue(),
                            onClick = {},
                            buttonText = "Referral program".textValue(),
                        )
                    )
                }
                ReferralProgramViewModel.BottomDialog.ReferralsInvited -> TODO()
            }
        },
    ) {
        ReferralProgramScreen(
            uiState = uiState,
            headerState = headerState.value,
            onEnterCodeClicked = viewModel::onEnterCodeClicked,
            onReferralCodeClicked = {
                clipboardManager.setText(
                    AnnotatedString(uiState.referralCode.removePrefix("#"))
                )
                viewModel.onReferralCodeCopied()
            },
            onReferralLinkClicked = {
                clipboardManager.setText(
                    AnnotatedString(uiState.referralLink)
                )
                viewModel.onReferralLinkCopied()
            },
            onInviteUserButtonClicked = viewModel::onInviteUserButtonClicked,
            onDismissRequest = viewModel::onDismissRequest,
            onDialogCloseButtonClicked = viewModel::onCloseDialogClicked,
            onReferralClick = viewModel::onReferralClick,
            onReferralsReloadClick = viewModel::onReferralsReloadClick,
            onReferralProgramFootnoteClick = viewModel::onReferralProgramFootnoteClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ReferralProgramScreen(
    uiState: ReferralProgramViewModel.UiState,
    headerState: MainHeaderState,
    onEnterCodeClicked: () -> Unit,
    onReferralCodeClicked: () -> Unit,
    onReferralLinkClicked: () -> Unit,
    onInviteUserButtonClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    onDialogCloseButtonClicked: () -> Unit,
    onReferralClick: (UserInfoModel) -> Unit,
    onReferralsReloadClick: () -> Unit,
    onReferralProgramFootnoteClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(state = headerState)

            val pagerState = rememberPagerState()
            val coroutineScope = rememberCoroutineScope()

            // todo strings
            val current = "Main".textValue()
            val history = "My referrals".textValue()
            TitleSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = listOf(current, history),
                selectedItemIndex = pagerState.currentPage,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
            )

            Body(
                pagerState = pagerState,
                uiState = uiState,
                onEnterCodeClicked = onEnterCodeClicked,
                onReferralCodeClicked = onReferralCodeClicked,
                onReferralLinkClicked = onReferralLinkClicked,
                onInviteUserButtonClicked = onInviteUserButtonClicked,
                onReferralClick = onReferralClick,
                onReferralsReloadClick = onReferralsReloadClick,
                onReferralProgramFootnoteClick = onReferralProgramFootnoteClick,
            )
        }

        if (uiState.isInviteUserDialogVisible) {
            DiamondDialog(
                title = StringKey.ReferralProgramInviteDialogTitle.textValue(),
                message = StringKey.ReferralProgramInviteDialogMessage.textValue(),
                onDismissRequest = onDismissRequest,
                secondaryButton = DiamondDialogButtonData(
                    text = StringKey.ReferralProgramDialogActionClose.textValue(),
                    onClick = onDialogCloseButtonClicked,
                ),
            ) {
                CopyButton(
                    hint = StringKey.ReferralProgramHintCode.textValue().get().text,
                    value = uiState.referralCode,
                    onClick = onReferralCodeClicked,
                )
                Spacer(modifier = Modifier.height(8.dp))
                CopyButton(
                    hint = StringKey.ReferralProgramHintLink.textValue().get().text,
                    value = uiState.referralLink,
                    onClick = onReferralLinkClicked,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body(
    pagerState: PagerState,
    uiState: ReferralProgramViewModel.UiState,
    onEnterCodeClicked: () -> Unit,
    onReferralCodeClicked: () -> Unit,
    onReferralLinkClicked: () -> Unit,
    onInviteUserButtonClicked: () -> Unit,
    onReferralClick: (UserInfoModel) -> Unit,
    onReferralsReloadClick: () -> Unit,
    onReferralProgramFootnoteClick: () -> Unit,
) {
    HorizontalPager(
        pageCount = 2,
        state = pagerState,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // todo strings
            when (it) {
                0 -> {
                    FootnoteUi(
                        title = StringKey.ReferralProgramTitle.textValue(),
                        description = "Invite new users and earn even more rewards every day!".textValue(),
                        action = "How it works?".textValue(),
                        onClick = onReferralProgramFootnoteClick,
                    )
                    Main(
                        uiState = uiState,
                        onEnterCodeClicked = onEnterCodeClicked,
                        onReferralCodeClicked = onReferralCodeClicked,
                        onReferralLinkClicked = onReferralLinkClicked,
                        onInviteUserButtonClicked = onInviteUserButtonClicked,
                    )
                }
                1 -> {
                    FootnoteUi(
                        title = "Invited referrals".textValue(),
                        description = "Here you can see all invited referrals. View their activity, the rank and number of their NFTs, and their income growth through the referral program.".textValue(),
                        action = "How it works?".textValue(),
                        onClick = {},
                    )
                    MyReferrals(
                        uiState = uiState,
                        onReferralClick = onReferralClick,
                        onReferralsReloadClick = onReferralsReloadClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun FootnoteUi(
    title: TextValue,
    description: TextValue,
    action: TextValue,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(12.dp),
    ) {
        Text(
            text = title.get(),
            style = AppTheme.specificTypography.headlineLarge,
        )
        Text(
            text = description.get(),
            style = AppTheme.specificTypography.bodyMedium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.defaultTileRipple(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = AppTheme.specificIcons.question.get(),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = action.get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textLink,
            )
        }
    }
}

@Composable
private fun Main(
    uiState: ReferralProgramViewModel.UiState,
    onEnterCodeClicked: () -> Unit,
    onReferralCodeClicked: () -> Unit,
    onReferralLinkClicked: () -> Unit,
    onInviteUserButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        ReferralCodeCard(onEnterCodeClicked = onEnterCodeClicked)
        Spacer(modifier = Modifier.height(16.dp))
        CopyButton(
            hint = StringKey.ReferralProgramHintCode.textValue().get().text,
            value = uiState.referralCode,
            onClick = onReferralCodeClicked,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CopyButton(
            hint = StringKey.ReferralProgramHintLink.textValue().get().text,
            value = uiState.referralLink,
            onClick = onReferralLinkClicked,
        )
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DirectReferralCard(
                image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
                title = StringKey.ReferralProgramTitleDirectReferral.textValue("2"),
                message = StringKey.ReferralProgramMessageDirectReferral.textValue("2"),
            )
            DirectReferralCard(
                image = ImageValue.ResImage(R.drawable.img_direct_referral_1),
                title = StringKey.ReferralProgramTitleDirectReferral.textValue("1"),
                message = StringKey.ReferralProgramMessageDirectReferral.textValue("1"),
            )
        }
        SimpleButtonActionM(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            onClick = onInviteUserButtonClicked,
        ) {
            SimpleButtonContent(text = StringKey.ReferralProgramActionInviteUser.textValue())
        }
    }
}

@Composable
private fun MyReferrals(
    uiState: ReferralProgramViewModel.UiState,
    onReferralClick: (UserInfoModel) -> Unit,
    onReferralsReloadClick: () -> Unit,
) {
    when (uiState.referralsUiState) {
        ReferralsUiState.Shimmer -> {
            Column {
                repeat(3) {
                    CellTileState(
                        leftPart = LeftPart.Shimmer,
                        middlePart = MiddlePart.Shimmer(
                            needValueLine = true,
                        ),
                        rightPart = RightPart.Shimmer(needLine = true),
                    ).Content(modifier = Modifier)
                }
            }
        }
        is ReferralsUiState.Data -> {
            LazyColumn {
                items(uiState.referralsUiState.values, key = { it.entityId }) { model ->
                    CellTileState(
                        leftPart = model.avatar?.let {
                            LeftPart.Logo(it) {
                                transformations(CircleCropTransformation())
                            }
                        },
                        middlePart = MiddlePart.Data(
                            value = model.name.textValue(),
                        ),
                        rightPart = RightPart.ActionIcon(
                            source = AppTheme.specificIcons.forward.toImageValue(),
                            clickListener = { onReferralClick(model) },
                        ),
                    ).Content(modifier = Modifier)
                }
            }
        }
        ReferralsUiState.Empty -> {
            // todo strings
            EmptyListTileState(
                image = ImageValue.ResImage(R.drawable.img_direct_referral_2),
                title = "No referrals yet".textValue(),
                message = "You haven't invited any user yet.".textValue(),
            ).Content(modifier = Modifier)
        }
        ReferralsUiState.Error -> {
            MessageBannerState.defaultState(onClick = onReferralsReloadClick)
                .Content(modifier = Modifier)
        }
    }
}

@Composable
private fun ReferralCodeCard(
    onEnterCodeClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(AppTheme.shapes.medium)
            .fillMaxWidth()
            .heightIn(min = 172.dp, max = 188.dp),
    ) {
        Image(
            painter = ImageValue.ResImage(R.drawable.img_referral_program_card_background).get(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Image(
            painter = ImageValue.ResImage(R.drawable.img_diamonds).get(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = StringKey.ReferralProgramTitleEnterCode.textValue().get(),
                style = AppTheme.specificTypography.headlineSmall,
                color = AppTheme.specificColorScheme.white,
            )
            Text(
                text = StringKey.ReferralProgramMessageEnterCode.textValue().get(),
                style = AppTheme.specificTypography.bodyLarge,
                color = AppTheme.specificColorScheme.white,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )
            SimpleButtonGreyS(
                onClick = onEnterCodeClicked,
            ) {
                SimpleButtonContent(text = StringKey.ReferralProgramActionEnterCode.textValue())
            }
        }
    }
}

@Composable
private fun RowScope.DirectReferralCard(
    image: ImageValue,
    title: TextValue,
    message: TextValue,
) {
    Card(
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white),
        border = BorderStroke(
            width = 1.dp, color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f)
        ),
        modifier = Modifier.weight(1f),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppTheme.specificColorScheme.lightGrey,
                        shape = AppTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = image.get(),
                    contentDescription = null,
                    modifier = Modifier.height(90.dp)
                )
            }
            Text(
                text = title.get(),
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
            Text(
                text = message.get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
            )
        }
    }
}

@Composable
private fun CopyButton(
    hint: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = CircleShape,
            )
            .background(color = AppTheme.specificColorScheme.white, shape = CircleShape)
            .defaultTileRipple(shape = CircleShape) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = hint,
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiddleEllipsisText(
                text = value,
                style = AppTheme.specificTypography.bodyLarge,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
            )
            Icon(
                painter = AppTheme.specificIcons.copy.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.darkGrey,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ReferralCodeDialog(
    referralCode: String,
    isInviteUserButtonEnabled: Boolean,
    isLoading: Boolean,
    onInviteUserClicked: () -> Unit,
    onInviteCodeValueChanged: (String) -> Unit,
) {
    SimpleBottomDialogUI(header = StringKey.ReferralProgramCodeDialogTitle.textValue()) {
        item {
            Text(
                text = LocalStringHolder.current(StringKey.ReferralProgramCodeDialogMessage),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 32.dp),
                textAlign = TextAlign.Center,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                onValueChange = onInviteCodeValueChanged,
                value = referralCode,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.ReferralProgramCodeDialogHintEnterCode),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                enabled = isInviteUserButtonEnabled,
                onClick = onInviteUserClicked,
            ) {
                AnimatedContent(targetState = isLoading) {
                    if (it) SimpleButtonContentLoader()
                    else SimpleButtonContent(
                        text = StringKey.ReferralProgramCodeDialogActionInvite.textValue()
                    )
                }
            }
        }
    }
}