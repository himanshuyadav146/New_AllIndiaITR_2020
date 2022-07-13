//package dell.com.allindiaitr.adapter
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.content.ContextCompat.startActivity
//import androidx.recyclerview.widget.RecyclerView
//import dell.com.allindiaitr.Multilanguage.LanguageSettingActivity
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.holders.LanguageSettingHolder
//import dell.com.allindiaitr.home.DashboardActivity
//import dell.com.allindiaitr.home.MoreActivity
//import dell.com.allindiaitr.models.LanguageModel
//import dell.com.allindiaitr.utils.AppPreferences
//
//
//class LanguageAdapter(val mContext:Context,val languageList:List<LanguageModel>) :RecyclerView.Adapter<LanguageSettingHolder>() {
//    private var appPreferences: AppPreferences? = AppPreferences(mContext)
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageSettingHolder {
//
//        return LanguageSettingHolder(LayoutInflater.from(mContext).inflate(R.layout.list_view_languages, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return languageList.size
//    }
//
//    override fun onBindViewHolder(holder: LanguageSettingHolder, position: Int) {
//        holder.tv_name.text=languageList.get(position).language
//
//
//        holder.itemView.setOnClickListener {
//           // Toast.makeText(mContext,languageList.get(position).language,Toast.LENGTH_LONG).show()
//            if(appPreferences==null)
//            {
//                return@setOnClickListener
//            }
//            appPreferences!!.selectedLanguage=languageList.get(position).key
//           val i =Intent(mContext,DashboardActivity::class.java)
//            i.flags= Intent.FLAG_ACTIVITY_NEW_TASK
//            mContext.startActivity(i)
//
//        }
//    }
//}