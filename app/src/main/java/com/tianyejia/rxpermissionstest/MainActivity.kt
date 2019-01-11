package com.tianyejia.rxpermissionstest

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_test.setOnClickListener {
            requestInstallPackages()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Activity.requestInstallPackages() {
        val grant = packageManager.canRequestPackageInstalls()
        Log.d("MainActivity", "current install package permissions: $grant")
        var rxPermissions: RxPermissions? = RxPermissions(this)
        rxPermissions?.request(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            ?.subscribe { grant ->
                Log.d(
                    "MainActivity",
                    "current rxPermissions return install package permissions: " + grant
                )
                if (grant) {

                } else {
                    showPermissionDenyDialog("test"){
                        startInstallPermissionSettingActivity()
                    }
                    //do some thing
                }
            }
    }
}

fun Activity.showPermissionDenyDialog(
    message: String,
    onCancel: ((dialog: DialogInterface) -> Unit?)? = null,
    onConfirm: ((dialog: DialogInterface) -> Unit?)? = null
) {
    AlertDialog.Builder(this)
        .setTitle("request setting permission")
        .setMessage(message)
        .setPositiveButton("confirm") { dialog, _ ->
            onConfirm?.invoke(dialog)
            dialog.dismiss()
            startApplicationSetting()
        }
        .setNegativeButton("cancel") { dialog, _ ->
            onCancel?.invoke(dialog)
            dialog.dismiss()
        }
        .setCancelable(false)
        .show()
}

fun Activity.startApplicationSetting() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.parse("package:$packageName")
    startActivity(intent)
}

@RequiresApi(api = Build.VERSION_CODES.O)
fun Activity.startInstallPermissionSettingActivity() {
    val intent =
        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
    startActivityForResult(intent, 1)
}