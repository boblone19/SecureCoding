package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sell_product.*
import kotlinx.coroutines.Job
import securecodewarrior.ecommerceapp.ProductsRepository
import securecodewarrior.ecommerceapp.R


class SellProductActivity : Activity() {

    lateinit var sellProductRequest: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_product)

        Handler().post { setKeyboardShownListener() }

        sellProductButton.setOnClickListener { sellProduct() }
    }

    fun setKeyboardShownListener() {
        content.getViewTreeObserver().addOnGlobalLayoutListener {
            val r = Rect()
            content.getWindowVisibleDisplayFrame(r)
            val screenHeight = content.getRootView().getHeight()

            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                onKeyboardShowHide(true)
            } else {
                onKeyboardShowHide(false)
            }
        }
    }

    fun onKeyboardShowHide(visible: Boolean) {
        if (visible && hasThirdPartyKeyboard()) {
            Toast.makeText(this,
                    "Third party keyboards not allowed during payment",
                    Toast.LENGTH_SHORT).show()
            closeKeyboard()
        }
    }

    private fun hasThirdPartyKeyboard(): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        for (inputMethodInfo in imm.enabledInputMethodList) {
            if (inputMethodInfo.id == Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.DEFAULT_INPUT_METHOD)) {
                if (inputMethodInfo.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun closeKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    private fun sellProduct() {
        val name = textInputProductName.editText?.text.toString()
        val price = textInputProductPrice.editText?.text.toString()

        sellProductRequest = ProductsRepository.instance.sellProducts(
                object : ProductsRepository.SellProductCallback {
                    override fun onSuccess() {
                        Toast.makeText(this@SellProductActivity,
                                "Product posted successfully",
                                Toast.LENGTH_LONG).show()
                        finish()
                    }

                    override fun onError(errorMessage: String) {
                        Toast.makeText(this@SellProductActivity,
                                "Error getting product list, please try again",
                                Toast.LENGTH_LONG).show()
                    }

                }, this, name, price)

    }

    override fun onPause() {
        super.onPause()
        sellProductRequest.cancel()
    }
}