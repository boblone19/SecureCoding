package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.Job
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.UserRepository
import securecodewarrior.ecommerceapp.model.User

class LoginActivity : AppCompatActivity() {

    var passwordRetries = 3
    val passwordWaitS = 60

    var loginJob: Job? = null

    companion object {
        const val PIN_REQUEST_CODE = 2323
        const val LOGIN_PREFS = "login_prefs"
        const val FIRST_LOGIN = "first_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        aboutUs.setOnClickListener {
            startActivity(Intent(this,
                    WebActivity::class.java))
        }

        if (getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE).getBoolean(FIRST_LOGIN, true)) {
            showManualLogin()
        } else {
            trySessionLogin()
        }

    }

    private fun showManualLogin() {
        buttonLogin.visibility = View.VISIBLE
        buttonLogin.setOnClickListener { login() }
    }

    override fun onPause() {
        super.onPause()
        loginJob?.cancel()
    }

    private fun login() {

        if (passwordRetries < 0) {
            Toast.makeText(this@LoginActivity, "Loging temporarily locked to" +
                    "incorrect password, please try again in $passwordWaitS seconds",
                    Toast.LENGTH_LONG).show()
            return
        }

        var email = textInputEmailL.editText?.text.toString()
        var password = textInputPasswordL.editText?.text.toString()

        if (!validateEmail(email)) {
            return; }

        if (!validatePassword(password)) {
            return; }

        loginJob = UserRepository.instance.login(email, password,
                object : UserRepository.LoginCallback {
                    override fun onSuccess(user: User) {
                        var shopIntent = Intent(this@LoginActivity,
                                FingerPrintActivity::class.java)
                        shopIntent.putExtra(ShopActivity.ROLE_KEY, user.role)
                        startActivity(shopIntent)
                        finish()
                    }

                    override fun onError(errorMessage: String) {
                        Toast.makeText(this@LoginActivity, errorMessage,
                                Toast.LENGTH_LONG).show()
                        onBadPassword()
                    }
                }, this)
    }

    private fun trySessionLogin() {
        startActivityForResult(Intent(this, PinActivity::class.java), PIN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshTokenLogin(data!!.getStringExtra(PinActivity.REFRESH_TOKEN))
            } else {
                showManualLogin()
            }
        }
    }

    private fun refreshTokenLogin(refreshToken: String) {
        loginJob = UserRepository.instance.loginWithToken(refreshToken,
                object : UserRepository.LoginCallback {
                    override fun onSuccess(user: User) {
                        var shopIntent = Intent(this@LoginActivity,
                                FingerPrintActivity::class.java)
                        shopIntent.putExtra(ShopActivity.ROLE_KEY, user.role)
                        startActivity(shopIntent)
                        finish()
                    }

                    override fun onError(errorMessage: String) {
                        showManualLogin()
                    }
                }, this)
    }

    private fun onBadPassword() {
        passwordRetries--
        if (passwordRetries < 0) {
            Handler().postDelayed({ passwordRetries = 0 },
                    passwordWaitS * 1000L)
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            textInputEmail.error = "Please introduce a valid email"
            return false
        }
        return true

    }

    private fun validatePassword(password: String): Boolean {
        if (TextUtils.isEmpty(password)) {
            textInputEmail.error = "Please introduce a password"
            return false
        }
        return true;
    }
}
