package securecodewarrior.ecommerceapp.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import securecodewarrior.ecommerceapp.model.AuthResponse

interface AuthService {

    @FormUrlEncoded
    @POST("/auth")
    fun getUser(
            @Field("username") email: String,
            @Field("password") password: String)
            : Deferred<Response<AuthResponse>>

    @FormUrlEncoded
    @POST("/refreshToken")
    fun refreshToken(@Field("refresh_token") refreshToken: String): Response<AuthResponse>
}