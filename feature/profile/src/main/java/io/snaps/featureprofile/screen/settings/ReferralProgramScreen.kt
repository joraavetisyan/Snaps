package io.snaps.featureprofile.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyS
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.SimpleBottomDialogUI
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.screen.ButtonData
import io.snaps.featureprofile.screen.DialogUi
import io.snaps.featureprofile.viewmodel.ReferralProgramViewModel
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

    val coroutineScope = rememberCoroutineScope()

    viewModel.command.collectAsCommand {
        when (it) {
            ReferralProgramViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            ReferralProgramViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialog) {
                ReferralProgramViewModel.BottomDialog.ReferralCode -> ReferralCodeDialog(
                    referralCode = uiState.referralCode,
                    isInviteUserButtonEnabled = uiState.isReferralCodeValid,
                    onInviteUserClicked = viewModel::onReferralCodeDialogButtonClicked,
                    onReferralCodeValueChanged = viewModel::onReferralCodeValueChanged,
                )
            }
        },
    ) {
        ReferralProgramScreen(
            uiState = uiState,
            headerState = headerState.value,
            onEnterCodeClicked = viewModel::onEnterCodeClicked,
            onReferralCodeClicked = {},
            onReferralLinkClicked = {},
            onInviteUserButtonClicked = viewModel::onInviteUserButtonClicked,
            onDismissRequest = viewModel::onDismissRequest,
            onDialogCloseButtonClicked = viewModel::onCloseDialogClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            Text(
                text = StringKey.ReferralProgramTitle.textValue().get(),
                style = AppTheme.specificTypography.titleLarge,
                modifier = Modifier.padding(12.dp),
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
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
                        title = StringKey.ReferralProgramTitleDirectReferral.textValue("2")
                            .get().text,
                        message = StringKey.ReferralProgramMessageDirectReferral.textValue("2")
                            .get().text,
                    )
                    DirectReferralCard(
                        image = ImageValue.ResImage(R.drawable.img_direct_referral_1),
                        title = StringKey.ReferralProgramTitleDirectReferral.textValue("1")
                            .get().text,
                        message = StringKey.ReferralProgramMessageDirectReferral.textValue("1")
                            .get().text,
                    )
                }
                SimpleButtonActionM(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, shape = CircleShape)
                        .padding(horizontal = 12.dp),
                    onClick = onInviteUserButtonClicked,
                ) {
                    SimpleButtonContent(text = StringKey.ReferralProgramActionInviteUser.textValue())
                }
            }
        }

        if (uiState.isInviteUserDialogVisibility) {
            DialogUi(
                title = StringKey.ReferralProgramInviteDialogTitle.textValue(),
                message = StringKey.ReferralProgramInviteDialogMessage.textValue(),
                onDismissRequest = onDismissRequest,
                secondaryButton = ButtonData(
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
                style = AppTheme.specificTypography.titleMedium,
                color = AppTheme.specificColorScheme.white,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )
            SimpleButtonGreyS(
                onClick = onEnterCodeClicked,
                modifier = Modifier.shadow(elevation = 16.dp, shape = CircleShape)
            ) {
                SimpleButtonContent(text = StringKey.ReferralProgramActionEnterCode.textValue())
            }
        }
    }
}

@Composable
private fun RowScope.DirectReferralCard(
    image: ImageValue,
    title: String,
    message: String,
) {
    Card(
        shape = AppTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white),
        border = BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.darkGrey),
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
                text = title,
                style = AppTheme.specificTypography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
            Text(
                text = message,
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
                color = AppTheme.specificColorScheme.darkGrey,
                shape = CircleShape,
            )
            .background(color = AppTheme.specificColorScheme.white, shape = CircleShape)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = hint,
            style = AppTheme.specificTypography.titleMedium,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = AppTheme.specificTypography.titleMedium,
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
    onInviteUserClicked: () -> Unit,
    onReferralCodeValueChanged: (String) -> Unit,
) {
    SimpleBottomDialogUI(StringKey.ReferralProgramCodeDialogTitle.textValue()) {
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
                onValueChange = onReferralCodeValueChanged,
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
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                enabled = isInviteUserButtonEnabled,
                onClick = onInviteUserClicked,
            ) {
                SimpleButtonContent(text = StringKey.ReferralProgramCodeDialogActionInvite.textValue())
            }
        }
    }
}