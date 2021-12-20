package com.laurentdarl.letschatapplication.data.models.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.data.models.User
import com.laurentdarl.letschatapplication.databinding.ChatItemsBinding

class ChatAdapter(val context: Context, val user: ArrayList<User>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(private val binding: ChatItemsBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvChat.text = user.userName.toString()
            Glide.with(context).load(user.image).placeholder(R.drawable.ic_person).into(binding.userImg)
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
}