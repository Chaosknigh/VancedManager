package com.vanced.manager.core.installer.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import com.vanced.manager.core.installer.service.AppInstallService
import com.vanced.manager.core.installer.service.AppUninstallService
import java.io.File
import java.io.FileInputStream

private const val byteArraySize = 1024 * 1024 // Because 1,048,576 is not readable

fun installApp(apk: File, context: Context) {
    val packageInstaller = context.packageManager.packageInstaller
    val session =
        packageInstaller.openSession(packageInstaller.createSession(sessionParams))
    writeApkToSession(apk, session)
    session.commit(context.installIntentSender)
    session.close()
}

fun installSplitApp(apks: Array<File>, context: Context) {
    val packageInstaller = context.packageManager.packageInstaller
    val session =
        packageInstaller.openSession(packageInstaller.createSession(sessionParams))
    for (apk in apks) {
        writeApkToSession(apk, session)
    }
    session.commit(context.installIntentSender)
    session.close()
}

fun uninstallPackage(pkg: String, context: Context) {
    val packageInstaller = context.packageManager.packageInstaller
    packageInstaller.uninstall(pkg, context.uninstallIntentSender)
}

private fun writeApkToSession(
    apk: File,
    session: PackageInstaller.Session
) {
    val inputStream = FileInputStream(apk)
    val outputStream = session.openWrite(apk.name, 0, apk.length())
    inputStream.copyTo(outputStream, byteArraySize)
    session.fsync(outputStream)
    inputStream.close()
    outputStream.flush()
    outputStream.close()
}

private val intentFlags
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_MUTABLE
    else
        0

private val sessionParams
    get() = PackageInstaller.SessionParams(
        PackageInstaller.SessionParams.MODE_FULL_INSTALL
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setInstallReason(PackageManager.INSTALL_REASON_USER)
        }
    }

private val Context.installIntentSender
    get() = PendingIntent.getService(
        this,
        0,
        Intent(this, AppInstallService::class.java),
        intentFlags
    ).intentSender

private val Context.uninstallIntentSender
    get() = PendingIntent.getService(
        this,
        0,
        Intent(this, AppUninstallService::class.java),
        intentFlags
    ).intentSender