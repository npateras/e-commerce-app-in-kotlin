package com.unipi.mpsp21043.emarket.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.emarket.R
import com.unipi.mpsp21043.emarket.adapters.ProductsListAdapter
import com.unipi.mpsp21043.emarket.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.databinding.FragmentHomeBinding
import com.unipi.mpsp21043.emarket.models.Product
import com.unipi.mpsp21043.emarket.utils.Constants
import com.unipi.mpsp21043.emarket.utils.IntentUtils


class HomeFragment : BaseFragment() {

    // ~~~~~~~VARIABLES~~~~~~~
    private var _binding: FragmentHomeBinding? = null  // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private val binding get() = _binding!!
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        showShimmerDealsUI()
        showShimmerPopularUI()

        loadDeals()
        loadPopular()

        setupClickListeners()
    }

    private fun loadDeals() {
        FirestoreHelper().getDealsList(this@HomeFragment)
    }

    fun successDealsListFromFireStore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            hideShimmerDealsUI()

            // Sets RecyclerView's properties
            binding.recyclerViewDeals.run {
                adapter = ProductsListAdapter(
                    requireActivity(),
                    productsList
                )
                hasFixedSize()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        else
            showEmptyStateDealsUI()
    }

    private fun loadPopular() {
        FirestoreHelper().getPopularList(this@HomeFragment)
    }

    fun successPopularListFromFireStore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            hideShimmerPopularUI()

            // Sets RecyclerView's properties
            binding.recyclerViewPopular.run {
                adapter = ProductsListAdapter(
                    requireActivity(),
                    productsList
                )
                hasFixedSize()
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
        else
            showEmptyStatePopularUI()
    }

    private fun showShimmerDealsUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.GONE
            recyclerViewDeals.visibility = View.INVISIBLE
            shimmerViewContainerDeals.visibility = View.VISIBLE
            shimmerViewContainerDeals.startShimmer()
        }
    }

    private fun hideShimmerDealsUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.GONE
            recyclerViewDeals.visibility = View.VISIBLE
            shimmerViewContainerDeals.visibility = View.GONE
            shimmerViewContainerDeals.stopShimmer()
        }
    }

    private fun showEmptyStateDealsUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.VISIBLE
            recyclerViewDeals.visibility = View.INVISIBLE
            shimmerViewContainerDeals.visibility = View.GONE
            shimmerViewContainerDeals.stopShimmer()
        }
    }

    private fun showShimmerPopularUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.GONE
            recyclerViewDeals.visibility = View.INVISIBLE
            shimmerViewContainerDeals.visibility = View.VISIBLE
            shimmerViewContainerDeals.startShimmer()
        }
    }

    private fun hideShimmerPopularUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.GONE
            recyclerViewDeals.visibility = View.VISIBLE
            shimmerViewContainerDeals.visibility = View.GONE
            shimmerViewContainerDeals.stopShimmer()
        }
    }

    private fun showEmptyStatePopularUI() {
        binding.apply {
            layoutEmptyStateDeals.root.visibility = View.VISIBLE
            recyclerViewDeals.visibility = View.INVISIBLE
            shimmerViewContainerDeals.visibility = View.GONE
            shimmerViewContainerDeals.stopShimmer()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewCategoriesViewAll.setOnClickListener { IntentUtils().goToCategoriesActivity(requireActivity()) }
            textViewDealsViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Deals") }
            textViewPopularViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "popular") }
            imageButtonCategoryChilled.setOnClickListener{ IntentUtils().goToListProductsActivity(requireActivity(), "Chilled") }
            imageButtonCategoryGrocery.setOnClickListener{ IntentUtils().goToListProductsActivity(requireActivity(), "Grocery") }
            imageButtonCategoryHousehold.setOnClickListener{ IntentUtils().goToListProductsActivity(requireActivity(), "Household") }
            imageButtonCategoryLiquor.setOnClickListener{ IntentUtils().goToListProductsActivity(requireActivity(), "Liquor") }
        }
    }

    override fun onResume() {
        super.onResume()

        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
