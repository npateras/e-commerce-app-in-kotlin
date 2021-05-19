package com.unipi.p17172.emarket.fragment

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.unipi.p17172.emarket.adapter.ProductRecyclerViewAdapter
import com.unipi.p17172.emarket.database.getProducts
import com.unipi.p17172.emarket.databinding.FragmentHomeBinding
import com.unipi.p17172.emarket.holders.ProductViewHolder
import com.unipi.p17172.emarket.models.ProductModel


class HomeFragment : Fragment() {

    // ~~~~~~~VARIABLES~~~~~~~
    private var _binding: FragmentHomeBinding? = null  // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private val binding get() = _binding!!

    private var db: FirebaseFirestore? = null
    private val productsListRef: CollectionReference? = null

    private val firestoreDealsAdapter: FirestoreRecyclerAdapter<ProductModel, ProductViewHolder>? = null
    private val firestorePopularAdapter: FirestoreRecyclerAdapter<ProductModel, ProductViewHolder>? = null

    companion object {
        private var COLLECTION_NAME: String = "Products"
        private var FIELD_NAME: String = "sale"
        private var TAG: String = "[eMarket]"
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val queryDeals: Query = getProducts()

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> =
            FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(queryDeals, ProductModel::class.java)
                .build()

        val productsList = mutableListOf<ProductModel>()

        firestoreDealsAdapter = ProductRecyclerViewAdapter(productsList, requireContext(), db!!)

        firestoreListener = firestoreDB!!
            .collection(COLLECTION_NAME)
            .orderBy(FIELD_NAME)
            .whereGreaterThan(FIELD_NAME, 0)
            .limit(50)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val dealsList = mutableListOf()

                if (documentSnapshots != null) {
                    for (doc in documentSnapshots) {
                        val note = doc.toObject(ContactsContract.CommonDataKinds.Note::class.java)
                        note.id = doc.id
                        notesList.add(note)
                    }
                }

                mAdapter = NoteRecyclerViewAdapter(notesList, requireContext(), firestoreDB!!)
                rvNoteList.adapter = mAdapter
            })
    }

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
        binding.veilRecyclerViewDeals.addVeiledItems(15)
        binding.veilRecyclerViewPopular.addVeiledItems(15)

        val linearLayoutManagerDeals =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val linearLayoutManagerPopular =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.veilRecyclerViewDeals.setLayoutManager(linearLayoutManagerDeals)
        binding.veilRecyclerViewPopular.setLayoutManager(linearLayoutManagerPopular)

        db = FirebaseFirestore.getInstance();
    }

    private fun loadDeals() {
        Log.e(" TEST", "queryProductDetails.get().result.toString()")
        val queryDeals: Task<QuerySnapshot> = db
            .collection("Deals")
            .orderBy("sale")
            .limit(50).get()

        val options: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
            .setQuery(queryDeals, ProductModel::class.java)
            .build()

        val adapter: FirestoreRecyclerAdapter<*, *> =
            object : FirestoreRecyclerAdapter<Chat?, ChatHolder?>(options) {
                // ...
                override fun onDataChanged() {
                    // Called each time there is a new query snapshot. You may want to use this method
                    // to hide a loading spinner or check for the "no documents" state and update your UI.
                    // ...
                }

                override fun onError(e: FirebaseFirestoreException) {
                    // Called when there is an error getting a query snapshot. You may want to update
                    // your UI to display an error message to the user.
                    // ...
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
                    TODO("Not yet implemented")
                }

                override fun onBindViewHolder(holder: ChatHolder, position: Int, model: Chat) {
                    TODO("Not yet implemented")
                }
            }

        if (queryDeals.isComplete) {
            for (item in queryDeals.result?.documents!!) {
                val queryProductDetails: Query = firestoreDB
                    .collection("Products").whereEqualTo("productId", item["id"])
                if (queryProductDetails.get().isComplete) {
                    Log.e(" TEST", queryProductDetails.get().result.toString())
                }
            }
        }
    }

    private fun loadPopular() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        firestoreDealsAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        firestoreDealsAdapter?.stopListening()
    }
}