package securecodewarrior.ecommerceapp.model

import com.squareup.moshi.Json

data class Product(@field:Json(name = "id") val id: String,
                   @field:Json(name = "name") val name: String,
                   @field:Json(name = "price") val price: Float)
