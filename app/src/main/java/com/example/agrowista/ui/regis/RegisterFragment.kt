package com.example.agrowista.ui.regis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.FragmentRegisterBinding
import com.example.agrowista.ui.ViewModelFactory
import com.example.agrowista.R

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[RegisterViewModel::class.java]

        binding.loginText.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[RegisterViewModel::class.java]

        setupActions()
        setupObservers()
        genderDropdown()
    }

    private fun setupActions() {
        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val age = binding.ageEditText.text.toString()
            val gender = binding.genderAutoComplete.text.toString()
            val occupation = binding.occupationEditText.text.toString()
            val origin = binding.originEditText.text.toString()

            viewModel.register(
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                age = age,
                gender = gender,
                occupation = occupation,
                origin = origin
            )
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.registerButton.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registerSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    requireContext(),
                    "Registrasi berhasil! Silakan login.",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun genderDropdown() {
        val genderDropdown: AutoCompleteTextView = binding.genderAutoComplete
        val genderOptions = listOf("Laki-Laki", "Perempuan")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderDropdown.setAdapter(adapter)
        genderDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedGender = when (position) {
                0 -> "L"
                1 -> "P"
                else -> null
            }
            Log.d("RegisterFragment", "Selected gender: $selectedGender")
        }
    }
}