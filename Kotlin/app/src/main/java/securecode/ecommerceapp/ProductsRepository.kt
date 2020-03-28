package securecodewarrior.ecommerceapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import securecodewarrior.ecommerceapp.model.Product
import securecodewarrior.ecommerceapp.network.SCWRetrofitFactory
import securecodewarrior.ecommerceapp.utils.DeviceID


class ProductsRepository {

    companion object {
        val instance: ProductsRepository by lazy {
            ProductsRepository()
        }
    }

    fun getProducts(callback : ProductsCallback, context : Context) : Job {
            val deviceID = DeviceID.getDeviceId(context)
            val service = SCWRetrofitFactory.makeUserService(deviceID, context)
            return GlobalScope.launch(Dispatchers.Main) {
                val request = service.getProducts()
                val response = request.await()
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Error ${response.code()}")
                }
            }
    }

    fun sellProducts(callback : SellProductCallback, context : Context
                     , name : String, price : String) : Job {
        val deviceID = DeviceID.getDeviceId(context)
        val service = SCWRetrofitFactory.makeUserService(deviceID, context)
        return GlobalScope.launch(Dispatchers.Main) {
            val request = service.sellProduct(name, price)
            val response = request.await()
            if (response.isSuccessful) {
                callback.onSuccess()
            } else {
                callback.onError("Error ${response.code()}")
            }
        }
    }

    interface ProductsCallback {
        fun onSuccess(products: List<Product>)
        fun onError(errorMessage: String)
    }

    interface SellProductCallback {
        fun onSuccess()
        fun onError(errorMessage: String)
    }

}