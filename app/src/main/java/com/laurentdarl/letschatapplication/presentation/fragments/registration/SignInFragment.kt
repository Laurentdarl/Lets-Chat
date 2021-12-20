package com.laurentdarl.letschatapplication.presentation.fragments.registration

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.laurentdarl.letschatapplication.R
import com.laurentdarl.letschatapplication.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    companion object {
        const val DOMAIN_NAME = "gmail.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (user != null && !user.isAnonymous) {
            signedIn()
        }

    }

    private fun signedIn() {
        val actions = SignInFragmentDirections.actionSignInFragmentToChatFragment()
        findNavController().navigate(actions)
    }

    private fun signUpActions() {
        val actions = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(actions)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater)

        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        binding.tvSignUp.setOnClickListener {
            signUpActions()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun signIn() {
        val email = binding.tifEmail.text.toString().trim {it <= ' '}
        val password = binding.tifPassword.text.toString().trim {it <= ' '}
        validateUser(email, password)
    }

    private fun validateUser(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(
                    requireContext(),
                    "Email field is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(
                    requireContext(),
                    "Password field is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                signInUser(email, password)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signedIn()
                Toast.makeText(requireContext(), "Signed in successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    task.exception!!.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

