package dell.com.allindiaitr.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.HtmlCompat
import dell.com.allindiaitr.R
import dell.com.allindiaitr.submitDocument.StatusUpdateDetailsActivity
import dell.com.allindiaitr.models.NewItrBase

@Suppress("NAME_SHADOWING")
class ITRStatusAdapter(private val mContext: Context,
                       private val expandable_list_view: ExpandableListView,
                       private val efilingStatusesList: List<NewItrBase>,
                       private val efilingStatusFieldList: List<NewItrBase>): BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return efilingStatusesList[groupPosition].status!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val listTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val layoutInflater = this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.status_parent, null)
        }
        var img_companyreg = convertView?.findViewById<ImageView>(R.id.img_companyreg)
        var view_parent_line = convertView?.findViewById<View>(R.id.view_parent_line)
        var process_title = convertView?.findViewById<TextView>(R.id.process_title)

        process_title?.text = listTitle

        if (efilingStatusesList[groupPosition].isActivate == true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    img_companyreg?.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.colorPrimary))
                    view_parent_line?.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.colorPrimary))
                }
            }
        }

        if (efilingStatusesList[groupPosition].description!!.isNotEmpty() && efilingStatusesList[groupPosition].isActivate == true){
            expandable_list_view.expandGroup(groupPosition)
            convertView?.isClickable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    img_companyreg?.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.colorPrimary))
                }
                if (expandable_list_view.isGroupExpanded(groupPosition)) {
                    process_title?.typeface = Typeface.DEFAULT_BOLD
                    process_title?.textSize = 16F
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view_parent_line?.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.lighter_grey))
                    }
                }
                else {
                    process_title?.typeface = Typeface.DEFAULT
                    process_title?.textSize = 14F
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view_parent_line?.backgroundTintList = ColorStateList.valueOf(mContext.getColor(R.color.colorPrimary))
                    }
                }
            }
        }
        else {
            expandable_list_view.collapseGroup(groupPosition)
            convertView?.isClickable = false
            process_title?.typeface = Typeface.DEFAULT
            process_title?.textSize = 14F
        }


        if (groupPosition == efilingStatusesList.size -1){
            view_parent_line?.visibility = View.INVISIBLE
        }else {
            view_parent_line?.visibility = View.VISIBLE
        }


        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (this.efilingStatusesList[groupPosition].description == null) {
            0
        } else {
            1
        }
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return efilingStatusesList[groupPosition].description!!
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        if (convertView == null) {
            val layoutInflater = this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.status_child, null)
        }

        var process_decription = convertView?.findViewById<TextView>(R.id.process_decription)
        process_decription?.text = efilingStatusesList[groupPosition].description
        var field_list = convertView?.findViewById<LinearLayout>(R.id.field_list)
        var update_details = convertView?.findViewById<Button>(R.id.update_details)
        var view_child_line = convertView?.findViewById<View>(R.id.view_child_line)

        if (efilingStatusesList[groupPosition].status.equals("Review Information") && efilingStatusesList[groupPosition].isActivate == true){
            process_decription?.text = "Thank you for providing the details to us. Our Tax Experts will review them and will contact you for additional details."
            for (i in 0 until efilingStatusFieldList.size) {
                if (efilingStatusFieldList.isEmpty()){
                    field_list?.visibility = View.GONE
                    update_details?.visibility = View.GONE
                    process_decription?.text = efilingStatusesList[groupPosition].description
                }
                else {
                    field_list?.visibility = View.VISIBLE
                    update_details?.visibility = View.VISIBLE
                    process_decription?.text = "Some of the information mentioned-below is incorrect. Kindly update."
                    var tv = TextView(mContext)
                    tv.text = HtmlCompat.fromHtml("&#9670;  ${efilingStatusFieldList[groupPosition].label}", HtmlCompat.FROM_HTML_MODE_LEGACY)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tv.setTextColor(mContext.getColor(R.color.lighter_black))
                    };
                    tv.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                    field_list?.addView(tv)
                    update_details?.setOnClickListener {
                        val intent = Intent(mContext, StatusUpdateDetailsActivity::class.java)
                        mContext.startActivity(intent)
                    }
                }
            }
        }

        if (groupPosition == efilingStatusesList.size -1){
            view_child_line?.visibility = View.INVISIBLE
        }else {
            view_child_line?.visibility = View.VISIBLE
        }

        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return efilingStatusesList.size
    }

}