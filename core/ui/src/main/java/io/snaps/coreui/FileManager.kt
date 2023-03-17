package io.snaps.coreui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.Calendar
import javax.inject.Inject

private const val PublicAppDirectoryName = "SNAPS"

class FileManager @Inject constructor(@ApplicationContext val context: Context) {

    fun contentResolver() = context.contentResolver

    fun deleteFile(uri: Uri?) {
        uri ?: return
        contentResolver().delete(uri, null, null)
    }

    fun getMimeType(path: String): MediaType? {
        return (MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path))
            .orEmpty())
            .toMediaTypeOrNull()
    }

    fun createFileFromUri(uri: Uri): File? {
        return getNameFromUri(uri)?.let {
            val tempFile = File(context.cacheDir, it)
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                tempFile.createNewFile()
                outputStream = FileOutputStream(tempFile)
                inputStream = contentResolver().openInputStream(uri)
                inputStream?.copyTo(outputStream)
                outputStream.flush()
                tempFile
            } catch (e: Exception) {
                null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

    fun createFileFromBitmap(bitmap: Bitmap): File? {
        val tempFile =
            createTempFile(generateName(FileType.Pictures), getCacheDir(FileType.Pictures))
        var outputStream: FileOutputStream? = null
        return try {
            tempFile.createNewFile()
            outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            tempFile
        } catch (e: Exception) {
            null
        } finally {
            outputStream?.close()
        }
    }

    fun createPublicFile(type: FileType): Uri {
        fun createPublicFileViaFileProvider(type: FileType): Uri {
            val file = createFile(generateName(type), getPublicDir(type))
            return FileProvider.getUriForFile(context, getFileProviderAuthority(), file)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun createPublicFileViaMediaStore(type: FileType): Uri? {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, generateName(type))
                put(MediaStore.Images.ImageColumns.MIME_TYPE, type.mimeType())
                put(MediaStore.Images.ImageColumns.RELATIVE_PATH, "${type.publicDirectory}/$PublicAppDirectoryName")
            }
            return contentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createPublicFileViaMediaStore(type) ?: createPublicFileViaFileProvider(type)
        } else {
            createPublicFileViaFileProvider(type)
        }
    }

    fun createPrivateFile(type: FileType): Uri {
        val file = createFile(generateName(type), getPrivateDir(type))
        return FileProvider.getUriForFile(context, getFileProviderAuthority(), file)
    }

    fun createInternalFile(type: FileType): Uri {
        val file = createFile(name = generateName(type), dir = getFilesDir(type))
        return FileProvider.getUriForFile(context, getFileProviderAuthority(), file)
    }

    fun createInternalCacheFile(type: FileType): Uri {
        val file = createTempFile(generateName(type), getCacheDir(type))
        return FileProvider.getUriForFile(context, getFileProviderAuthority(), file)
    }

    fun copyFileToInternalStorage(uri: Uri?, fileType: FileType): Uri? {
        return copyFile(uri, createInternalFile(fileType))
    }

    fun copyFile(uri: Uri?, fileType: FileType) = copyFile(uri, createPublicFile(fileType))

    fun readFile(uri: Uri): String? {
        var inputStream: InputStream? = null
        return try {
            inputStream = contentResolver().openInputStream(uri)
            val buf = ByteArray(inputStream!!.available())
            inputStream.read(buf)
            String(buf, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        } finally {
            inputStream?.close()
        }
    }

    private fun copyFile(uri: Uri?, newFileUri: Uri): Uri? {
        uri ?: return null

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        return try {
            inputStream = contentResolver().openInputStream(uri)
            outputStream = contentResolver().openOutputStream(newFileUri)
            val buf = ByteArray(1024)
            inputStream!!.read(buf)
            do {
                outputStream!!.write(buf)
            } while (inputStream.read(buf) != -1)
            newFileUri
        } catch (e: Exception) {
            deleteFile(newFileUri)
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun getFileProviderAuthority() = "${context.packageName}.fileprovider"

    private fun getPublicDir(type: FileType) = File(
        Environment.getExternalStoragePublicDirectory(type.publicDirectory),
        PublicAppDirectoryName,
    ).apply { mkdir() }
    private fun getPrivateDir(type: FileType) = context.getExternalFilesDir(type.privatePath)
    private fun getFilesDir(type: FileType) = context.filesDir
    private fun getCacheDir(type: FileType) = context.cacheDir

    private fun createFile(name: String, dir: File?) = File(dir, name)
    private fun createTempFile(name: String, dir: File?) = File.createTempFile(name, null, dir)

    private fun getRandomId() = Calendar.getInstance().timeInMillis
    private fun generateName(type: FileType) = when (type) {
        FileType.Pictures -> "image_" + getRandomId() + ".jpeg"
        FileType.Videos -> "video_" + getRandomId() + ".mp4"
    }

    private fun getNameFromUri(uri: Uri): String? {
        return contentResolver()
            .query(uri, null, null, null, null)
            ?.run {
                val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
                moveToFirst()
                val fileName = getString(nameIndex)
                close()
                return@run fileName
            }
    }
}

enum class FileType(
    val privatePath: String,
    val publicDirectory: String,
) {

    Pictures("Pictures", Environment.DIRECTORY_PICTURES),
    Videos("Videos", Environment.DIRECTORY_MOVIES);

    fun mimeType() = when (this) {
        Pictures -> "image/jpeg"
        Videos -> "video/mp4"
    }
}