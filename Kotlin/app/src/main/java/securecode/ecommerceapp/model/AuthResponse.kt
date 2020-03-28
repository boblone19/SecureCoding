package securecodewarrior.ecommerceapp.model

import com.squareup.moshi.Json

data class AuthResponse(@field:Json(name = "user") val user: User,
                        @field:Json(name = "access_token") val accesToken: String,
                        @field:Json(name = "refresh_token") val refreshToken: String,
                        @field:Json(name = "pincode") val pincode: String)
