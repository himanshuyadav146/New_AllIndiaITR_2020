package dell.com.allindiaitr.sliderscreen

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dell.com.allindiaitr.login.LogInActivity
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.ActivitySliderBinding

class SliderActivity : AppCompatActivity() {

    private var introViewPagerAdapter: IntroScreenViewPagerAdapter? = null
    private var introBullets: Array<TextView>? = null
    private var introSliderLayouts: IntArray? = null
    private var runOnce: RunOnce? = null
    private lateinit var binding:ActivitySliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runOnce = RunOnce(this)
        if (!runOnce!!.isFirstTimeLaunch) {
            applicationStartup()
            finish()
        }

        //Get the intro slides
        introSliderLayouts = intArrayOf(
            R.layout.slider_one,
            R.layout.slider_two,
            R.layout.slider_three)
        makeIIntroBullets(0)

        introViewPagerAdapter = IntroScreenViewPagerAdapter()
        binding.siderViewpager!!.adapter = introViewPagerAdapter
        binding.siderViewpager!!.addOnPageChangeListener(introViewPagerListener)

        (binding.skipNextButton as View?)!!.setOnClickListener {
            val current = getItem(+1)
            if (current < introSliderLayouts!!.size) {
                binding.siderViewpager!!.currentItem = current
            } else {
                applicationStartup()
            }
        }
        setTransparentStatusBar()
    }

    private fun makeIIntroBullets(currentPage: Int) {
        var arraySize = introSliderLayouts!!.size
        introBullets = Array<TextView>(arraySize) { textboxInit() }
        val colorsActive = resources.getIntArray(R.array.array_intro_bullet_active)
        val colorsInactive = resources.getIntArray(R.array.array_intro_bullet_inactive)
        binding.sliderDots!!.removeAllViews()
        for (i in 0 until introBullets!!.size) {
            introBullets!![i] = TextView(this)
            introBullets!![i].text = HtmlCompat.fromHtml("   &#9679;   ", HtmlCompat.FROM_HTML_MODE_LEGACY)
            introBullets!![i].textSize = 17F
            introBullets!![i].setTextColor(colorsInactive[currentPage])
            binding.sliderDots!!.addView(introBullets!![i])
        }
        if (introBullets!!.isNotEmpty())
            introBullets!![currentPage].setTextColor(colorsActive[currentPage])
    }

    private fun textboxInit(): TextView {
        return TextView(applicationContext)
    }

    private fun getItem(i: Int): Int {
        return binding.siderViewpager!!.currentItem + i
    }

    private fun applicationStartup() {
        runOnce!!.isFirstTimeLaunch = false
        startActivity(Intent(this@SliderActivity, LogInActivity::class.java))
        finish()
    }

    private var introViewPagerListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            makeIIntroBullets(position)
            /*Based on the page position change the button text*/
        }
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            //Do nothing for now
        }
        override fun onPageScrollStateChanged(arg0: Int) {
            //Do nothing for now
        }
    }

    private fun setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        else {
            val window = window
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    inner class IntroScreenViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext)
            val view = layoutInflater.inflate(introSliderLayouts!![position], container, false)
            container.addView(view)
            return view
        }
        override fun getCount(): Int {
            return introSliderLayouts!!.size
        }
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}
