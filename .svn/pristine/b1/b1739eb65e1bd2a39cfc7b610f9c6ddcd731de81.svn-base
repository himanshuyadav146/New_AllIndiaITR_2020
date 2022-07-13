package dell.com.allindiaitr.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import dell.com.allindiaitr.R
import java.util.HashMap

@Suppress("NAME_SHADOWING")
class FAQExpandableAdapter(private val mContext: Context,
                           private val expandableListTitle: List<String>,
                           private val expandableListDetail: HashMap<String, List<String>>): BaseExpandableListAdapter(){

    override fun getGroup(groupPosition: Int): Any {
        return this.expandableListTitle[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView: View? = convertView
        val listTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val layoutInflater = this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = convertView?.findViewById(R.id.listTitle) as TextView
        listTitleTextView.text = listTitle

        if (isExpanded) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(mContext.getColor(R.color.lighter_grey))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(mContext.getColor(R.color.white))
            }
        }

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.expandableListDetail[this.expandableListTitle[groupPosition]]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.expandableListDetail[this.expandableListTitle[groupPosition]]!![childPosition]
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
        val expandedListText = getChild(groupPosition, childPosition) as String
        if (convertView == null) {
            val layoutInflater = this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val expandedListTextView = convertView?.findViewById(R.id.expandedListItem) as TextView
        expandedListTextView.text = expandedListText
        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return this.expandableListTitle.size
    }
}