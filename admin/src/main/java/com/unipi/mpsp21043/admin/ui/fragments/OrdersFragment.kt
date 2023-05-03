package com.unipi.mpsp21043.admin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.admin.adapters.OrdersListAdapter
import com.unipi.mpsp21043.admin.database.FirestoreHelper
import com.unipi.mpsp21043.admin.databinding.FragmentOrdersBinding
import com.unipi.mpsp21043.admin.models.Order

class OrdersFragment : Fragment() {
    // ~~~~~~~VARIABLES~~~~~~~
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentOrdersBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var ordersListAdapter: OrdersListAdapter
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        showShimmerUI()

        getOrdersList()
    }

    private fun getOrdersList() {
        FirestoreHelper().getOrdersList(this@OrdersFragment)
    }

    /**
     * A function to get the orders list from Cloud Firestore.
     *
     * @param ordersList Will receive the orders list from cloud firestore.
     */
    fun successOrdersListFromFirestore(ordersList: ArrayList<Order>) {

        if (ordersList.size > 0) {
            hideShimmerUI()

            binding.apply {
                ordersListAdapter = OrdersListAdapter(
                    requireActivity(),
                    ordersList
                )
                ordersListAdapter.setList(this@OrdersFragment.requireContext(), ordersList)
                // Sets RecyclerView's properties
                recyclerViewItems.run {
                    adapter = ordersListAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                }
            }
        }
        else
            showEmptyStateUI()
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
