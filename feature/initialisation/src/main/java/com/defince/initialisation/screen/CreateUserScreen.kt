package com.defince.initialisation.screen

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.defince.corecommon.container.textValue
import com.defince.corecommon.date.getLocaleDateByPhotoDateFormat
import com.defince.corecommon.strings.StringKey
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.tools.inset
import com.defince.coreuicompose.tools.insetAll
import com.defince.coreuicompose.uikit.button.SimpleButtonActionL
import com.defince.coreuicompose.uikit.button.SimpleButtonContent
import com.defince.coreuicompose.uikit.input.SimpleTextField
import com.defince.coreuicompose.uikit.status.PhotoAlertDialog
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.LocalStringHolder
import com.defince.initialisation.ScreenNavigator
import com.defince.initialisation.viewmodel.CreateUserViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File

private const val AUTHORITY_SUFFIX = ".fileprovider"
private const val FILE_NAMING_PREFIX = "JPEG_"
private const val FILE_NAMING_SUFFIX = "_"
private const val FILE_FORMAT = ".jpg"

@Composable
fun CreateUserScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<CreateUserViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    CreateUserScreen(
        uiState = uiState,
        onNicknameChanged = viewModel::onNickNameValueChanged,
        onStartButtonClicked = viewModel::onStartButtonClicked,
        onUploadPhotoClicked = viewModel::onUploadPhotoClicked,
        onDismissRequest = viewModel::onDismissRequest,
        onTakePhotoClicked = viewModel::onTakePhotoClicked,
        onPickPhotoClicked = viewModel::onPickPhotoClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CreateUserScreen(
    uiState: CreateUserViewModel.UiState,
    onNicknameChanged: (String) -> Unit,
    onStartButtonClicked: () -> Unit,
    onUploadPhotoClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    onTakePhotoClicked: (Boolean) -> Unit,
    onPickPhotoClicked: (Boolean) -> Unit,
    onDeleteClicked: () -> Unit,
) {
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var hasImage by remember {
        mutableStateOf(false)
    }
    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        hasImage = uri != null
        imageUri = uri
        onPickPhotoClicked(hasImage)
    }
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
            onTakePhotoClicked(hasImage)
        }
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.specificColorScheme.uiContentBg)
            .inset(insetAll()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(148.dp))
        Text(
            text = LocalStringHolder.current(StringKey.CreateUserTitle),
            color = AppTheme.specificColorScheme.textPrimary,
            style = AppTheme.specificTypography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = LocalStringHolder.current(StringKey.CreateUserMessage),
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.titleSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            textAlign = TextAlign.Center,
        )
        SimpleTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            onValueChange = onNicknameChanged,
            value = uiState.nicknameValue,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            placeholder = {
                Text(
                    text = LocalStringHolder.current(StringKey.CreateUserHintNickname),
                    style = AppTheme.specificTypography.titleSmall,
                )
            },
        )
        if (hasImage && uiState.photoStatus == CreateUserViewModel.PhotoStatus.Uploaded) {
            Photo(
                imageUri = imageUri,
                onDeleteClick = onDeleteClicked,
            )
        }
        if (uiState.photoStatus == CreateUserViewModel.PhotoStatus.NotUploaded) {
            SimpleButtonActionL(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onUploadPhotoClicked,
            ) {
                SimpleButtonContent(
                    text = StringKey.CreateUserFieldPhotoUploaded.textValue(),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        if (uiState.photoStatus == CreateUserViewModel.PhotoStatus.Uploaded) {
            SimpleButtonActionL(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onStartButtonClicked,
                enabled = uiState.isStartButtonEnabled,
            ) {
                SimpleButtonContent(
                    text = StringKey.CreateUserActionStart.textValue(),
                )
            }
        }
    }

    if (uiState.isDialogVisible) {
        PhotoAlertDialog(
            onDismissRequest = onDismissRequest,
            onTakePhotoClicked = {
                imageUri = setupOutputUri(imageUri, context)
                cameraLauncher.launch(imageUri)
            },
            onPickPhotoClicked = {
                imagePicker.launch("image/*")
            },
        )
    }
}

@Composable
private fun Photo(
    imageUri: Uri?,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(
                color = AppTheme.specificColorScheme.white,
                shape = AppTheme.shapes.medium,
            )
            .padding(12.dp),
    ) {
        AsyncImage(
            model = imageUri,
            modifier = Modifier
                .size(44.dp)
                .clip(AppTheme.shapes.medium),
            contentDescription = "Selected image",
            contentScale = ContentScale.Crop,
        )
        Text(
            text = LocalStringHolder.current(StringKey.CreateUserFieldPhotoUploaded),
            color = AppTheme.specificColorScheme.textPrimary,
            style = AppTheme.specificTypography.bodySmall,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
        )
        Icon(
            painter = AppTheme.specificIcons.delete.get(),
            contentDescription = null,
            tint = AppTheme.specificColorScheme.darkGrey,
            modifier = Modifier
                .size(16.dp)
                .clickable {
                    onDeleteClick()
                }
                .align(Alignment.CenterVertically),
        )
    }
}

private fun setupOutputUri(outputUri: Uri?, context: Context): Uri? {
    return if (outputUri == null) {
        val authorities = "${context.applicationContext?.packageName}$AUTHORITY_SUFFIX"
        FileProvider.getUriForFile(context, authorities, createImageFile(context))
    } else {
        outputUri
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = getLocaleDateByPhotoDateFormat()
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "$FILE_NAMING_PREFIX${timeStamp}$FILE_NAMING_SUFFIX",
        FILE_FORMAT,
        storageDir
    )
}