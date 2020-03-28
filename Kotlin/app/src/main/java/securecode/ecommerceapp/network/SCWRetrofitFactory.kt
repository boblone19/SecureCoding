package securecodewarrior.ecommerceapp.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import securecodewarrior.ecommerceapp.BuildConfig
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.certextractor.PeerCertificateExtractor
import securecodewarrior.ecommerceapp.database.CipherDatabase
import securecodewarrior.ecommerceapp.database.RefreshToken
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateFactory
import java.security.cert.CertificateNotYetValidException
import java.security.cert.X509Certificate


object SCWRetrofitFactory {

    const val BASE_URL = "https://scw.com"
    const val DOMAIN = "scw.com"


    fun makeAuthService(context: Context): AuthService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(createSSLPinningOkHttp(context).build())
                .build()
                .create(AuthService::class.java)

    }

    fun createSSLPinningOkHttp(context: Context): OkHttpClient.Builder {
        val x509Certificate = CertificateFactory.getInstance("X509")
                .generateCertificate(
                        context.resources.openRawResource(R.raw.certificate)
                ) as X509Certificate

        try {
            x509Certificate.checkValidity()
        } catch (e: CertificateExpiredException) {
            throw RuntimeException("Certificate expired")
        } catch (e: CertificateNotYetValidException) {
            throw RuntimeException("Invalid certificate")
        }

        val peerCertificate = PeerCertificateExtractor().extract(x509Certificate)

        return OkHttpClient.Builder().certificatePinner(
                CertificatePinner.Builder().add(DOMAIN, peerCertificate).build()
        )
    }


    fun makeUserService(deviceID: String, context: Context): UserService {

        var okHttpClient = createSSLPinningOkHttp(context)

        okHttpClient.authenticator(TokenAuthenticator(deviceID, context))

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptor)
        }

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(UserService::class.java)
    }
}

class TokenAuthenticator(val deviceID: String, val context: Context) : Authenticator {

    override fun authenticate(route: Route, response: Response): Request? {

        val refreshToken = CipherDatabase.getInstance()!!.refreshTokenDAO().getLastToken()

        val authTokens = SCWRetrofitFactory.makeAuthService(context)
                .refreshToken(refreshToken.token)

        CipherDatabase.getInstance()!!.refreshTokenDAO().insertToken(
                RefreshToken(authTokens.body()!!.refreshToken, System.currentTimeMillis()))

        return response.request().newBuilder()
                .header("ACCESS_TOKEN", authTokens.body()?.accessToken)
                .header("DEVICE_ID", deviceID)
                .build();
    }
}




