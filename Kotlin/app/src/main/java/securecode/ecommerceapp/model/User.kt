package securecodewarrior.ecommerceapp.model

import com.squareup.moshi.Json

data class User(@field:Json(name = "id") val id: String,
            @field:Json(name = "name") val name: String,
            @field:Json(name = "name") val role: Role)

enum class Role {
    BUYER, SELLER
}