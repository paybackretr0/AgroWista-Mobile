package com.example.agrowista.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.FragmentLoginBinding
import com.example.agrowista.ui.ViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[LoginViewModel::class.java]


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.registerText.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                binding.loginButton.isEnabled = false
                viewModel.login(email, password)
            }
        }

        binding.actionHomeText.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            binding.loginButton.isEnabled = true

            result.onSuccess { authResponse ->
                if (!isAdded) return@onSuccess
                Toast.makeText(requireContext(), authResponse.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            }.onFailure { throwable ->
                if (!isAdded) return@onFailure
                Toast.makeText(requireContext(), throwable.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.emailInputLayout.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                binding.passwordInputLayout.error = "Password is required"
                false
            }
            else -> {
                binding.emailInputLayout.error = null
                binding.passwordInputLayout.error = null
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

