package com.laurentdarl.letschatapplication.presentation.fragments.registration

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private val dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://lets-chat-84356-default-rtdb.firebaseio.com/")
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)

        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun signUp() {
        val names = binding.tifNames.text.toString()
        val phone = binding.tifPhone.text.toString()
        val email = binding.tifEmail.text.toString().trim { it <= ' ' }
        val password = binding.tifPassword.text.toString().trim { it <= ' ' }
        val repeatPassword = binding.tifRepeatPassword.text.toString().trim { it <= ' ' }
        validateInput(names, phone, email, password, repeatPassword)
    }

    private fun validateInput(
        userName: String,
        phone: String,
        email: String,
        password: String,
        repeatPassword: String
    ) {
        when {
            TextUtils.isEmpty(userName) -> Toast.makeText(
                requireContext(),
                "Please fill in your full names",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                requireContext(),
                "Please enter a valid email address",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                requireContext(),
                "Please enter a password",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(repeatPassword) -> Toast.makeText(
                requireContext(),
                "Please confirm your password",
                Toast.LENGTH_SHORT
            ).show()
            repeatPassword != password -> Toast.makeText(
                requireContext(),
                "Passwords do not match, please check password",
                Toast.LENGTH_SHORT
            ).show()
            email.length <= 6 -> {
                Toast.makeText(
                    requireContext(),
                    "Email should exceed 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            }
            password.length <= 6 -> {
                Toast.makeText(
                    requireContext(),
                    "Password should exceed 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else -> {
                registerUser(userName, email, password, phone)
            }
        }
    }

    private fun registerUser(userName: String, email: String, password: String, phone: String) {
        showProgressDialog()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                hideProgressDialog()
                Toast.makeText(requireContext(), "Signed up successfully",
                    Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                val userId = user!!.uid
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["userId"] = userId
                hashMap["userName"] = userName
                hashMap["phone"] = phone
                hashMap["email"] = email
                hashMap["profileImage"] = ""
                databaseReference.setValue(hashMap).addOnCompleteListener { task ->
                    auth.signOut()
                    if (task.isSuccessful) {
                        hideProgressDialog()
                        loginAction()
                    }
                }
            } else {
                hideProgressDialog()
                Toast.makeText(
                    requireContext(),
                    task.exception!!.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loginAction() {
        val actions = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(actions)
    }

    private fun showProgressDialog() {
        val view = View.inflate(requireContext(), R.layout.progress_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun hideProgressDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(null)

        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}