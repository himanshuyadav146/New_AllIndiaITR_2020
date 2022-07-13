package dell.com.allindiaitr.submitDocument

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivityFailPaymentBinding

class FailPaymentActivity : AppCompatActivity() {

    lateinit var mContext: Context
    private lateinit var binding:ActivityFailPaymentBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFailPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "Payment Failed"

        mContext = this

        if (intent.extras?.getString("transactionId")?.length!! > 30) {
            binding.transactionIdTextView.text = "${intent.extras?.getString("transactionId")?.toString()?.substring(0, 25)} " + "\n" +
                    "${intent.extras?.getString("transactionId")?.toString()?.substring(25, intent.extras?.getString("transactionId")?.length!!)}"

            binding.orderIdTextView.text = "${intent.extras?.getString("orderId")?.toString()?.substring(0, 25)} " + "\n" +
                    "${intent.extras?.getString("orderId")?.toString()?.substring(25, intent.extras?.getString("orderId")?.length!!)}"

        } else {
            binding.transactionIdTextView.text = intent.extras?.getString("transactionId")
            binding.orderIdTextView.text = intent.extras?.getString("orderId")
        }
        binding.amountTextView.text = "\u20B9 " + intent.extras?.getString("amount")

        binding.retryPaymentButton.setOnClickListener {

            val intent = Intent(this, PaymentActivity::class.java)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            startActivity(intent)
            finish()


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

}
