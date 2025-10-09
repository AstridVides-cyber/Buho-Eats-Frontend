package com.frontend.buhoeats

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import com.frontend.buhoeats.navigation.AppNavHost

import com.frontend.buhoeats.ui.theme.BuhoEatsTheme
import com.frontend.buhoeats.ui.theme.ThemeManager
import java.io.File

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_INSTALL_PACKAGES_CODE = 1234
        private const val CHANNEL_ID = "perm_channel"
        private const val NOTIF_ID = 1
    }

    // Inicia el servicio en onCreate de MainActivity
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Crear canal de notificaciones ANTES de pedir permisos
        createNotificationChannel()
        // Notificación de prueba para forzar registro
        requestNotificationPermissionIfNeededThenInstall()
        val serviceIntent = Intent(this, PermissionCheckService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        setContent {
            BuhoEatsTheme(darkTheme = ThemeManager.isDarkTheme) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }

    private fun checkInstallPermissionIfNotificationsEnabled() {
        val notificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        if (notificationsEnabled) {
            checkInstallPermission()
        }
    }

    private fun requestNotificationPermissionIfNeededThenInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)
            Log.d("Permisos", "Antes de pedir permiso: hasPermission=$hasPermission, shouldShowRationale=$shouldShowRationale")
            if (!hasPermission) {
                window.decorView.postDelayed({
                    Log.d("Permisos", "Lanzando requestPermission para POST_NOTIFICATIONS")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }, 800)
            } else {
                window.decorView.postDelayed({ checkInstallPermissionIfNotificationsEnabled() }, 1500)
            }
        } else {
            window.decorView.postDelayed({ checkInstallPermissionIfNotificationsEnabled() }, 1500)
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permisos", "Permiso de notificaciones concedido")
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
                checkInstallPermissionIfNotificationsEnabled()
            } else {
                Log.d("Permisos", "Permiso de notificaciones DENEGADO")
                // Si el usuario rechaza, muestra un diálogo explicativo
                AlertDialog.Builder(this)
                    .setTitle("Permiso de notificaciones requerido")
                    .setMessage("Para recibir avisos importantes, activa el permiso de notificaciones en la configuración de la app.")
                    .setPositiveButton("Ir a configuración") { _, _ ->
                        Log.d("Permisos", "Usuario eligió ir a configuración de la app")
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:" + packageName)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        Log.d("Permisos", "Usuario canceló el diálogo de notificaciones")
                    }
                    .setCancelable(false)
                    .show()
            }
        }

    private fun checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Permisos", "Verificando permiso de instalación de paquetes")
            if (!packageManager.canRequestPackageInstalls()) {
                Log.d("Permisos", "Permiso de instalación NO concedido")
                showPermissionDialog()
                showPermissionNotification()
            } else {
                Log.d("Permisos", "Permiso de instalación concedido")
                // Permiso concedido, puedes continuar con la lógica de instalación
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showPermissionNotification() {
        Log.d("Permisos", "Intentando mostrar notificación de permiso de instalación")
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Permiso requerido")
            .setContentText("Para funcionar correctamente, la app necesita permisos de instalación de apps externas.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(NOTIF_ID, builder.build())
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

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso requerido")
            .setMessage("Para instalar actualizaciones, permite la instalación de apps externas.")
            .setPositiveButton("Ir a configuración") { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + packageName))
                startActivityForResult(intent, REQUEST_INSTALL_PACKAGES_CODE)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Si cancela, volvemos a pedir el permiso
                checkInstallPermission()
            }
            .setCancelable(false)
            .show()
    }

    private var apkDownloadId: Long = -1L
    private var apkFileName: String = "update.apk"
    private val githubApkUrl = "https://github.com/HenryMauricio2004/ejemploDeSpoofing/releases/download/1.0.7/app-debug.apk"

    private val onApkDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: return
            if (id == apkDownloadId) {
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkFileName)
                if (file.exists()) {
                    val apkUri = FileProvider.getUriForFile(
                        this@MainActivity,
                        "com.frontend.buhoeats.fileprovider",
                        file
                    )
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Nueva versión disponible")
                        .setMessage("Se encontró una nueva versión de la app. ¡Presiona OK para instalarla!")
                        .setPositiveButton("OK!") { _, _ ->
                            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(apkUri, "application/vnd.android.package-archive")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            startActivity(installIntent)
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    Toast.makeText(this@MainActivity, "No se pudo descargar el APK", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun downloadAndInstallApkFromGithub() {
        val request = DownloadManager.Request(Uri.parse(githubApkUrl))
            .setTitle("Descargando actualización")
            .setDescription("Descargando la última versión de la app")
            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, apkFileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        apkDownloadId = dm.enqueue(request)
        Toast.makeText(this, "Descargando actualización...", Toast.LENGTH_SHORT).show()
    }

    private var isReceiverRegistered = false

    override fun onStart() {
        super.onStart()
        if (!isReceiverRegistered) {
            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // DownloadManager.ACTION_DOWNLOAD_COMPLETE es un broadcast del sistema, pero Android Studio puede seguir mostrando la advertencia.
                // Para evitar el warning, explícitamente usa Context.RECEIVER_EXPORTED para este caso.
                registerReceiver(onApkDownloadComplete, filter, Context.RECEIVER_EXPORTED)
            } else {
                registerReceiver(onApkDownloadComplete, filter)
            }
            isReceiverRegistered = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (isReceiverRegistered) {
            unregisterReceiver(onApkDownloadComplete)
            isReceiverRegistered = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PACKAGES_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!packageManager.canRequestPackageInstalls()) {
                    // Si el usuario no dio el permiso, volvemos a pedirlo
                    checkInstallPermission()
                } else {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkInstallPermissionIfNotificationsEnabled()
    }
}
