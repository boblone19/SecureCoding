package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import kotlinx.android.synthetic.main.activity_web.*
import securecodewarrior.ecommerceapp.R


class WebActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        var webView = webView;

        webView.settings.javaScriptEnabled = false

        webView.settings.builtInZoomControls = true
        webView.settings.setGeolocationEnabled(true)

        webView.webChromeClient = MyWebChromeClient()

        webView.loadUrl("https://securecodewarrior.com/")
    }


    fun showGeolocationDialog(origin: String, callback: GeolocationPermissions.Callback) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Geolocation permission")

        builder.setMessage("This website wants to access your location")

        builder.setPositiveButton("ALLOW") { dialog, which ->
            callback.invoke(origin, true, false)
        }

        builder.setNegativeButton("DON'T ALLOW") { dialog, which ->
            callback.invoke(origin, true, false)
        }

        builder.create().show()
    }


    inner class MyWebChromeClient() : WebChromeClient() {

        override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback) {
            showGeolocationDialog(origin, callback)
        }

    }

}
