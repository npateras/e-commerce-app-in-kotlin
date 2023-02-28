package com.unipi.mpsp21043.emarketadmin.database

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unipi.mpsp21043.emarketadmin.models.*
import com.unipi.mpsp21043.emarketadmin.ui.activities.*
import com.unipi.mpsp21043.emarketadmin.ui.fragments.OrdersFragment
import com.unipi.mpsp21043.emarketadmin.ui.fragments.ProductsFragment
import com.unipi.mpsp21043.emarketadmin.ui.fragments.UsersFragment
import com.unipi.mpsp21043.emarketadmin.utils.Constants

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
                Log.e(Constants.TAG,
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

    /*fun getUserAdminStatus(signInActivity: SignInActivity) {
        dbFirestore.collection(Constants.COLLECTION_USERS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnCompleteListener { task ->

                if (task.result!!.isEmpty) {
                    signInActivity.successUserAdminStatusFromFirestore(User())
                    return@addOnCompleteListener
                }
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        // Here we get the list of boards in the form of documents.
                        Log.d("User", document.toString())

                        val userDetails: User = document.toObject(User::class.java)

                        signInActivity.successUserAdminStatusFromFirestore(userDetails)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Favorites", "Error while getting favorites of product.", e)
            }
    }*/

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
     * A function to add address to the cloud firestore.
     *
     * @param activity
     * @param productInfo
     */
    fun addProduct(activity: AddEditProductActivity, productHashMap: HashMap<String, Any>) {

        // Collection name address.
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(productHashMap, SetOptions.merge())
            .addOnSuccessListener {

                Log.e(activity.javaClass.simpleName, "New product added!")

                // Here call a function of base activity for transferring the result to it.
                activity.successUpdateProductToFirestore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding product.",
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
     * A function to update the existing product to the cloud firestore.
     *
     * @param activity Base class
     * @param productInfo Which fields are to be updated.
     * @param productId existing product id
     */
    fun updateProduct(activity: AddEditProductActivity, productHashMap: HashMap<String, Any>, productId: String) {

        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .document(productId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(productHashMap, SetOptions.merge())
            .addOnSuccessListener {

                Log.e(activity.javaClass.simpleName, "Product updated!")

                // Here call a function of base activity for transferring the result to it.
                activity.successUpdateProductToFirestore()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating product.",
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
     * A function to delete an existing product from the Cloud Firestore.
     *
     * @param activity Base class
     * @param productId existing address id
     */
    fun deleteProduct(activity: Activity, productId: String) {

        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is ProductDetailsActivity -> activity.deleteProductSuccess()
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ProductDetailsActivity -> activity.hideProgressDialog()
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    /**
     * A function to update the existing user details to the cloud firestore.
     *
     * @param activity Base class
     * @param userInfo Which fields are to be updated.
     */
    fun updateProfile(activity: EditProfileActivity, userHashMap: HashMap<String, Any>) {

        dbFirestore.collection(Constants.COLLECTION_USERS)
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userHashMap, SetOptions.merge())
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

    fun getProductsList(fragment: Fragment, orderBy: String, direction: Query.Direction) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .orderBy(orderBy, direction)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Products List (Sorted)", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    productsList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Products List (Sorted)", "Error while getting products list.", e)
            }
    }

    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .orderBy(Constants.FIELD_DATE_ADDED, Query.Direction.DESCENDING)
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
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Products List", "Error while getting products list.", e)
            }
    }

    /**
     * A function to get the products list from cloud firestore that are on sale.
     *
     * @param fragmentFavorites The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    /*fun getFavoritesList(fragmentFavorites: FavoritesFragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .orderBy(Constants.FIELD_DATE_ADDED, Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.d("Favorites List", document.documents.toString())

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
    }*/

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

                when (activity) {
                    is MainActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is SignInActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                    is UserDetailsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userDetailsSuccess(user)
                    }
                    is MyAccountActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyAccountActivity -> {
                        activity.showErrorUI()
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
    fun getUserDetails(activity: Activity, userId: String, type: String) {

        // Here we pass the collection name from which we wants the data.
        dbFirestore.collection(Constants.COLLECTION_USERS)
            // The document id to get the Fields of user.
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                Log.d(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                when (activity) {
                    is ProductDetailsActivity -> {
                        if (type == "added")
                            activity.successAddedByUserDetailsFromFirestore(user)
                        else if (type == "modified")
                            activity.successLastModifiedByUserDetailsFromFirestore(user)
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
    fun getOrdersList(fragment: Fragment) {
        dbFirestore.collection(Constants.COLLECTION_ORDERS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                when (fragment) {
                    is OrdersFragment -> fragment.successOrdersListFromFirestore(list)
                }

            }
            .addOnFailureListener { e ->

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

    /**
     * A function to get the list of users from cloud firestore.
     */
    fun getUsersList(fragment: Fragment) {
        dbFirestore.collection(Constants.COLLECTION_USERS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    val user = i.toObject(User::class.java)!!
                    user.id = i.id

                    list.add(user)
                }

                when (fragment) {
                    is UsersFragment -> fragment.successUsersListFromFirestore(list)
                }

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                Log.e(fragment.javaClass.simpleName, "Error while getting the users list.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        // Getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is AddEditProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is EditProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is AddEditProductActivity -> {
                        activity.hideProgressDialog()
                    }

                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun getSearchResults(activity: Activity) {
        /*// The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
            .orderBy(Constants.FIELD_DATE_ADDED, Query.Direction.DESCENDING)
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
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Products List", "Error while getting products list.", e)
            }*/
    }

}
