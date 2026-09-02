package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable?
)

object AppListHelper {
    suspend fun getInstalledApps(context: Context): List<AppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        
        val apps = mutableListOf<AppItem>()
        for (info in resolveInfos) {
            // Exclude our own app if desired
            if (info.activityInfo.packageName == context.packageName) continue
            
            val name = info.loadLabel(pm).toString()
            val packageName = info.activityInfo.packageName
            val icon = info.loadIcon(pm)
            
            apps.add(AppItem(name, packageName, icon))
        }
        
        apps.sortedBy { it.name.lowercase() }
    }
}
