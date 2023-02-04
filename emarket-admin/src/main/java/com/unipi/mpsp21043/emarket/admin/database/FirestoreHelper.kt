package com.unipi.mpsp21043.emarket.admin.database

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.unipi.mpsp21043.emarket.admin.models.Product
import com.unipi.mpsp21043.emarket.admin.ui.fragments.FavoritesFragment
import com.unipi.mpsp21043.emarket.admin.ui.fragments.HomeFragment
import com.unipi.mpsp21043.emarket.admin.ui.fragments.MyAccountFragment
import com.unipi.mpsp21043.emarket.admin.utils.Constants
import com.unipi.mpsp21043.emarket.admin.models.*
import com.unipi.mpsp21043.emarket.admin.ui.activities.*
import com.unipi.mpsp21043.emarket.ui.activities.MainActivity

class FirestoreHelper {

    // Access a Cloud Firestore instance.
    private val dbFirestore = FirebaseFirestore.getInstance()


    // region -User Management-
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

    fun getUserFCMRegistrationToken(activity: Activity) {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(
                    Constants.TAG,
                    "Fetching FCM registration token failed",
                    task.exception)
                return@OnCompleteListener
            }

            if (getCurrentUserID() == "")
                return@OnCompleteListener

            // Get new FCM registration token
            val token = task.result!!

            when (activity) {
                is MainActivity -> {
                    activity.userFcmRegistrationTokenSuccess(token)
                }
            }
        })
    }

    fun getFCMRegistrationTokenDB(onComplete: (tokens: MutableList<String>) -> Unit) {
        dbFirestore.collection(Constants.COLLECTION_USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                onComplete(user.registrationTokens)
            }
            .addOnFailureListener { e ->
                Log.e(
                    Constants.TAG,
                    "Fetching FCM registration token failed.",
                    e
                )
            }
    }

    /**
     * A function that gets the registration fcm tokens from the registered user in the FireStore
     * database.
     *
     * @param registrationTokens todo
     */
    fun setFCMRegistrationToken(registrationTokens: MutableList<String>) {
        dbFirestore.collection(Constants.COLLECTION_USERS)
            .document(getCurrentUserID())
            .update(mapOf(Constants.FIELD_REGISTRATION_TOKENS to registrationTokens))
            .addOnFailureListener { e ->
                Log.e(
                    Constants.TAG,
                    "Update of FCM registration tokens failed.",
                    e
                )
            }
    }
    //end region USER

    // region ADDRESS
    fun getAddressList(activity: ListAddressActivity) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_ADDRESSES)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.successUserAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }

    /**
     * A function to add address to the cloud firestore.
     *
     * @param activity
     * @param addressInfo
     */
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        dbFirestore.collection(Constants.COLLECTION_ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.successUpdateAddressToFirestore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    /**
     * A function to update the existing address to the cloud firestore.
     *
     * @param activity Base class
     * @param addressInfo Which fields are to be updated.
     * @param addressId existing address id
     */
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        dbFirestore.collection(Constants.COLLECTION_ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.successUpdateAddressToFirestore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    /**
     * A function to delete the existing address from the cloud firestore.
     *
     * @param activity Base class
     * @param addressId existing address id
     */
    fun deleteAddress(activity: ListAddressActivity, addressId: String) {

        dbFirestore.collection(Constants.COLLECTION_ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    // endregion

    /**
     * A function to update the existing user details to the cloud firestore.
     *
     * @param activity Base class
     * @param userInfo Which fields are to be updated.
     */
    fun updateProfile(activity: EditProfileActivity, userInfo: User) {

        dbFirestore.collection(Constants.COLLECTION_ADDRESSES)
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.successUpdateProfileToFirestore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user's profile.",
                    e
                )
            }
    }

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    /*fun registerUser(activity: SignUpActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }*/

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
                Log.d("Products List", document.documents.toString())

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

    fun getPopularList(fragment: Fragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .whereGreaterThan(Constants.FIELD_POPULARITY, 0)
            .orderBy(Constants.FIELD_POPULARITY, Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Popular Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val popularProductsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    popularProductsList.add(product)
                }
                when (fragment) {
                    is HomeFragment -> {
                        fragment.successPopularListFromFireStore(popularProductsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Popular Product List", "Error while getting popular product list.", e)
            }
    }

    fun getProductsListFromCategory(activity: Activity, filter: String) {
        // Special case when user opens the deals and popular categories.
        when (filter) {
            "Deals" -> {
                // The collection name for PRODUCTS
                dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
                    .whereGreaterThan(Constants.FIELD_SALE, 0) // Only return products with a sale percentage bigger than 0.
                    .orderBy(Constants.FIELD_SALE, Query.Direction.DESCENDING)
                    .get() // Will get the documents snapshots.
                    .addOnSuccessListener { document ->

                        // Here we get the list of boards in the form of documents.
                        Log.d("Products On-Sale List", document.documents.toString())

                        // Here we have created a new instance for Products ArrayList.
                        val productsList: ArrayList<Product> = ArrayList()

                        // A for loop as per the list of documents to convert them into Products ArrayList.
                        for (i in document.documents) {

                            val product = i.toObject(Product::class.java)
                            product!!.id = i.id

                            productsList.add(product)
                        }
                        when (activity) {
                            is ListProductsActivity -> {
                                activity.successOProductsListFromFirestore(productsList)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Get Product On-Sale List", "Error while getting product on-sale list.", e)
                    }
                return
            }
            "Popular" -> {
                dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
                    .whereGreaterThan(Constants.FIELD_POPULARITY, 0)
                    .orderBy(Constants.FIELD_POPULARITY, Query.Direction.DESCENDING)
                    .get() // Will get the documents snapshots.
                    .addOnSuccessListener { document ->

                        // Here we get the list of boards in the form of documents.
                        Log.d("Popular Products List", document.documents.toString())

                        // Here we have created a new instance for Products ArrayList.
                        val productsList: ArrayList<Product> = ArrayList()

                        // A for loop as per the list of documents to convert them into Products ArrayList.
                        for (i in document.documents) {

                            val product = i.toObject(Product::class.java)
                            product!!.id = i.id

                            productsList.add(product)
                        }
                        when (activity) {
                            is ListProductsActivity -> {
                                activity.successOProductsListFromFirestore(productsList)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Get Popular Product List", "Error while getting popular products list.", e)
                    }
                return
            }
        }
        // If the above statement isn't valid we will proceed to get the products for a specific
        // product category
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .whereEqualTo(Constants.FIELD_CATEGORY, filter) // Getting products only from a certain category
            .orderBy(Constants.FIELD_NAME, Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Category Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    productsList.add(product)
                }
                when (activity) {
                    is ListProductsActivity -> {
                        activity.successOProductsListFromFirestore(productsList)
                    }
                    else -> {}
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Category Product List", "Error while getting category products list.", e)
            }
    }

    /**
     * A function to get the products list from cloud firestorm that are on sale.
     *
     * @param activity The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getCategoriesList(activity: Activity) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_CATEGORIES)
            .orderBy(Constants.FIELD_NAME, Query.Direction.ASCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Categories List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val categoriesList: ArrayList<Category> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val category = i.toObject(Category::class.java)
                    category!!.categoryId = i.id

                    categoriesList.add(category)
                }
                when (activity) {
                    is AllCategoriesActivity -> {
                        activity.successCategoriesListFromFirestore(categoriesList)
                    }
                    else -> {}
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Categories List", "Error while getting categories list.", e)
            }
    }


    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.d(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.EMARKET_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    user.fullName
                )
                editor.apply()

                when (activity) {
                    is MainActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is SignInActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(fragment: Fragment) {

        // Here we pass the collection name from which we wants the data.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.d(fragment.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    fragment.requireContext().getSharedPreferences(
                        Constants.EMARKET_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    user.fullName
                )
                editor.apply()

                when (fragment) {
                    is MyAccountFragment -> {
                        fragment.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (fragment) {
                    is MyAccountFragment -> {
                        fragment.unveilDetails()
                        // TODO Show error
                        /*fragment.hideProgressDialog()*/
                    }
                }

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting user details.",
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
                Log.d(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                activity.successProductDetailsFromFirestore(product)
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the product details.",
                    e
                )
            }
    }

    /**
     * A function to get the list of orders from cloud firestore.
     */
    fun getMyOrdersList(context: Context) {
        dbFirestore.collection(Constants.COLLECTION_ORDERS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(context.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                when (context) {
                    is ListOrdersActivity -> context.successOrdersListFromFirestore(list)
                }

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                when (context) {
                    is ListOrdersActivity -> context.hideProgressDialog()
                }

                Log.e(context.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

}
