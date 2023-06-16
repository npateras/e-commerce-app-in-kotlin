package com.unipi.mpsp21043.admin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.mpsp21043.admin.adapters.ProductsListAdapter
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.FragmentProductsBinding
import com.unipi.mpsp21043.admin.models.Product
import com.unipi.mpsp21043.admin.utils.IntentUtils


class ProductsFragment : Fragment() {

    // ~~~~~~~VARIABLES~~~~~~~
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentProductsBinding? = null  // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var productsListAdapter: ProductsListAdapter
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
        binding.apply {
            fabAdd.setOnClickListener { IntentUtils().goToAddNewProductActivity(this@ProductsFragment.requireContext())}
        }
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

            productsListAdapter = ProductsListAdapter(
                requireActivity(),
                productsList
            )

            // productsListAdapter.setList(this@ProductsFragment.requireContext(), productsList)
            // Sets RecyclerView's properties
            binding.recyclerViewItems.run {
                adapter = productsListAdapter
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
