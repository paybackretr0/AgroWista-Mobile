package com.example.agrowista.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agrowista.adapter.AgroAdapter
import com.example.agrowista.adapter.UlasanAdapter
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.HomeResponse
import com.example.agrowista.databinding.FragmentHomeBinding
import com.example.agrowista.ui.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val userPreferences = UserPreferences.getInstance(requireContext())
        viewModel = ViewModelProvider(this, ViewModelFactory(userPreferences))[HomeViewModel::class.java]

        viewModel.homeData.observe(viewLifecycleOwner) { homeResponse ->
            populateData(homeResponse)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.fetchHomeData()
        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnReview.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUlasanFragment())
        }
    }

    private fun populateData(homeResponse: HomeResponse) {
        homeResponse.data?.let { data ->

            binding.tvUlasanCount.text = data.totalUlasan.toString()
            binding.tvWisataCount.text = data.totalWisata.toString()
            binding.tvProdukCount.text = data.totalProduk.toString()

            binding.agrotourismRecycler.adapter = AgroAdapter(data.agro)
            binding.reviewsRecycler.adapter = UlasanAdapter(data.ulasan)
        }
    }
}