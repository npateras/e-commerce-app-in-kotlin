package com.unipi.mpsp21043.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.client.adapters.ProductsListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.FragmentHomeBinding
import com.unipi.mpsp21043.client.models.Product
import com.unipi.mpsp21043.client.utils.IntentUtils


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
        loadDeals()
        loadPopular()

        setupClickListeners()
    }

    private fun loadDeals() {
        showShimmerDealsUI()

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
        showShimmerPopularUI()

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
            layoutEmptyStatePopular.root.visibility = View.GONE
            recyclerViewPopular.visibility = View.INVISIBLE
            shimmerViewContainerPopular.visibility = View.VISIBLE
            shimmerViewContainerPopular.startShimmer()
        }
    }

    private fun hideShimmerPopularUI() {
        binding.apply {
            layoutEmptyStatePopular.root.visibility = View.GONE
            recyclerViewPopular.visibility = View.VISIBLE
            shimmerViewContainerPopular.visibility = View.GONE
            shimmerViewContainerPopular.stopShimmer()
        }
    }

    private fun showEmptyStatePopularUI() {
        binding.apply {
            layoutEmptyStatePopular.root.visibility = View.VISIBLE
            recyclerViewPopular.visibility = View.INVISIBLE
            shimmerViewContainerPopular.visibility = View.GONE
            shimmerViewContainerPopular.stopShimmer()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewCategoriesViewAll.setOnClickListener { IntentUtils().goToCategoriesActivity(requireActivity()) }
            textViewDealsViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Deals") }
            textViewPopularViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Popular") }
            imageButtonCategoryChilled.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Chilled") }
            imageButtonCategoryGrocery.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Grocery") }
            imageButtonCategoryHousehold.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Household") }
            imageButtonCategoryLiquor.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "Liquor") }
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
