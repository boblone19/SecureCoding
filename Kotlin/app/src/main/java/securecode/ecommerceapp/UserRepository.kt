package securecodewarrior.ecommerceapp

import android.content.Context
import com.commonsware.cwac.saferoom.SafeHelperFactory
import kotlinx.coroutines.*
import securecodewarrior.ecommerceapp.activities.LoginActivity
import securecodewarrior.ecommerceapp.database.CipherDatabase
import securecodewarrior.ecommerceapp.database.RefreshToken
import securecodewarrior.ecommerceapp.model.AuthResponse
import securecodewarrior.ecommerceapp.model.User
import securecodewarrior.ecommerceapp.network.SCWRetrofitFactory


class UserRepository {

    companion object {
        val instance: UserRepository by lazy {
            UserRepository()
        }
    }

    fun login(email: String, password: String, callback: LoginCallback,
              context: Context): Job {
        val service = SCWRetrofitFactory.makeAuthService(context)
        return GlobalScope.launch(Dispatchers.Main) {
            val request = service.getUser(email, password)
            val response = request.await()
            if (response.isSuccessful) {
                saveRefreshToken(response.body()!!, context)
                context.getSharedPreferences(LoginActivity.LOGIN_PREFS,
                        Context.MODE_PRIVATE)
                        .edit().putBoolean(LoginActivity.FIRST_LOGIN,
                                false).commit()
                callback.onSuccess(response.body()!!.user)
            } else {
                callback.onError("Error ${response.code()}")
            }
        }
    }

    private fun saveRefreshToken(response: AuthResponse, context: Context) {
        val factory = SafeHelperFactory(response.pincode.toCharArray())

        val db = CipherDatabase.getInstance(context, factory)

        db.refreshTokenDAO().insertToken(RefreshToken(response.refreshToken,
                System.currentTimeMillis()))
    }

    fun loginWithToken(refreshToken: String, callback: LoginCallback,
                       context: Context): Job {
        val service = SCWRetrofitFactory.makeAuthService(context)
        return GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO, {
                service.refreshToken(refreshToken)
            })
            if (response.isSuccessful) {
                callback.onSuccess(response.body()!!.user)
            } else {
                callback.onError("Error ${response.code()}")
            }
        }
    }

    interface LoginCallback {
        fun onSuccess(user: User)
        fun onError(errorMessage: String)
    }
}