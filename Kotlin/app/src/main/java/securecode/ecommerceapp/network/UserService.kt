package securecodewarrior.ecommerceapp.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import securecodewarrior.ecommerceapp.model.Product

interface UserService {

    @GET("/getProducts")
    fun getProducts(): Deferred<Response<List<Product>>>

    @FormUrlEncoded
    @POST("/sellProduct")
    fun sellProduct(@Field("productName") name: String,
                     @Field("productPrice") price : String)
            : Deferred<Response<Boolean>>
}