package com.unipi.mpsp21043.emarketadmin.database

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
    //end region USER

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
     * @param userHashMap Which fields are to be updated.
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
     * A function to get the logged user details from from Firestore Database.
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

}
