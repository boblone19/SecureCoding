package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import securecodewarrior.ecommerceapp.BuildConfig
import securecodewarrior.ecommerceapp.R
import java.io.IOException

class SplashActivity : Activity() {

    private val PACKAGE_NAME = "securecodewarrior.ecommerceapp"

    private val knownRootPackages = arrayOf("com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su")

    private val knownRootCloakers = arrayOf("com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.amphoras.hidemyrootadfree",
            "com.formyhm.hiderootPremium",
            "com.formyhm.hideroot")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        if (!isAppRooted()
                && isAppFromAStore()
                && appNotRunningInEmulator()) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }

    private fun isAppFromAStore(): Boolean {
        if (!BuildConfig.DEBUG) {
            val installer = getPackageManager().getInstallerPackageName(PACKAGE_NAME)

            if (installer == null) {
                Toast.makeText(this, "App not from an app store",
                        Toast.LENGTH_LONG).show()
                return false
            }

            if (!installer.contains("google") || installer.contains("amazon")) {
                return true
            }

            Toast.makeText(this, "App not from Play or Amazon stores",
                    Toast.LENGTH_LONG).show()
            return false;
        }
        return true
    }


    fun isAppRooted(): Boolean {
        if (canExecuteCommand("su")) return true
        if (canExecuteCommand("busybox")) return true
        if (isPackageInstalled(knownRootPackages.toList())) return true
        return isPackageInstalled(knownRootCloakers.toList())
    }

    private fun canExecuteCommand(command: String): Boolean {
        try {
            Runtime.getRuntime().exec(command)
            return true
        } catch (localIOException: IOException) {
            //Can  not execute su
            return false
        }
    }

    private fun isPackageInstalled(packages: List<String>): Boolean {
        val pm = getPackageManager()
        for (p in packages) {
            try {
                // App detected
                pm.getPackageInfo(p, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                //Package not installed
            }
        }
        return false
    }

    private fun appNotRunningInEmulator(): Boolean {
        if (isEmulator()) {
            Toast.makeText(this, "This app can't run on an Emulator",
                    Toast.LENGTH_LONG).show()
            return false;
        }
        return true;
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || "goldfish".equals(Build.HARDWARE)
    }


}
