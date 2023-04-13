package com.estebi.fogo1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.estebi.fogo1.databinding.FragmentHomeBinding
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.repository.posts.PostsRepository
import com.estebi.fogo1.ui.home.adapter.HomePostsAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeAdapter = HomePostsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.progressBar.visibility = View.VISIBLE

        val homeRecyclerView = binding.recyclerHomePosts
        homeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext()/*, LinearLayoutManager.HORIZONTAL, false*/)
        homeRecyclerView.setHasFixedSize(true)
        homeRecyclerView.adapter = homeAdapter
        homeAdapter.setItemListener(object : HomePostsAdapter.onItemClickListener {
            override fun onItemClick(posts: Posts) {
                val postDesc = posts.postDesc
                val builder = AlertDialog.Builder(requireContext())
                with(builder)
                {
                    setTitle("The recipe of ${posts.titlePost}")
                    setMessage(postDesc)
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        })
        observeHomePosts()
        return root
    }

    private fun observeHomePosts() {
        PostsRepository.getPostsList().observe(requireActivity()) { postsList ->
            homeAdapter.setListData(postsList)
            homeAdapter.notifyDataSetChanged()
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}