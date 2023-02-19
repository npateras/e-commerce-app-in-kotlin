package com.unipi.mpsp21043.emarketadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.mpsp21043.emarketadmin.adapters.ProductsListAdapter
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.FragmentProductsBinding
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils


class ProductsFragment : Fragment() {

    // ~~~~~~~VARIABLES~~~~~~~
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentProductsBinding? = null  // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)

        init()
        setupUI()

        return binding.root
    }

    private fun init() {
        showShimmerUI()

        getProducts()
    }

    private fun setupUI() {
        binding.fabAdd.setOnClickListener { IntentUtils().goToAddNewProductActivity(this@ProductsFragment.requireContext())}
    }

    private fun getProducts() {
        FirestoreHelper().getProductsList(this@ProductsFragment)
    }

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param productsList Will receive the product list from cloud firestore.
     */
    fun successProductsListFromFirestore(productsList: ArrayList<Product>) {

        if (productsList.size > 0) {
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerViewItems.run {
                adapter = ProductsListAdapter(
                    requireActivity(),
                    productsList
                )
                hasFixedSize()
                layoutManager = GridLayoutManager(this@ProductsFragment.requireContext(), 3, GridLayoutManager.VERTICAL, false)
            }
        }
        else
            showEmptyStateUI()

        binding.shimmerViewContainer.stopShimmer()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerViewItems.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun showEmptyStateUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.VISIBLE
            recyclerViewItems.visibility = View.GONE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    /**
     * The fragment's onResume() will be called only when the Activity's onResume() is called.
     */
    override fun onResume() {
        super.onResume()

        init()
    }

    /**
     * We clean up any references to the binding class instance in the fragment's onDestroyView() method.
     */
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
