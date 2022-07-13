//package dell.com.allindiaitr.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import dell.com.allindiaitr.R
//import dell.com.allindiaitr.models.User
//
//class CustomAdapter(val userList: ArrayList<User>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>()  {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_view_languages, parent, false)
//        return ViewHolder(v)
//    }
//
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bindItems(userList[position])
//    }
//
//
//    //the class is hodling the list view
//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bindItems(user: User) {
//            val textViewName = itemView.findViewById(R.id.tv_language) as TextView
//            // val textViewAddress  = itemView.findViewById(R.id.textViewAddress) as TextView
//            textViewName.text = user.name
//            // textViewAddress.text = user.address
//        }
//    }
//}