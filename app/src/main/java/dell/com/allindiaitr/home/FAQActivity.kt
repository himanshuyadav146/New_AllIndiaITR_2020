package dell.com.allindiaitr.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dell.com.allindiaitr.R
import dell.com.allindiaitr.adapter.FAQExpandableAdapter
import dell.com.allindiaitr.databinding.ActivityFaqBinding
import dell.com.allindiaitr.utils.ExpandableListDataPump
import java.util.HashMap

class FAQActivity : AppCompatActivity() {

    lateinit var binding:ActivityFaqBinding
    internal val prevExpandPosition = intArrayOf(-1)
    var expandableListTitle = arrayListOf<String>()
    lateinit var expandableListDetail : HashMap<String, List<String>>
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar as Toolbar?)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbarTitle.text = "FAQ"

        mContext = this

        binding.expandableListView.setOnGroupExpandListener { groupPosition ->
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition) {
                binding.expandableListView.collapseGroup(prevExpandPosition[0])
            }
            prevExpandPosition[0] = groupPosition
        }

        binding.expandableListView.setOnGroupCollapseListener {
        }

        binding.expandableListView.setOnChildClickListener { _, _, _, _, _ ->
            false
        }

        expandableListDetail = ExpandableListDataPump().getData()
        expandableListTitle = ArrayList<String>(expandableListDetail.keys)
        binding.expandableListView.setAdapter(FAQExpandableAdapter(this, expandableListTitle, expandableListDetail))
        binding.expandableListView.expandGroup(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item!!.itemId
        if (id == android.R.id.home){
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
