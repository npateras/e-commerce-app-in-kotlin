package com.unipi.mpsp21043.emarket.admin.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.emarket.admin.R
import com.unipi.mpsp21043.emarket.admin.database.FirestoreHelper
import com.unipi.mpsp21043.emarket.admin.databinding.FragmentFavoritesBinding
import com.unipi.mpsp21043.emarket.admin.utils.Constants

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
        veilRecycler()

        loadFavorites()
    }

    private fun loadFavorites() {
        // FirestoreHelper().getFavoritesList(this@FavoritesFragment)
    }

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param favoritesList Will receive the product list from cloud firestore.
     */
    /*fun successFavoritesListFromFireStore(favoritesList: ArrayList<Favorite>) {

        if (favoritesList.size > 0) {
            binding.veilRecyclerView.visibility = View.VISIBLE
            binding.layoutEmptyState.root.visibility = View.GONE

            // sets VeilRecyclerView's properties
            binding.veilRecyclerView.run {
                setVeilLayout(R.layout.shimmer_item_product)
                setAdapter(
                    FavoritesListAdapter(
                        requireActivity(),
                        favoritesList
                    )
                )
                setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
                getRecyclerView().setHasFixedSize(true)
                addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_VERTICAL)
                // delay-auto-unveil
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        unVeil()
                    },
                    1000
                )
            }
        }
        else {
            binding.apply {
                veilRecyclerView.unVeil()
                veilRecyclerView.visibility = View.GONE
                layoutEmptyState.root.visibility = View.VISIBLE
            }
        }
    }*/

    private fun veilRecycler() {
        binding.apply {
            veilRecyclerView.veil()

            veilRecyclerView.addVeiledItems(Constants.DEFAULT_VEILED_ITEMS_VERTICAL)
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
