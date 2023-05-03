package com.unipi.mpsp21043.client.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.client.adapters.FavoritesListAdapter
import com.unipi.mpsp21043.client.database.FirestoreHelper
import com.unipi.mpsp21043.client.databinding.FragmentFavoritesBinding
import com.unipi.mpsp21043.client.models.Favorite

class FavoritesFragment : Fragment() {
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {

        if (FirestoreHelper().getCurrentUserID() != "") {
            showShimmerUI()
            loadFavorites()
        }
        else
            showMustSignInUI()
    }

    private fun loadFavorites() {
        FirestoreHelper().getFavoritesList(this@FavoritesFragment)
    }

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param favoritesList Will receive the product list from cloud firestore.
     */
    fun successFavoritesListFromFireStore(favoritesList: ArrayList<Favorite>) {

        if (favoritesList.size > 0) {
            hideShimmerUI()

            // Sets RecyclerView's properties
            binding.recyclerView.run {
                adapter = FavoritesListAdapter(
                    requireActivity(),
                    favoritesList
                )
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }
        }
        else {
            showEmptyStateUI()
        }
    }

    fun showErrorUI() {
        hideShimmerUI()
        binding.apply {
            layoutErrorState.root.visibility = View.VISIBLE
        }
    }

    private fun showShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.visibility = View.VISIBLE
            shimmerViewContainer.startShimmer()
        }
    }

    private fun hideShimmerUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun showEmptyStateUI() {
        binding.apply {
            layoutEmptyState.root.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
        }
    }

    private fun showMustSignInUI() {
        binding.apply {
            layoutErrorStateMustSignIn.root.visibility = View.VISIBLE
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
