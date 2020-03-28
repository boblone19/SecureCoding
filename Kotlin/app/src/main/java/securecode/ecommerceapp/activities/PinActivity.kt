package securecodewarrior.ecommerceapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.commonsware.cwac.saferoom.SafeHelperFactory
import kotlinx.android.synthetic.main.activity_pin.*
import securecodewarrior.ecommerceapp.BuildConfig
import securecodewarrior.ecommerceapp.R
import securecodewarrior.ecommerceapp.database.CipherDatabase

class PinActivity : AppCompatActivity() {

    lateinit var pinDisplay: TextView
    var pin = ""
    lateinit var pinForm: View

    companion object {
        const val REFRESH_TOKEN = "refresh_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        pinForm = pin_form
        pinDisplay = pin_display

        initPinUI()
    }

    protected fun resetSequence() {
        pin = ""
        pinDisplay.text = ""
        pinForm.visibility = View.VISIBLE
    }

    protected fun initPinUI() {
        val ids = intArrayOf(R.id.zero, R.id.one, R.id.two, R.id.three,
                R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine)
        for (i in 0..9) {
            val b = findViewById<View>(ids[i])
            b.setOnClickListener(PinButtonClickListener(i, this))
        }
        val backspace = findViewById<View>(R.id.backspace)
        backspace.setOnClickListener({ removeNumberFromPin() })
    }

    fun addNumberToPin(number: Int) {
        if (pin.length > 5) return
        pin += number
        addAsterixToDisplay()

        if (pin.length == 5) onPinInputCompleted()
    }

    protected fun removeNumberFromPin() {
        if (pin.length == 0) return
        pin = pin.substring(0, pin.length - 2)
        pinDisplay.text = ""
        for (i in 0 until pin.length)
            addAsterixToDisplay()
    }

    protected fun addAsterixToDisplay() {
        pinDisplay.text = pinDisplay.text.toString() + "*"
    }

    protected fun onPinInputCompleted() {

        var refreshToken = CipherDatabase.getInstance(this,
                SafeHelperFactory(pin.toCharArray()))
                .refreshTokenDAO().getLastToken()

        if (refreshToken == null) {
            if (BuildConfig.DEBUG) {
                Log.d("SCW", "Refresh token not valid");
            }
            setResult(Activity.RESULT_CANCELED)
        }

        setResult(Activity.RESULT_OK, Intent().putExtra(REFRESH_TOKEN, refreshToken.token))

        finish()
    }
}

class PinButtonClickListener(private val value: Int,
                             private val fingerPinActivity: PinActivity) : View.OnClickListener {

    override fun onClick(v: View) {
        fingerPinActivity.addNumberToPin(value)
    }
}