package com.unipi.p17172.emarket.database

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.unipi.p17172.emarket.models.Cart
import com.unipi.p17172.emarket.models.Favorite
import com.unipi.p17172.emarket.models.Product
import com.unipi.p17172.emarket.ui.activities.MyCartActivity
import com.unipi.p17172.emarket.ui.activities.ProductDetailsActivity
import com.unipi.p17172.emarket.ui.fragments.FavoritesFragment
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
                        fragment.successDealsListFromFireStore(productsList)
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

    /**
     * A function to get the products list from cloud firestore that are on sale.
     *
     * @param fragmentFavorites The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getFavoritesList(fragmentFavorites: FavoritesFragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo(Constants.FIELD_USER_ID, "test")
            .orderBy(Constants.FIELD_DATE_ADDED, Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Favorites List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val favoritesList: ArrayList<Favorite> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val favorite = i.toObject(Favorite::class.java)
                    favorite!!.id = i.id

                    favoritesList.add(favorite)
                }
                fragmentFavorites.successFavoritesListFromFireStore(favoritesList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                // TODO: Show error state maybe

                Log.e("Get Favorites List", "Error while getting favorite products list.", e)
            }
    }

    fun isFavorite(productId: String, isFavoriteCallback: IsFavoriteCallback) {

        var isFavorite = false

        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo(Constants.FIELD_USER_ID, "test")
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
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

    fun addToFavorites(activity: Activity, productId: String, userId: String) {
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(productId)
            .collection(Constants.COLLECTION_FAVORITES)
            .document(userId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userId, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                //activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to update the cart item in the cloud firestore.
     *
     * @param context Context of a activity class.
     * @param cartId cart id of the item.
     */
    fun updateCartProduct(context: Context, cartId: String, quantity: Int) {
        val cartHashMap = HashMap<String, Any>()
        cartHashMap[Constants.FIELD_CART_QUANTITY] = quantity

        // Cart items collection name
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .document(cartId) // user id
            .update(cartHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    is MyCartActivity -> {
//                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                /*when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }*/

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    fun getCartId(activity: ProductDetailsActivity, userId: String, productId: String) {

        var cartId = ""

        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .whereEqualTo(Constants.FIELD_USER_ID, userId)
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e("Favorites", document?.documents.toString())

                if (document != null && !document.isEmpty)
                    cartId = document.documents[0][Constants.FIELD_CART_ID] as String

                activity.cartIdSuccess(cartId)
            }
            .addOnFailureListener { e ->
                Log.e("Get Cart ID", "Error while getting ID of cart.", e)
            }
    }

    fun removeFromFavorites(activity: Activity, productId: String, userId: String) {
        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(productId)
            .collection(Constants.COLLECTION_FAVORITES)
            .document(userId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                //activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
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

    /**
     * A function to get the list of orders from cloud firestore.
     */
    fun getCartProductDetails(activity: ProductDetailsActivity, cartId: String) {
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .document(cartId)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val cartItem = document.toObject(Cart::class.java)!!

                activity.cartProductDetailsSuccess(cartItem)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.unveilDetails()

                Log.e(activity.javaClass.simpleName, "Error while getting cart product details.", e)
            }
    }

    // Callbacks
    interface IsFavoriteCallback {
        fun onCallback(isFavorite: Boolean)
    }
}