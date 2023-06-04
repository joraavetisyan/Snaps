package io.snaps.featureprofile.presentation.screen.settings

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.date.getLocaleDateByPhotoDateFormat
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.dialog.PhotoAlertDialog
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.EditProfileViewModel
import java.io.File

private const val AUTHORITY_SUFFIX = ".fileprovider"
private const val FILE_NAMING_PREFIX = "JPEG_"
private const val FILE_NAMING_SUFFIX = "_"
private const val FILE_FORMAT = ".jpg"

@Composable
fun EditProfileScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<EditProfileViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    EditProfileScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onDismissRequest = viewModel::onDismissRequest,
        onEditNameClicked = router::toEditNameScreen,
        onPickPhotoClicked = viewModel::onPickPhotoClicked,
        onTakePhotoClicked = viewModel::onTakePhotoClicked,
        onUploadPhotoClicked = viewModel::onUploadPhotoClicked,
    )
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun EditProfileScreen(
    uiState: EditProfileViewModel.UiState,
    onBackClicked: () -> Boolean,
    onDismissRequest: () -> Unit,
    onEditNameClicked: () -> Unit,
    onPickPhotoClicked: (Uri?) -> Unit,
    onTakePhotoClicked: (Uri?) -> Unit,
    onUploadPhotoClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        onPickPhotoClicked(imageUri)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        onTakePhotoClicked(imageUri)
    }
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA,
    ) {
        if (it) {
            cameraLauncher.launch(imageUri)
        }
    }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = StringKey.EditProfileTitle.textValue().get())
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .doOnClick(onClick = onUploadPhotoClicked)
                    .padding(top = 16.dp, bottom = 12.dp),
            ) {
                if (uiState.avatar != null) {
                    Image(
                        painter = uiState.avatar.get(),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp),
                        contentScale = ContentScale.Crop,
                    )
                } else if (uiState.imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        modifier = Modifier.size(128.dp),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    ShimmerTileCircle(size = 128.dp)
                }
            }
            Text(
                text = StringKey.EditProfileFieldPhoto.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.specificColorScheme.textSecondary,
            )
            Spacer(modifier = Modifier.height(32.dp))
            EditButton(name = uiState.name, onClick = onEditNameClicked)
        }
        if (uiState.isDialogVisible) {
            PhotoAlertDialog(
                onDismissRequest = onDismissRequest,
                onTakePhotoClicked = {
                    imageUri = setupOutputUri(context)
                    if (cameraPermissionState.status == PermissionStatus.Granted) {
                        cameraLauncher.launch(imageUri)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                onPickPhotoClicked = {
                    imagePicker.launch("image/*")
                },
            )
        }

        FullScreenLoaderUi(isLoading = uiState.isLoading)
    }
}

@Composable
private fun EditButton(
    onClick: () -> Unit,
    name: String,
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
            text = StringKey.EditProfileHintName.textValue().get(),
            style = AppTheme.specificTypography.titleSmall,
            color = AppTheme.specificColorScheme.textSecondary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = AppTheme.specificTypography.bodyLarge,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Icon(
                painter = AppTheme.specificIcons.navigateNext.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.darkGrey,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun setupOutputUri(context: Context): Uri? {
    val authorities = "${context.applicationContext?.packageName}$AUTHORITY_SUFFIX"
    return FileProvider.getUriForFile(context, authorities, createImageFile(context))
}

private fun createImageFile(context: Context): File {
    val timeStamp = getLocaleDateByPhotoDateFormat()
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "$FILE_NAMING_PREFIX${timeStamp}$FILE_NAMING_SUFFIX",
        FILE_FORMAT,
        storageDir,
    )
}