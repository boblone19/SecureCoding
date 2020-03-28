package securecodewarrior.ecommerceapp.encryption


import android.annotation.SuppressLint
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import securecodewarrior.ecommerceapp.BuildConfig
import java.lang.Exception
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

@SuppressLint("NewApi")
class SymmetricEncryptor {

    private val CIPHER_TYPE = KeyProperties.KEY_ALGORITHM_AES +
            "/" + KeyProperties.BLOCK_MODE_GCM +
            "/" + KeyProperties.ENCRYPTION_PADDING_NONE

    private val GCM_TAG_LENGTH = 128

    private var keyStore: KeyStore? = null
    var iv: ByteArray? = null
        private set

    init {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore!!.load(null)
        } catch (e: KeyStoreException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }

    fun generateKey(alias: String) {
        try {
            if (!keyStore!!.containsAlias(alias)) {
                val keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

                keyGenerator.init(
                        KeyGenParameterSpec.Builder(alias,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setKeySize(256)
                                .setUserAuthenticationRequired(false)
                                .build())
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }

    fun encrypt(alias: String, plainText: ByteArray): ByteArray? {
        try {
            val key = keyStore!!.getKey(alias, null) as SecretKey
            val cipher = Cipher.getInstance(CIPHER_TYPE)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            iv = cipher.iv
            return cipher.doFinal(plainText)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return null
    }

    fun decrypt(alias: String, cipherText: ByteArray, IV: ByteArray): ByteArray? {
        try {
            val key = keyStore!!.getKey(alias, null) as SecretKey
            val cipher = Cipher.getInstance(CIPHER_TYPE)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, IV)
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)
            return cipher.doFinal(cipherText)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return null
    }


}

