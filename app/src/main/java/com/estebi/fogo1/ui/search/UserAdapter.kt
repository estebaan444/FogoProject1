package com.estebi.fogo1.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.estebi.fogo1.R
import com.estebi.fogo1.models.User
import com.squareup.picasso.Picasso

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserHolder>() {
    private lateinit var listener: onItemClickListener
    private var listData = listOf<User>()

    fun setListData(data: List<User>) {
        listData = data
    }

    fun filterList(filteredList: List<User>) {
        listData = filteredList
        notifyDataSetChanged()
    }

    interface onItemClickListener {
        fun onItemClick(user: User)
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return UserHolder(v)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = listData[position]
        holder.bindView(user)
    }



    inner class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(user: User) {
            val userName = itemView.findViewById<TextView>(R.id.textUsername)
            userName.text = "${user.userName}"


            val postImg = itemView.findViewById<ImageView>(R.id.userImageView)
            val userImg = "${user.userImg}";
            Picasso.get().load(userImg).into(postImg)


            itemView.setOnClickListener {
                listener.onItemClick(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            0
        }
    }
}