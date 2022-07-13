package dell.com.allindiaitr.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dell.com.allindiaitr.R
import dell.com.allindiaitr.databinding.CardOptionsBinding
import dell.com.allindiaitr.holders.SourceOfIncomeHolder
import dell.com.allindiaitr.models.ITROption_Model
import dell.com.allindiaitr.models.NewItrBase

class SourceOfIncomeAdapter(): RecyclerView.Adapter<SourceOfIncomeHolder>() {

    lateinit var mContext: Context
    lateinit var mModelList: List<ITROption_Model>
    var newItrBase = NewItrBase.instance

    constructor(mContext: Context, mModelList: List<ITROption_Model>) : this() {
        this.mContext = mContext
        this.mModelList = mModelList
        this.newItrBase.isSalary = "false"
        this.newItrBase.isHouseProperty = "false"
        this.newItrBase.isBusiness = "false"
        this.newItrBase.isCapitalGain = "false"
        this.newItrBase.isOtherSource = "false"
        this.newItrBase.isForeignIncome = "false"
    }

    constructor(mContext: Context, mModelList: List<ITROption_Model>, newItrBase: NewItrBase) : this() {
        this.mContext = mContext
        this.mModelList = mModelList
        this.newItrBase.isSalary = newItrBase.isSalary
        this.newItrBase.isHouseProperty = newItrBase.isHouseProperty
        this.newItrBase.isBusiness = newItrBase.isBusiness
        this.newItrBase.isCapitalGain = newItrBase.isCapitalGain
        this.newItrBase.isOtherSource = newItrBase.isOtherSource
        this.newItrBase.isForeignIncome = newItrBase.isForeignIncome
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SourceOfIncomeHolder {
//        return SourceOfIncomeHolder(LayoutInflater.from(mContext).inflate(R.layout.card_options, p0, false))
    val binding= CardOptionsBinding.inflate(LayoutInflater.from(mContext),p0,false)
        return SourceOfIncomeHolder(binding)
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    override fun onBindViewHolder(p0: SourceOfIncomeHolder, p1: Int) {
        mModelList[0].isSelected = newItrBase.isSalary == "true"
        mModelList[1].isSelected = newItrBase.isHouseProperty == "true"
        mModelList[2].isSelected = newItrBase.isBusiness == "true"
        mModelList[3].isSelected = newItrBase.isCapitalGain == "true"
        mModelList[4].isSelected = newItrBase.isOtherSource == "true"
        mModelList[5].isSelected = newItrBase.isForeignIncome == "true"

        p0.income_source_textView.text = mModelList[p1].text
        p0.income_cource_imageView.setImageResource(mModelList[p1].image!!)
        if (mModelList[p1].isSelected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                p0.income_source_textView.setTextColor(mContext.getColor(R.color.white))
                p0.card_options.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary))
                p0.income_cource_imageView.setColorFilter(mContext.getColor(R.color.white))
            } else {
                p0.income_source_textView.setTextColor(Color.parseColor("#FFFFFF"))
                p0.card_options.setCardBackgroundColor(Color.parseColor("#00B894"))
                p0.income_cource_imageView.setColorFilter(Color.parseColor("#FFFFFF"))
            }
            p0.income_source_textView.typeface = Typeface.DEFAULT_BOLD
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                p0.income_source_textView.setTextColor(mContext.getColor(R.color.dark_grey))
                p0.card_options.setCardBackgroundColor(mContext.getColor(R.color.white))
                p0.income_cource_imageView.setColorFilter(mContext.getColor(R.color.dark_grey))
            } else {
                p0.income_source_textView.setTextColor(Color.parseColor("#777777"))
                p0.card_options.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                p0.income_cource_imageView.setColorFilter(Color.parseColor("#777777"))
            }
            p0.income_source_textView.typeface = Typeface.DEFAULT
        }

        p0.itemView.setOnClickListener {
            mModelList[p1].isSelected= !mModelList[p1].isSelected
            if (mModelList[p1].isSelected) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    p0.income_source_textView.setTextColor(mContext.getColor(R.color.white))
                    p0.card_options.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary))
                    p0.income_cource_imageView.setColorFilter(mContext.getColor(R.color.white))
                } else {
                    p0.income_source_textView.setTextColor(Color.parseColor("#FFFFFF"))
                    p0.card_options.setCardBackgroundColor(Color.parseColor("#00B894"))
                    p0.income_cource_imageView.setColorFilter(Color.parseColor("#FFFFFF"))
                }
                p0.income_source_textView.typeface = Typeface.DEFAULT_BOLD
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    p0.income_source_textView.setTextColor(mContext.getColor(R.color.dark_grey))
                    p0.card_options.setCardBackgroundColor(mContext.getColor(R.color.white))
                    p0.income_cource_imageView.setColorFilter(mContext.getColor(R.color.dark_grey))
                } else {
                    p0.income_source_textView.setTextColor(Color.parseColor("#777777"))
                    p0.card_options.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    p0.income_cource_imageView.setColorFilter(Color.parseColor("#777777"))
                }
                p0.income_source_textView.typeface = Typeface.DEFAULT
            }
        }
    }
}