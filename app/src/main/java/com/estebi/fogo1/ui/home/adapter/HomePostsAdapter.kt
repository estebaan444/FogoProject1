package com.estebi.fogo1.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.estebi.fogo1.R
import com.estebi.fogo1.models.Posts
import com.squareup.picasso.Picasso

class HomePostsAdapter : RecyclerView.Adapter<HomePostsAdapter.PostsHolder>(){
    private lateinit var listener: onItemClickListener
    private var listData = listOf<Posts>()

    fun setListData(data:List<Posts>){
        listData = data
    }

    interface onItemClickListener {
        fun onItemClick(posts: Posts)
    }

    fun setItemListener(listener: onItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_posts, parent, false)
        return PostsHolder(v)
    }

    override fun onBindViewHolder(holder: PostsHolder, position: Int) {
        val posts = listData[position]
        holder.bindView(posts)
    }

    inner class PostsHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindView(posts: Posts) {
            val postUsername = itemView.findViewById<TextView>(R.id.textUsernamePost)
            postUsername.text = "${posts.userNamePosts}"

            val postUserImage = itemView.findViewById<ImageView>(R.id.userImagePost)
            val userImg = "${posts.userImgProfile}";
            Picasso.get().load(userImg).into(postUserImage)

            val titlePost = itemView.findViewById<TextView>(R.id.titlePost)
            titlePost.text = "${posts.titlePost}"

            val postImg = itemView.findViewById<ImageView>(R.id.imageViewPosts)
            val img = "${posts.userImgPosts}";
            Picasso.get().load(img).into(postImg)

            val postDesc = itemView.findViewById<TextView>(R.id.postDesc)
            postDesc.text = "${posts.postDesc}"

            itemView.setOnClickListener {
                listener.onItemClick(posts)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(listData.isNotEmpty()){
            listData.size
        } else{ 0 }
    }
}