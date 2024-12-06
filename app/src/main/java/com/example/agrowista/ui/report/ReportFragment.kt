package com.example.agrowista.ui.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrowista.R
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.databinding.FragmentReportBinding
import com.example.agrowista.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var binding: FragmentReportBinding
    private var selectedImageUri: Uri? = null
    private var temporaryPhotoUri: Uri? = null
    private var selectedGender: Char? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[ReportViewModel::class.java]

        genderDropdown()
        loadUserProfile()
        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            val isLoggedIn = userPreferences.isUserLoggedIn().first()
            if (!isLoggedIn) {
                userNotLoggedIn()
                enableGuestFields()
            } else {
                userLoggedIn()
            }
        }
    }

    private fun resetForm() {
        binding.apply {
            etNama.text?.clear()
            etUmur.text?.clear()
            etAsal.text?.clear()
            etPekerjaan.text?.clear()
            genderAutoComplete.text?.clear()
            etJudul.text?.clear()
            etDeskripsi.text?.clear()
            resetImageState()
        }
    }

    private fun enableGuestFields() {
        binding.apply {
            etNama.isEnabled = true
            etUmur.isEnabled = true
            etAsal.isEnabled = true
            etPekerjaan.isEnabled = true
            genderAutoComplete.isEnabled = true
        }
    }

    private fun userNotLoggedIn(){
        binding.loginBanner.visibility = View.VISIBLE
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(ReportFragmentDirections.actionReportFragmentToLoginFragment())
        }
    }

    private fun userLoggedIn(){
        binding.loginBanner.visibility = View.GONE
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
            Log.d("Report", "Selected gender: $selectedGender")
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_required), Toast.LENGTH_SHORT).show()
            }
        }

    private fun setupViews() {
        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                startCamera()
            }
        }

        binding.btnSubmitReport.setOnClickListener {
            if (selectedImageUri == null || !isImageValid(selectedImageUri!!)) {
                Toast.makeText(requireContext(), getString(R.string.insert_image_first), Toast.LENGTH_SHORT).show()
                binding.btnSubmitReport.isEnabled = false
                return@setOnClickListener
            }
            uploadSelectedImage()
        }

        binding.ivImage.setOnClickListener {
            openGallery()
        }
    }

    private fun resetImageState() {
        selectedImageUri = null
        binding.ivImage.setImageResource(R.drawable.ic_image)
        binding.btnSubmitReport.isEnabled = false
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && isImageValid(uri)) {
            selectedImageUri = uri
            binding.ivImage.setImageURI(uri)
            binding.btnSubmitReport.isEnabled = true
        } else {
            selectedImageUri = null
            binding.btnSubmitReport.isEnabled = false
            Toast.makeText(requireContext(), getString(R.string.invalid_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isImageValid(uri: Uri): Boolean {
        return try {
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(uri)
            mimeType?.let {
                return it.startsWith("image/")
            } ?: false
        } catch (e: Exception) {
            Log.e("ReportFragment", "Error validating image", e)
            false
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            temporaryPhotoUri?.let { uri ->
                selectedImageUri = uri
                binding.ivImage.setImageURI(uri)
                binding.btnSubmitReport.isEnabled = true
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.failed_to_capture_image), Toast.LENGTH_SHORT).show()
            binding.btnSubmitReport.isEnabled = false
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        try {
            val photoFile = createImageFile()
            temporaryPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            takePicture.launch(temporaryPhotoUri)
        } catch (e: Exception) {
            Log.e("ReportFragment", "Error starting camera", e)
            Toast.makeText(requireContext(), getString(R.string.failed_to_start_camera), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        getContent.launch("image/*")
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
                Log.e("ReportFragment", "Error loading profile: ${e.message}")
            }
        }
    }

    fun Uri.toFile(): File {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(this)

        if (mimeType !in listOf("image/*, image/jpeg", "image/png", "image/jpg")) {
            throw IllegalArgumentException("Invalid file type")
        }

        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            contentResolver.openInputStream(this)?.use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun uploadSelectedImage() {
        val currentUri = selectedImageUri
        if (currentUri == null || !isImageValid(currentUri)) {
            Toast.makeText(requireContext(), getString(R.string.insert_image_first), Toast.LENGTH_SHORT).show()
            binding.btnSubmitReport.isEnabled = false
            return
        }

        val imageFile = currentUri.toFile()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = if (viewModel.isUserLoggedIn()) {
                    viewModel.sendReport(
                        binding.etJudul.text.toString(),
                        binding.etDeskripsi.text.toString(),
                        imageFile
                    )
                } else {
                    viewModel.sendReportGuest(
                        binding.etNama.text.toString(),
                        binding.etUmur.text.toString().toInt(),
                        binding.etAsal.text.toString(),
                        binding.etPekerjaan.text.toString(),
                        selectedGender.toString(),
                        binding.etJudul.text.toString(),
                        binding.etDeskripsi.text.toString(),
                        imageFile
                    )
                }

                response.error?.let {
                    if (!it) {
                        Snackbar.make(binding.root, "Laporan berhasil dikirim", Snackbar.LENGTH_SHORT).show()
                        if (viewModel.isUserLoggedIn()) {
                            binding.etJudul.text?.clear()
                            binding.etDeskripsi.text?.clear()
                            resetImageState()
                        } else {
                            Snackbar.make(binding.root, "Laporan berhasil dikirim", Snackbar.LENGTH_SHORT).show()
                            resetForm()
                        }
                    } else {
                        Snackbar.make(binding.root, response.message ?: "Gagal mengirim laporan", Snackbar.LENGTH_SHORT).show()
                        resetForm()
                    }
                }
            } catch (e: Exception) {
                Log.e("ReportFragment", "Error sending report", e)
                Snackbar.make(binding.root, "Gagal mengirim laporan", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}