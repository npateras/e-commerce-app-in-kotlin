package com.unipi.mpsp21043.emarketadmin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.unipi.mpsp21043.emarketadmin.adapters.ProductsListAdapter
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import com.unipi.mpsp21043.emarketadmin.databinding.FragmentProductsBinding
import com.unipi.mpsp21043.emarketadmin.models.Product
import com.unipi.mpsp21043.emarketadmin.utils.IntentUtils


class ProductsFragment : BaseFragment() {

    // ~~~~~~~VARIABLES~~~~~~~
    private var _binding: FragmentProductsBinding? = null  // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private val binding get() = _binding!!
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        binding.apply {
            veilRecyclerView.visibility = View.INVISIBLE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
            fabAdd.setOnClickListener { IntentUtils().goToAddNewProductActivity(this@ProductsFragment.requireContext())}
        }

        getProducts()
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
            binding.veilRecyclerView.visibility = View.VISIBLE
            binding.layoutEmptyState.root.visibility = View.GONE

            // sets VeilRecyclerView's properties
            binding.veilRecyclerView.run {
                adapter = ProductsListAdapter(
                    requireActivity(),
                    productsList
                )
                hasFixedSize()
                layoutManager = GridLayoutManager(this@ProductsFragment.requireContext(), 3, GridLayoutManager.VERTICAL, false)
            }
        }
        else
            hideRecycler()

        binding.shimmerViewContainer.stopShimmer()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    private fun hideRecycler() {
        binding.apply {
            // veilRecyclerView.unVeil()
            veilRecyclerView.visibility = View.GONE
            layoutEmptyState.root.visibility = View.VISIBLE
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
