package com.example.agrowista.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.FragmentAccountBinding
import com.example.agrowista.ui.ViewModelFactory
import com.example.agrowista.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[AccountViewModel::class.java]

        setupObservers()
        fetchData()
        setupActions()

        viewLifecycleOwner.lifecycleScope.launch {
            val isLoggedIn = userPreferences.isUserLoggedIn().first()
            if (!isLoggedIn) {
                userNotLoggedIn()
                backToLogin()
            } else {
                binding.btnLogout.visibility = View.VISIBLE
            }
        }
    }

    private fun userNotLoggedIn(){
        binding.btnLogout.visibility = View.GONE
        binding.tvName.text = getString(R.string.tamu)
        binding.tvOccupation.text = getString(R.string.lainnya)
        binding.tvTotalPoints.text = "0"
        binding.tvAge.text = getString(R.string.lainnya)
        binding.tvGender.text = getString(R.string.lainnya)
        binding.tvOrigin.text = getString(R.string.lainnya)
        binding.tvEmail.text = getString(R.string.lainnya)
        binding.btnBackToLogin.visibility = View.VISIBLE
    }

    private fun backToLogin() {
        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToLoginFragment())
        }
    }

    private fun setupObservers() {
        viewModel.accountData.observe(viewLifecycleOwner) { response ->
            response.data?.let { data ->
                binding.apply {
                    tvName.text = data.dataDiri?.nama?:"Tamu"
                    tvOccupation.text = data.dataDiri?.pekerjaan?:getString(R.string.lainnya)
                    tvTotalPoints.text = data.totalPoint?.toString()?:"0"
                    tvAge.text = data.dataDiri?.umur?.toString()?:getString(R.string.lainnya)
                    tvGender.text = data.dataDiri?.jenisKelamin?:getString(R.string.lainnya)
                    if (data.dataDiri?.jenisKelamin == "L") {
                        tvGender.text = getString(R.string.laki)
                    } else if (data.dataDiri?.jenisKelamin == "P") {
                        tvGender.text = getString(R.string.perempuan)
                    } else {
                        tvGender.text = getString(R.string.lainnya)
                    }
                    tvOrigin.text = data.dataDiri?.asal?:getString(R.string.lainnya)
                    tvEmail.text = data.email?:getString(R.string.lainnya)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchData() {
        viewModel.getUserId().observe(viewLifecycleOwner) { userId ->
            if (userId.isNotEmpty()) {
                viewModel.getAccountData(userId)
            }
        }
    }

    private fun setupActions() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToLoginFragment())
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
        }
    }
}