package securecodewarrior.ecommerceapp.utils

import android.content.Context
import android.provider.Settings


object DeviceID {

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID)
    }
}