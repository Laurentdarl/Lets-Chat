package com.laurentdarl.letschatapplication.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.data.models.User
import com.laurentdarl.letschatapplication.databinding.ChatItemsBinding
import com.laurentdarl.letschatapplication.presentation.fragments.home.ChatFragmentDirections

class UserAdapter(var context: Context?, val clicker: (User) -> Unit): RecyclerView.Adapter<UserAdapter.ChatViewHolder>() {
    var user = emptyList<User>()

    inner class ChatViewHolder(private val binding: ChatItemsBinding):
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(user: User) {
            binding.tvChat.text = user.userName.toString()
            Glide.with(context!!).load(user.profileImage).placeholder(R.drawable.ic_person).into(binding.userImg)

            binding.root.setOnClickListener {
                user.let {
                    val actions = ChatFragmentDirections.actionChatFragmentToChatsFragment(user)
                    binding.root.findNavController().navigate(actions)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = ChatItemsBinding.inflate(LayoutInflater.from(parent.context))
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(user[position])
    }

    override fun getItemCount(): Int {
        return user.size
    }

    fun setData(note: List<User>) {
        this.user = note
        notifyDataSetChanged()
    }
}