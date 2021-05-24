package com.unipi.p17172.emarket.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.p17172.emarket.R
import com.unipi.p17172.emarket.adapters.ProductsListAdapter
import com.unipi.p17172.emarket.database.FirestoreHelper
import com.unipi.p17172.emarket.databinding.FragmentHomeBinding
import com.unipi.p17172.emarket.models.Product


class HomeFragment : Fragment() {

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
        loadDeals()
        loadPopular()

        return binding.root
    }

    private fun init() {

    }

    private fun loadDeals() {
        FirestoreHelper().getDealsList(this@HomeFragment)
    }

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param productsList Will receive the product list from cloud firestore.
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // Hide Progress dialog.
        // hideProgressDialog()

        if (productsList.size > 0) {
            //binding.veilRecyclerViewDeals.unVeil()
            binding.veilRecyclerViewDeals.visibility = View.VISIBLE
            binding.layoutEmptyStateDeals.root.visibility = View.GONE

            // sets VeilRecyclerView's properties
            binding.veilRecyclerViewDeals.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(ProductsListAdapter(requireActivity(), productsList, this@HomeFragment))
                setLayoutManager(LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false))
                getRecyclerView().setHasFixedSize(true)
                addVeiledItems(15)
                // delay-auto-unveil
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        unVeil()
                    },
                    1000
                )
            }
        } else {
            binding.veilRecyclerViewDeals.visibility = View.INVISIBLE
            binding.layoutEmptyStateDeals.root.visibility = View.VISIBLE
        }
    }

    private fun loadPopular() {

    }

    override fun onResume() {
        super.onResume()

        loadDeals()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}