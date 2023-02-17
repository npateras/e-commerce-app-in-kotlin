package com.unipi.mpsp21043.emarketadmin.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.unipi.mpsp21043.emarketadmin.adapters.FavoritesListAdapter
import com.unipi.mpsp21043.emarketadmin.databinding.FragmentStatisticsBinding
import com.unipi.mpsp21043.emarketadmin.models.Favorite
import com.unipi.mpsp21043.emarketadmin.utils.Constants

class StatisticsFragment : Fragment() {
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        veilRecycler()

        loadFavorites()
    }

    private fun loadFavorites() {
        // FirestoreHelper().getFavoritesList(this@StatisticsFragment)
    }
    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param favoritesList Will receive the product list from cloud firestore.
     */
    fun successFavoritesListFromFireStore(favoritesList: ArrayList<Favorite>) {

        if (favoritesList.size > 0) {
            binding.veilRecyclerView.visibility = View.VISIBLE
            binding.layoutEmptyState.root.visibility = View.GONE

            // sets VeilRecyclerView's properties
            binding.veilRecyclerView.run {
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
    }

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
