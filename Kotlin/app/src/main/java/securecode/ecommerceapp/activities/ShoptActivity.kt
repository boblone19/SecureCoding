package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import securecodewarrior.ecommerceapp.ProductsRepository
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.adapters.ProductAdapter
import securecodewarrior.ecommerceapp.database.CipherDatabase
import securecodewarrior.ecommerceapp.model.Product
import securecodewarrior.ecommerceapp.model.Role

class ShopActivity : Activity() {

    lateinit var productsRequest : Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLogout.setOnClickListener { logout() }

        if (intent.getSerializableExtra(ROLE_KEY) == Role.SELLER) {
            sellProductButton.show()
            sellProductButton.setOnClickListener {
                startActivity(Intent(this, SellProductActivity::class.java)) }
        }

        productsRequest = ProductsRepository.instance.getProducts(object : ProductsRepository.ProductsCallback {
            override fun onSuccess(products: List<Product>) {
                displayProductList(products)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@ShopActivity,
                        "Error getting product list, please try again",
                        Toast.LENGTH_LONG).show()
            }
        }, this)
    }

    fun displayProductList(products: List<Product>) {
        list.adapter = ProductAdapter(products, this@ShopActivity)
    }

    fun logout() {
        var refreshTokenDAO = CipherDatabase.getInstance()!!.refreshTokenDAO()
        refreshTokenDAO.deleteToken(refreshTokenDAO.getLastToken())
        finish()
    }

    override fun onPause() {
        super.onPause()
        productsRequest.cancel()
    }

    companion object {
        const val ROLE_KEY = "role_key"
    }

}