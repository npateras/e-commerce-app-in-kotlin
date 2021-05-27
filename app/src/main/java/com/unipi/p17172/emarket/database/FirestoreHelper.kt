package com.unipi.p17172.emarket.database

import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.ui.activities.ProductDetailsActivity
import com.unipi.p17172.emarket.ui.fragments.HomeFragment
import com.unipi.p17172.emarket.utils.Constants

class FirestoreHelper {

    // Access a Cloud Firestore instance.
    private val dbFirestore = FirebaseFirestore.getInstance()

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the products list from cloud firestore that are on sale.
     *
     * @param fragment The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getDealsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .whereGreaterThan(Constants.FIELD_SALE, 0) // Only return products with a sale percentage bigger than 0.
            .orderBy(Constants.FIELD_SALE, Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    productsList.add(product)
                }
                when (fragment) {
                    is HomeFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                    else -> {}
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is HomeFragment -> {
                        // TODO: Show error state maybe
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    fun isFavorite(productId: String, userId: String, isFavoriteCallback: IsFavoriteCallback) {

        var isFavorite = false

        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .document(productId)
                .collection(Constants.COLLECTION_FAVORITES)
                .whereEqualTo(Constants.FIELD_USER_ID, userId)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    // Here we get the list of boards in the form of documents.
                    Log.e("Favorites", document?.documents.toString())

                    if (document != null && !document.isEmpty)
                        isFavorite = true

                    isFavoriteCallback.onCallback(isFavorite)
                }
                .addOnFailureListener { e ->
                    Log.e("Get Favorites", "Error while getting favorites of product.", e)
                }
    }

    fun addToFavorites(productId: String) {
        val user = getCurrentUserID()

    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                activity.productDetailsSuccess(product)
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }


    // Callbacks
    interface IsFavoriteCallback {
        fun onCallback(isFavorite: Boolean)
    }
}