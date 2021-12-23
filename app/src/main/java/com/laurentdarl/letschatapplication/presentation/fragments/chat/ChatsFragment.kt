package com.laurentdarl.letschatapplication.presentation.fragments.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.data.adapters.ChatAdapter
import com.laurentdarl.letschatapplication.data.models.Chat
import com.laurentdarl.letschatapplication.data.models.NotificationData
import com.laurentdarl.letschatapplication.data.models.PushNotification
import com.laurentdarl.letschatapplication.data.models.User
import com.laurentdarl.letschatapplication.databinding.FragmentChatsBinding
import com.laurentdarl.letschatapplication.domain.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    private var user = auth.currentUser
    private val navArgs by navArgs<ChatsFragmentArgs>()
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = ArrayList<Chat>()
    private var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(layoutInflater)

        chatAdapter = ChatAdapter(requireContext())
        binding.rvChatMessages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(navArgs.chats.userId.toString())

        displayProfilePic()
        binding.btnSendMsg.setOnClickListener {
            val message = binding.etSendMsg.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "No message input", Toast.LENGTH_SHORT).show()
                binding.etSendMsg.setText("")
            } else {
                sendMessage(auth.currentUser!!.uid, navArgs.chats.userId.toString(), message)
                binding.etSendMsg.setText("")
                topic = "/topics/${navArgs.chats.userId.toString()}"
                PushNotification(NotificationData(navArgs.chats.userName!!, message), topic)
                    .also {
                    sendNotification(it)
                }
            }

            readMessage(auth.currentUser!!.uid, navArgs.chats.userId.toString())
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun displayProfilePic() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.getValue(User::class.java)
                binding.chatUserName.text = users!!.userName.toString()
                if (users!!.profileImage == "") {
                    binding.chatProfImg.setImageResource(R.drawable.ic_profile24)
                } else {
                    Glide.with(requireContext()).load(users.profileImage).into(binding.chatProfImg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Could not load profile Image", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        val reference: DatabaseReference? = FirebaseDatabase.getInstance().reference
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message

        reference!!.child("Chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (auth.currentUser != null) {
                    chatList.clear()
                }

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                        chat.senderId.equals(receiverId) && chat.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                chatAdapter.setData(chatList)
                binding.rvChatMessages.apply {
                    hasFixedSize()
                    adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO)
        .launch {
            try {
                val response = RetrofitInstance.api.pushNotification(notification)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Response ${Gson().toJson(response)}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}