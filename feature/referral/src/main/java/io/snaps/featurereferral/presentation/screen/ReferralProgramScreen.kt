package io.snaps.featurereferral.presentation.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startSharePhotoIntent
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.LocalBottomNavigationHeight
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.gradientBackground
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.tools.toPx
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialog
import io.snaps.coreuicompose.uikit.bottomsheetdialog.FootnoteBottomDialogItem
import io.snaps.coreuicompose.uikit.bottomsheetdialog.ModalBottomSheetTargetStateListener
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonContentWithLoader
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.dialog.DiamondDialog
import io.snaps.coreuicompose.uikit.dialog.DiamondDialogButtonData
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.other.TitleSlider
import io.snaps.coreuicompose.uikit.status.FootnoteUi
import io.snaps.coreuicompose.uikit.status.InfoBlock
import io.snaps.coreuicompose.uikit.text.MiddleEllipsisText
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurereferral.ScreenNavigator
import io.snaps.featurereferral.presentation.viewmodel.ReferralProgramViewModel
import kotlinx.coroutines.launch
import toTypeface

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

    ModalBottomSheetTargetStateListener(
        sheetState = sheetState,
        onStateToChange = viewModel::onBottomDialogStateChange,
    )

    val coroutineScope = rememberCoroutineScope()

    val clipboardManager = LocalClipboardManager.current

    val context = LocalContext.current

    viewModel.command.collectAsCommand {
        when (it) {
            ReferralProgramViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            ReferralProgramViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            is ReferralProgramViewModel.Command.OpenUserInfoScreen -> router.toProfileScreen(it.userId)
            is ReferralProgramViewModel.Command.OpenShareDialog -> context.startSharePhotoIntent(
                uri = it.uri,
                text = it.text,
            )
        }
    }

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    val templatePhoto = generateTemplatePhoto(
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_referral_code_template),
        qr = uiState.referralQr,
        code = uiState.referralCode,
    )

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
                ReferralProgramViewModel.BottomDialog.ReferralQr -> ReferralQrBottomDialog(
                    bitmap = templatePhoto,
                    onClick = viewModel::onShareQrClicked,
                )
                ReferralProgramViewModel.BottomDialog.ReferralProgramFootnote -> FootnoteBottomDialog(
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_eating.imageValue(),
                        title = StringKey.ReferralProgramDialogTitleFootnoteMain1.textValue(),
                        text = StringKey.ReferralProgramDialogMessageFootnoteMain1.textValue(),
                    ),
                    FootnoteBottomDialogItem(
                        image = R.drawable.img_guy_glad.imageValue(),
                        title = StringKey.ReferralProgramDialogTitleFootnoteMain2.textValue(),
                        text = StringKey.ReferralProgramDialogMessageFootnoteMain2.textValue(),
                        onClick = viewModel::onShowReferralQrClicked,
                        buttonText = StringKey.ReferralProgramDialogActionFootnoteMain2.textValue(),
                    )
                )
                ReferralProgramViewModel.BottomDialog.ReferralsInvitedFootnote -> ReferralsInvitedBottomDialog()
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
            onReferralProgramFootnoteClick = viewModel::onReferralProgramFootnoteClick,
            onReferralsInvitedFootnoteClick = viewModel::onReferralsInvitedFootnoteClick,
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
    onReferralProgramFootnoteClick: () -> Unit,
    onReferralsInvitedFootnoteClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            MainHeader(state = headerState)

            val pagerState = rememberPagerState()
            val coroutineScope = rememberCoroutineScope()

            val current = StringKey.ReferralProgramTitleSliderMain.textValue()
            val history = StringKey.ReferralProgramTitleSliderMyReferrals.textValue()
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
                onReferralProgramFootnoteClick = onReferralProgramFootnoteClick,
                onReferralsInvitedFootnoteClick = onReferralsInvitedFootnoteClick,
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
    onReferralProgramFootnoteClick: () -> Unit,
    onReferralsInvitedFootnoteClick: () -> Unit,
) {
    HorizontalPager(
        pageCount = 2,
        state = pagerState,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (it) {
                0 -> {
                    FootnoteUi(
                        title = StringKey.ReferralProgramTitleFootnoteMain.textValue(),
                        description = StringKey.ReferralProgramMessageFootnoteMain.textValue(),
                        action = StringKey.ActionHowItWorks.textValue(),
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
                        title = StringKey.ReferralProgramTitleFootnoteMyReferrals.textValue(),
                        description = StringKey.ReferralProgramMessageFootnoteMyReferrals.textValue(),
                        action = StringKey.ActionHowItWorks.textValue(),
                        onClick = onReferralsInvitedFootnoteClick,
                    )
                    uiState.referralsTileState.Content(modifier = Modifier.weight(1f))
                }
            }
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
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp + LocalBottomNavigationHeight.current),
    ) {
        ReferralCodeCard(uiState = uiState, onEnterCodeClicked = onEnterCodeClicked)
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
                image = R.drawable.img_guys_surprised_eating.imageValue(),
                title = StringKey.ReferralProgramTitleDirectReferral.textValue(uiState.firstLevelReferral),
                message = StringKey.ReferralProgramMessageDirectReferral.textValue(uiState.secondLevelReferral),
            )
            DirectReferralCard(
                image = R.drawable.img_guys_surprised_shadowed_eating.imageValue(),
                title = StringKey.ReferralProgramTitleDirectReferral.textValue(uiState.secondLevelReferral),
                message = StringKey.ReferralProgramMessageDirectReferral.textValue(uiState.secondLevelReferral),
            )
        }
        if (uiState.isInviteAvailable) {
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
}

@Composable
private fun ReferralCodeCard(
    uiState: ReferralProgramViewModel.UiState,
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
            painter = R.drawable.img_referral_program_card_background.imageValue().get(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Image(
            painter = R.drawable.img_diamonds.imageValue().get(),
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
                onClick = { if (uiState.isInviteAvailable) onEnterCodeClicked() },
            ) {
                SimpleButtonContent(
                    text = uiState.invitedByCode.takeUnless {
                        it.isBlank()
                    }?.textValue() ?: StringKey.ReferralProgramActionEnterCode.textValue(),
                )
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
            // todo MiddleEllipsisText, but it clears on recompose
            Text(
                text = value,
                style = AppTheme.specificTypography.bodyLarge,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
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
                SimpleButtonContentWithLoader(
                    isLoading = isLoading,
                    text = StringKey.ReferralProgramCodeDialogActionInvite.textValue(),
                )
            }
        }
    }
}

@Composable
private fun ReferralsInvitedBottomDialog() {
    SimpleBottomDialogUI {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Image(
                    painter = R.drawable.img_guy_hands_up.imageValue().get(),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp),
                )
                Text(
                    text = StringKey.ReferralProgramDialogTitleFootnoteMyReferrals.textValue()
                        .get(),
                    style = AppTheme.specificTypography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = StringKey.ReferralProgramDialogMessageFootnoteMyReferrals.textValue()
                        .get(),
                    style = AppTheme.specificTypography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                InfoBlock(message = StringKey.ReferralProgramDialogMessageFootnoteMyReferralsDisclaimer.textValue())
                ReferralLevelBlock(
                    title = StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel1.textValue(),
                    text = StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel1.textValue(),
                    image = R.drawable.img_sunglasses4.imageValue(),
                    backgroundColor = AppTheme.specificColorScheme.uiSystemOrange,
                    rotateDegree = -20f,
                )
                ReferralLevelBlock(
                    title = StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel2.textValue(),
                    text = StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel2.textValue(),
                    image = R.drawable.img_sunglasses9.imageValue(),
                    backgroundColor = AppTheme.specificColorScheme.uiSystemBlue,
                    rotateDegree = 15f,
                )
                ReferralLevelBlock(
                    title = StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel3.textValue(),
                    text = StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel3.textValue(),
                    image = R.drawable.img_sunglasses5.imageValue(),
                    backgroundColor = AppTheme.specificColorScheme.uiSystemPurple,
                    rotateDegree = -30f,
                )
            }
        }
    }
}

@Composable
private fun ReferralLevelBlock(
    title: TextValue,
    text: TextValue,
    image: ImageValue,
    backgroundColor: Color,
    rotateDegree: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = AppTheme.shapes.medium)
            .gradientBackground(
                colors = listOf(
                    backgroundColor,
                    AppTheme.specificColorScheme.uiContentBg,
                ),
                angle = 0f,
            )
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title.get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.white,
            )
            Text(
                text = text.get(),
                style = AppTheme.specificTypography.labelSmall,
                color = AppTheme.specificColorScheme.white,
            )
        }
        Image(
            painter = image.get(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .rotate(rotateDegree),
        )
    }
}

@Composable
fun ReferralQrBottomDialog(
    bitmap: Bitmap,
    onClick: (Bitmap) -> Unit,
) {
    SimpleBottomDialogUI {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            SimpleButtonActionL(
                onClick = { onClick(bitmap) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                SimpleButtonContent(
                    text = "Share template".textValue(),
                    iconLeft = AppTheme.specificIcons.share,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun generateTemplatePhoto(
    bitmap: Bitmap,
    qr: Bitmap?,
    code: String,
): Bitmap {
    val templateBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    Canvas(templateBitmap.asImageBitmap()).apply {
        with(this.nativeCanvas) {
            val padding = 64.dp.toPx()
            val staticLayout = getStaticLayout(
                text = code,
                paint = getTextPaint(
                    size = 130.sp.toPx(),
                    typeface = AppTheme.specificTypography.headlineLarge.toTypeface(),
                ),
                width = bitmap.width - padding.toInt() * 2,
            )
            val staticLayout2 = getStaticLayout(
                text = "Download the SNAPS app and get a referral code".textValue().get().text,
                paint = getTextPaint(
                    size = 60.sp.toPx(),
                    typeface = AppTheme.specificTypography.bodyLarge.toTypeface(),
                ),
                width = bitmap.width - padding.toInt() * 2,
            )
            drawBitmap(bitmap, 0f, 0f, null)
            qr?.let {
                val left = (bitmap.width - it.width) / 2f
                drawBitmap(it, left, padding, null)
            }
            val text1dy = 2 * padding + (qr?.height?.toFloat() ?: 0f)
            val text1dx = (bitmap.width - staticLayout.width) / 2f
            translate(text1dx, text1dy)
            staticLayout.draw(this)
            translate((staticLayout.width - staticLayout2.width) / 2f, staticLayout.height + padding)
            staticLayout2.draw(this)
        }
    }
    return templateBitmap
}

private fun getStaticLayout(
    text: String,
    paint: TextPaint,
    width: Int,
): StaticLayout {
    return StaticLayout
        .Builder
        .obtain(text, 0, text.length, paint, width)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .build()
}

private fun getTextPaint(
    size: Float,
    typeface: Typeface,
) = TextPaint().apply {
    isAntiAlias = true
    this.typeface = typeface
    textSize = size
    color = android.graphics.Color.WHITE
}