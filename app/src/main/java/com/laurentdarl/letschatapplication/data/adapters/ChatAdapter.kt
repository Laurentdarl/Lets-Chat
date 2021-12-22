package com.laurentdarl.letschatapplication.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.data.models.Chat
import com.laurentdarl.letschatapplication.databinding.ChatItemsBinding
import com.laurentdarl.letschatapplication.databinding.LeftChatBinding
import com.laurentdarl.letschatapplication.databinding.RightChatBinding
import com.laurentdarl.letschatapplication.presentation.fragments.home.ChatFragmentDirections
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(var context: Context?): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var user: FirebaseUser? = null
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private var chat = emptyList<Chat>()


    inner class ChatViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: CircleImageView = view.findViewById(R.id.chat_prof_img)
        val message: TextView = view.findViewById(R.id.chat_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.right_chat, parent, false)
            return ChatViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_chat, parent, false)
            return ChatViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chats = chat[position]

        holder.message.text = chats.message.toString()
//        Glide.with(context!!).load(chats.receiverId).placeholder(R.drawable.ic_profile24).into(holder.image)

    }

    override fun getItemCount(): Int {
        return chat.size
    }

    override fun getItemViewType(position: Int): Int {
        user = FirebaseAuth.getInstance().currentUser
        if (chat[position].senderId == user!!.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }

    fun setData(note: List<Chat>) {
        this.chat = note
        notifyDataSetChanged()
    }
}