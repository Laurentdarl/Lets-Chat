package com.laurentdarl.letschatapplication.presentation.fragments.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.laurentdarl.letschatapplication.data.models.User
import com.laurentdarl.letschatapplication.data.models.adapters.ChatAdapter
import com.laurentdarl.letschatapplication.databinding.FragmentChatBinding
import com.laurentdarl.letschatapplication.R

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private var auth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private var user = auth.currentUser
    private val authUI = AuthUI.getInstance()
    private val userList = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(layoutInflater)

        getUsersList()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileFragment -> {
                item.onNavDestinationSelected(
                    findNavController()
                ) || super.onOptionsItemSelected(item)
            }
            R.id.sign_out -> {
                signOut()
                user?.reload()
                user = FirebaseAuth.getInstance().currentUser
            }
            else -> return true
        }
        return true
    }

    private fun signOut() {
        authUI.signOut(requireContext())
            .addOnCompleteListener { task ->
                loginAction()
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "logged out successfully",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginAction() {
        val actions = ChatFragmentDirections.actionChatFragmentToSignInFragment()
        findNavController().navigate(actions)
    }

    private fun getUsersList() {
        val firebaseUser = auth.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.image == "") {
//                    binding.profImg.setImageResource(R.drawable.placeholder_profile)
                }else {
//                    Glide.with(requireContext()).load(currentUser.image).into(binding.profImg)
                }

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (!user!!.userId.equals(firebaseUser!!.uid)) {
                        userList.add(user)
                    }
                }
                chatAdapter = ChatAdapter(requireContext(), userList)
                binding.rvChats.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    hasFixedSize()
                    adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}