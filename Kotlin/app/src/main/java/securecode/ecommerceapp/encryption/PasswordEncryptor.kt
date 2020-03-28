package securecodewarrior.ecommerceapp.encryption

import android.annotation.SuppressLint
import java.util.*

@SuppressLint("NewApi")
class PasswordEncryptor {

    private val PASSWORD_KEY = "password_key"
    private val IV_SEPARATOR = "]"

    fun decryptPassword(password: String): String {
        val se = SymmetricEncryptor()
        val passwordWithIV = password.split(IV_SEPARATOR)
        val passwordBytes = Base64.getDecoder().decode(passwordWithIV[0])
        val iVBytes = Base64.getDecoder().decode(passwordWithIV[1])
        val password = se.decrypt(PASSWORD_KEY, passwordBytes, iVBytes)
        return String(password!!)
    }

    fun encryptPassword(password: String): String {
        val se = SymmetricEncryptor()
        se.generateKey(PASSWORD_KEY)
        val encryptedPasswordBytes = se.encrypt(PASSWORD_KEY, password.toByteArray())
        val encryptedPassword =
                Base64.getEncoder().encodeToString(encryptedPasswordBytes) +
                        IV_SEPARATOR +
                        Base64.getEncoder().encodeToString(se.iv)
        return encryptedPassword
    }

}