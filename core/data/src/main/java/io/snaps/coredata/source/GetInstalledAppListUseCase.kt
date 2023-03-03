package io.snaps.coredata.source

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class AppModel(
    val name: String,
    val drawable: Drawable,
    val packageName: String,
)

class GetInstalledAppListUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

     @SuppressLint("QueryPermissionsNeeded")
     operator fun invoke(filterOptions: List<String>? = null): List<AppModel> {
         val intent = Intent(Intent.ACTION_MAIN).apply {
             addCategory(Intent.CATEGORY_LAUNCHER)
         }
         val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             context.packageManager.queryIntentActivities(
                 intent,
                 PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
             )
         } else {
             context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
         }
         return resolveInfoList.filter {
             val packageName = it.activityInfo.packageName
             packageName in filterOptions.orEmpty()
         }.map {
             AppModel(
                 name = it.loadLabel(context.packageManager).toString(),
                 drawable = it.loadIcon(context.packageManager),
                 packageName = it.activityInfo.packageName,
             )
         }
     }
}