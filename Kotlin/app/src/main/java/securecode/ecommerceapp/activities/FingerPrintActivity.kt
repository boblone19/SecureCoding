package securecodewarrior.ecommerceapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import android.widget.Toast
import securecodewarrior.ecommerceapp.BuildConfig
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.utils.FingerPrintUtils
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

@SuppressLint("NewApi")
class FingerPrintActivity : Activity() {

    private val KEY_NAME = "fingerprint_key"
    private val PERMISSION_REQUEST = 1234


    lateinit var keyGenerator: KeyGenerator
    lateinit var keyStore: KeyStore
    lateinit var cipher: Cipher
    lateinit var cryptoObject: FingerprintManagerCompat.CryptoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)


        if (!FingerPrintUtils.isSdkVersionSupported) {
            Toast.makeText(this,
                    "Finger only available in Android M or later",
                    Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!FingerPrintUtils.isHardwareSupported(this)) {
            Toast.makeText(this,
                    "Finger print hardware not available on this device",
                    Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!FingerPrintUtils.isFingerprintAvailable(this)) {
            Toast.makeText(this,
                    "Please register a fingerprint on your device first",
                    Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!FingerPrintUtils.isPermissionGranted(this)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.USE_FINGERPRINT),
                    PERMISSION_REQUEST)
        } else {
            initAuthentication()
            authenticate()
        }

    }

    private fun initAuthentication() {
        generateKey()
        initCipher()
        cryptoObject = FingerprintManagerCompat.CryptoObject(cipher)
    }

    private fun authenticate() {
        val fingerprintManagerCompat = FingerprintManagerCompat.from(this)
        fingerprintManagerCompat.authenticate(cryptoObject, 0, CancellationSignal(),

                object : FingerprintManagerCompat.AuthenticationCallback() {

                    override fun onAuthenticationError(errMsgId: Int,
                                                       errString: CharSequence?) {
                        super.onAuthenticationError(errMsgId, errString)
                        updateStatus(errString.toString())
                    }

                    override fun onAuthenticationHelp(helpMsgId: Int,
                                                      helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpMsgId, helpString)
                        updateStatus(helpString.toString())
                    }

                    override fun onAuthenticationSucceeded(
                            result: FingerprintManagerCompat.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        authSucceded()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        updateStatus(getString(R.string.fingerprint_failed))
                    }
                }, null)

    }

    private fun authSucceded() {
        Toast.makeText(this,
                "Finger print authentication successful",
                Toast.LENGTH_LONG).show()

        startActivity(Intent(this, ShopActivity::class.java))
        finish()
    }

    private fun updateStatus(status: String) {

    }

    private fun generateKey() {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore")
            keyGenerator.init(KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())

            keyGenerator.generateKey()

        } catch (exc: KeyStoreException) {
            printStackTrace(exc)
        } catch (exc: NoSuchAlgorithmException) {
            printStackTrace(exc)
        } catch (exc: NoSuchProviderException) {
            printStackTrace(exc)
        } catch (exc: InvalidAlgorithmParameterException) {
            printStackTrace(exc)
        } catch (exc: CertificateException) {
            printStackTrace(exc)
        } catch (exc: IOException) {
            printStackTrace(exc)
        }

    }

    private fun  printStackTrace(exc : Exception) {
        if (BuildConfig.DEBUG) {
            exc.printStackTrace();
        }
    }

    private fun initCipher(): Boolean {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES +
                    "/" + KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false

        } catch (e: KeyStoreException) {

            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>?,
                                            grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST) {
            for (i in 0 until permissions!!.size) {
                val permission = permissions[i]
                val grantResult = grantResults!![i]
                if (permission == android.Manifest.permission.USE_FINGERPRINT) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        initAuthentication()
                        authenticate()
                    } else {
                        ActivityCompat.requestPermissions(this,
                                arrayOf(android.Manifest.permission.USE_FINGERPRINT),
                                PERMISSION_REQUEST)
                    }
                }
            }
        }
    }
}