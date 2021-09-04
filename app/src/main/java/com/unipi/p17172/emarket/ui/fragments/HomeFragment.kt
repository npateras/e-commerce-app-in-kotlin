package com.unipi.p17172.emarket.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.ProductsListAdapter
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.FragmentHomeBinding
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.utils.Constants
import com.unipi.p17172.emarket.utils.IntentUtils


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
        veilRecyclers()
        loadDeals()
        setupClickListeners()
    }

    private fun loadDeals() {
        FirestoreHelper().getDealsList(this@HomeFragment)
    }

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param productsList Will receive the product list from cloud firestore.
     */
    fun successDealsListFromFireStore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {

            // Show the recycler and remove the empty state layout completely.
            binding.apply {
                veilRecyclerViewDeals.visibility = View.VISIBLE
                layoutEmptyStateDeals.root.visibility = View.GONE
            }

            // sets VeilRecyclerView's properties
            binding.veilRecyclerViewDeals.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(
                    ProductsListAdapter(
                        requireActivity(),
                        productsList,
                        this@HomeFragment
                    )
                )
                setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false))
                getRecyclerView().setHasFixedSize(true)
                addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_HORIZONTAL)
                // delay-auto-unveil
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        unveilRecyclers()
                    },
                    1000
                )
            }
        } else {
            unveilRecyclers()
            // Hide the recycler and show the empty state layout.
            binding.apply {
                veilRecyclerViewDeals.visibility = View.INVISIBLE
                layoutEmptyStateDeals.root.visibility = View.VISIBLE
            }

        }
    }

    // todo
    private fun loadPopular() {

    }

    private fun veilRecyclers() {
        binding.apply {
            veilRecyclerViewDeals.veil()
            veilRecyclerViewPopular.veil()

            veilRecyclerViewDeals.addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_HORIZONTAL)
            veilRecyclerViewPopular.addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_HORIZONTAL)
        }
    }
    private fun unveilRecyclers() {
        binding.apply {
            veilRecyclerViewDeals.unVeil()
            veilRecyclerViewPopular.unVeil()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            txtViewCategoriesViewAll.setOnClickListener { IntentUtils().goToCategoriesActivity(requireActivity()) }
            txtViewDealsViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "deals") }
            txtViewPopularViewAll.setOnClickListener { IntentUtils().goToListProductsActivity(requireActivity(), "popular") }
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
