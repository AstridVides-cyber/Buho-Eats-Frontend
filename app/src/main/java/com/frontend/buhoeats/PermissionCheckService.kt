package com.frontend.buhoeats

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class PermissionCheckService : Service() {
    companion object {
        private const val CHANNEL_ID = "perm_channel"
        private const val NOTIF_ID = 2
        private const val CHECK_INTERVAL_MS = 5_000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val checkRunnable = object : Runnable {
        override fun run() {
            checkPermissionAndNotify()
            handler.postDelayed(this, CHECK_INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        if (shouldShowPermissionNotification()) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Permiso requerido")
                .setContentText("La app necesita permisos de instalación de apps externas para funcionar correctamente.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
            startForeground(NOTIF_ID, builder.build())
        }
        handler.removeCallbacks(checkRunnable)
        handler.post(checkRunnable)
        return START_STICKY
    }

    private fun shouldShowPermissionNotification(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notifPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (notifPerm != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pm = packageManager
            if (!pm.canRequestPackageInstalls()) {
                return true
            }
        }
        return false
    }

    private fun checkPermissionAndNotify() {
        if (shouldShowPermissionNotification()) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Permiso requerido")
                .setContentText("La app necesita permisos de instalación de apps externas para funcionar correctamente.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
            startForeground(NOTIF_ID, builder.build())
        } else {
            stopForeground(true)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Permisos"
            val descriptionText = "Notificaciones de permisos requeridos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
