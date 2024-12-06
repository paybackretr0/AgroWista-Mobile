package com.example.agrowista.ui.ulasan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.DataItem
import com.example.agrowista.databinding.FragmentUlasanBinding
import com.example.agrowista.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UlasanFragment : Fragment() {

    private lateinit var binding: FragmentUlasanBinding
    private lateinit var viewModel: UlasanViewModel
    private var jenisWisataList = mutableListOf<DataItem>()
    private var selectedWisataId: Int? = null
    private var selectedGender: Char? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUlasanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[UlasanViewModel::class.java]

        genderDropdown()
        loadUserProfile()
        loadJenisWisata()

        viewLifecycleOwner.lifecycleScope.launch {
            val isLoggedIn = userPreferences.isUserLoggedIn().first()
            if (!isLoggedIn) {
                enableGuestFields()
                sendReviewAsGuest()
            } else {
                setupReviewSubmission()
                binding.loginBanner.visibility = View.GONE
            }
        }
    }

    private fun enableGuestFields() {
        binding.apply {
            etNama.isEnabled = true
            etUmur.isEnabled = true
            etAsal.isEnabled = true
            etPekerjaan.isEnabled = true
            genderAutoComplete.isEnabled = true
            binding.loginBanner.visibility = View.VISIBLE
            binding.btnLogin.setOnClickListener {
                findNavController().navigate(UlasanFragmentDirections.actionUlasanFragmentToLoginFragment())
            }
        }
    }

    private fun loadJenisWisata() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = viewModel.getJenisWisata()
                response.data?.let { dataItems ->
                    val filteredItems = dataItems.filterNotNull()

                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        filteredItems.map { it.namaWisata ?: "" }
                    )
                    binding.etJenisWisata.setAdapter(adapter)

                    jenisWisataList.clear()
                    jenisWisataList.addAll(filteredItems)

                    binding.etJenisWisata.setOnItemClickListener { _, _, position, _ ->
                        selectedWisataId = jenisWisataList[position].id
                    }
                }
            } catch (e: Exception) {
                Log.e("UlasanFragment", "Error loading jenis wisata: ${e.message}")
                Snackbar.make(binding.root, "Gagal memuat jenis wisata", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun genderDropdown() {
        val genderDropdown: AutoCompleteTextView = binding.genderAutoComplete
        val genderOptions = listOf("Laki-Laki", "Perempuan")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderDropdown.setAdapter(adapter)
        genderDropdown.setOnItemClickListener { _, _, position, _ ->
             selectedGender = when (position) {
                0 -> 'L'
                1 -> 'P'
                else -> null
            }
            Log.d("Ulasan", "Selected gender: $selectedGender")
        }
    }

    private fun loadUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = viewModel.getUserId()
                val response = viewModel.getUserProfile(userId)

                response.data?.dataDiri?.let { dataDiri ->
                    binding.apply {
                        etNama.setText(dataDiri.nama)
                        etUmur.setText(dataDiri.umur?.toString())
                        etAsal.setText(dataDiri.asal)
                        etPekerjaan.setText(dataDiri.pekerjaan)

                        val genderText = if (dataDiri.jenisKelamin == "L") "Laki-Laki" else "Perempuan"
                        genderAutoComplete.setText(genderText, false)

                        etNama.isEnabled = false
                        etUmur.isEnabled = false
                        etAsal.isEnabled = false
                        etPekerjaan.isEnabled = false
                        genderAutoComplete.isEnabled = false
                    }
                } ?: run {
                    Snackbar.make(binding.root, "Data profil tidak ditemukan", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("UlasanFragment", "Error loading profile: ${e.message}")
            }
        }
    }

    private fun setupReviewSubmission() {
        binding.btnKirimUlasan.setOnClickListener {
            val ulasan = binding.etUlasan.text.toString()

            if (selectedWisataId == null) {
                Snackbar.make(binding.root, "Pilih jenis wisata terlebih dahulu", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ulasan.isBlank()) {
                Snackbar.make(binding.root, "Ulasan tidak boleh kosong", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = viewModel.sendReview(selectedWisataId!!, ulasan)
                    Snackbar.make(binding.root, "Ulasan berhasil dikirim", Snackbar.LENGTH_SHORT).show()
                    binding.etUlasan.text?.clear()
                    binding.etJenisWisata.text?.clear()
                } catch (e: Exception) {
                    Log.e("UlasanFragment", "Error sending review: ${e.message}")
                    Snackbar.make(binding.root, "Gagal mengirim ulasan", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendReviewAsGuest(){
        binding.btnKirimUlasan.setOnClickListener {
            val nama = binding.etNama.text.toString()
            val umur = binding.etUmur.text.toString()
            val asal = binding.etAsal.text.toString()
            val pekerjaan = binding.etPekerjaan.text.toString()
            val jenis_kelamin = selectedGender.toString()
            val ulasan = binding.etUlasan.text.toString()

            if(asal.isBlank() || pekerjaan.isBlank() || jenis_kelamin.isBlank() || ulasan.isBlank
                    () || selectedWisataId == null || umur.isBlank()){
                Snackbar.make(binding.root, "Data tidak boleh kosong", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = viewModel.sendReviewGuest(nama, umur.toInt(), asal, pekerjaan,
                        jenis_kelamin, selectedWisataId!!, ulasan)
                    Snackbar.make(binding.root, "Ulasan berhasil dikirim", Snackbar.LENGTH_SHORT).show()
                    binding.etNama.text?.clear()
                    binding.etUmur.text?.clear()
                    binding.etAsal.text?.clear()
                    binding.etPekerjaan.text?.clear()
                    binding.genderAutoComplete.text?.clear()
                    binding.etUlasan.text?.clear()
                    binding.etJenisWisata.text?.clear()
                    Log.d("UlasanFragment", "Review sent successfully: $response")
                } catch (e: Exception) {
                    Log.e("UlasanFragment", "Error sending review: ${e.message}")
                    Snackbar.make(binding.root, "Gagal mengirim ulasan", Snackbar.LENGTH_LONG).show()
                    binding.etNama.text?.clear()
                    binding.etUmur.text?.clear()
                    binding.etAsal.text?.clear()
                    binding.etPekerjaan.text?.clear()
                    binding.genderAutoComplete.text?.clear()
                    binding.etUlasan.text?.clear()
                    binding.etJenisWisata.text?.clear()
                }
            }
        }
    }
}