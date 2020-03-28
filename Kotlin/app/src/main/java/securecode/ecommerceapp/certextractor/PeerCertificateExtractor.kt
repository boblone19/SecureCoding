package securecodewarrior.ecommerceapp.certextractor

import android.annotation.SuppressLint
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.*


class PeerCertificateExtractor {

    /**
     * Get peer certificate(Public key to sha256 to base64)
     * @param certificate Crt or der or pem file with a valid certificate
     * @return
     */
    @SuppressLint("NewApi")
    fun extract(certificate: X509Certificate): String {

        try {
            val publicKeyEncoded = certificate.publicKey.encoded
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val publicKeySha256 = messageDigest.digest(publicKeyEncoded)
            val publicKeyShaBase64 = Base64.getEncoder().encode(publicKeySha256)

            return "sha256/" + String(publicKeyShaBase64)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

}