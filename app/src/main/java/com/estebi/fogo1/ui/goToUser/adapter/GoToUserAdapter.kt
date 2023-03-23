package com.estebi.fogo1.ui.goToUser.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.estebi.fogo1.R
import com.estebi.fogo1.models.Posts
import com.squareup.picasso.Picasso

class GoToUserAdapter : RecyclerView.Adapter<GoToUserAdapter.PostsHolder>() {
    private lateinit var listener: onItemClickListener
    private var listData = listOf<Posts>()

    fun setListData(data: List<Posts>) {
        listData = data
    }

    interface onItemClickListener {
        fun onItemClick(posts: Posts)
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_myposts, parent, false)
        return PostsHolder(v)
    }

    override fun onBindViewHolder(holder: PostsHolder, position: Int) {
        val posts = listData[position]
        holder.bindView(posts)
    }

    inner class PostsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(posts: Posts) {
            val tvTitle = itemView.findViewById<TextView>(R.id.idTVCourse)
            tvTitle.text = "${posts.titlePost}"

            val postImg = itemView.findViewById<ImageView>(R.id.idIVCourse)
            val img = "${posts.userImgPosts}";
            Picasso.get().load(img).into(postImg)
            itemView.setOnClickListener {
                listener.onItemClick(posts)
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