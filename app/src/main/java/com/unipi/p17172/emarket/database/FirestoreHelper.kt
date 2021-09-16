package com.unipi.p17172.emarket.database

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
import com.unipi.p17172.emarket.models.*
import com.unipi.p17172.emarket.ui.activities.*
import com.unipi.p17172.emarket.ui.fragments.FavoritesFragment
import com.unipi.p17172.emarket.ui.fragments.HomeFragment
import com.unipi.p17172.emarket.ui.fragments.MyAccountFragment
import com.unipi.p17172.emarket.ui.fragments.MyCartFragment
import com.unipi.p17172.emarket.utils.Constants





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
     * A function to get all the product list from the cloud firestore.
     *
     * @param fragment The fragment is passed as parameter to the function because it is called from fragment and need to the success result.
     */
    fun getAllProductsList(fragment: MyCartFragment) {
        // The collection name for PRODUCTS
        dbFirestore.collection(Constants.COLLECTION_PRODUCTS)
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

                fragment.successProductsListFromFireStore(productsList)
            }
            .addOnFailureListener { e ->
                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    // region -Cart Management-
    /**
     * A function to get the cart items list from the cloud firestore.
     *
     * @param fragment
     */
    fun getCartItemsList(fragment: MyCartFragment) {
        // The collection name for Cart Items
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for cart items ArrayList.
                val cartItemsList: ArrayList<Cart> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val cart = i.toObject(Cart::class.java)!!
                    cart.id = i.id

                    cartItemsList.add(cart)
                }

                fragment.successCartItemsListFromFireStore(cartItemsList)
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error while getting the cart items list of the user.", e)
            }
    }

    /**
     * A function to update the existing cart details to the cloud firestore.
     *
     * @param activity Base class
     * @param cartItem Which fields are to be updated.
     */
    fun addItemToCart(activity: ProductDetailsActivity, cartItem: Cart) {
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.successItemAddedToCart()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding item to cart.",
                    e
                )
            }
    }

    /**
     * A function to update the existing cart item from the cloud firestore.
     *
     * @param activity Base class
     * @param cartItem Which fields are to be updated.
     * @param productId product id of the item.
     */
    fun updateItemFromCart(activity: ProductDetailsActivity, cartItem: Cart, productId: String) {

        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.isEmpty)
                        activity.hideProgressDialog()

                    for (document in task.result!!) {
                        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
                            .document(document.id)
                            .set(cartItem, SetOptions.merge())
                            .addOnSuccessListener {
                                activity.hideProgressDialog()
                            }
                            .addOnFailureListener { e ->
                                activity.hideProgressDialog()
                                Log.e(
                                    activity.javaClass.simpleName,
                                    "Error while updating the cart item.",
                                    e
                                )
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting cart item while updating.",
                    e
                )
            }
    }

    /**
     * A function to delete the existing cart item from the cloud firestore.
     *
     * @param activity Base class
     * @param productId product id of the item.
     */
    fun deleteItemFromCart(context: Context, productId: String) {

        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    when (context) {
                        is ProductDetailsActivity -> {
                            if (task.result!!.isEmpty)
                                context.hideProgressDialog()

                            for (document in task.result!!) {
                                dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        context.successItemDeletedFromCart()
                                    }
                                    .addOnFailureListener { e ->
                                        context.hideProgressDialog()
                                        Log.e(
                                            context.javaClass.simpleName,
                                            "Error while deleting the cart item.",
                                            e
                                        )
                                    }
                            }
                        }
                        MyCartFragment().requireContext() -> {
                            if (task.result!!.isEmpty)
                                MyCartFragment().hideProgressDialog()

                            for (document in task.result!!) {
                                dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        MyCartFragment().itemRemovedSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        MyCartFragment().hideProgressDialog()
                                        Log.e(
                                            context.javaClass.simpleName,
                                            "Error while deleting the cart item.",
                                            e
                                        )
                                    }
                            }
                        }
                    }

                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is ProductDetailsActivity -> {
                        context.hideProgressDialog()
                        Log.e(
                            context.javaClass.simpleName,
                            "Error while getting cart item while removal.",
                            e
                        )
                    }
                    MyCartFragment().requireContext() -> {
                        MyCartFragment().hideProgressDialog()
                        Log.e(
                            context.javaClass.simpleName,
                            "Error while getting cart item while removal.",
                            e
                        )
                    }
                }
            }
    }

    /**
     * A function to update the cart item in the cloud firestore.
     *
     * @param context activity class.
     * @param cart_id cart id of the item.
     * @param itemHashMap to be updated values.
     */
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    MyCartFragment().requireContext() -> {
                        MyCartFragment().itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    MyCartFragment().requireContext() -> {
                        MyCartFragment().hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
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
    fun registerUser(activity: SignUpActivity, userInfo: User) {

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
     * A function to get the products list from cloud firestore that are on sale.
     *
     * @param fragmentFavorites The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getFavoritesList(fragmentFavorites: FavoritesFragment) {
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
    }

    fun getFavoriteProduct(productDetailsActivity: ProductDetailsActivity, productId: String) {
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get() // Will get the documents snapshots.
            .addOnCompleteListener { task ->

                if (task.result!!.isEmpty) {
                    productDetailsActivity.successFavoriteProductFromFirestore(Favorite())
                    return@addOnCompleteListener
                }
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        // Here we get the list of boards in the form of documents.
                        Log.d("Favorite Product", document.toString())

                        val favoriteProduct: Favorite = document.toObject(Favorite::class.java)

                        productDetailsActivity.successFavoriteProductFromFirestore(favoriteProduct)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Favorites", "Error while getting favorites of product.", e)
            }
    }

    fun checkIfProductIsInCart(activity: Activity, productId: String) {
        dbFirestore.collection(Constants.COLLECTION_CART_ITEMS)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get() // Will get the documents snapshots.
            .addOnCompleteListener { task ->

                if (task.result!!.isEmpty) {
                    when (activity) {
                        is ProductDetailsActivity -> activity.successCartItemFromFirestore(Cart())
                    }
                    return@addOnCompleteListener
                }
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        // Here we get the list of boards in the form of documents.
                        Log.d("Cart Item", document.toString())

                        val cartItem: Cart = document.toObject(Cart::class.java)

                        when (activity) {
                            is ProductDetailsActivity -> activity.successCartItemFromFirestore(cartItem)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Favorites", "Error while getting favorites of product.", e)
            }
    }

    fun addToFavorites(fragment: Fragment, favorite: Favorite) {
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .document()
            .set(favorite, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("", "task success")

                when (fragment) {
                    is HomeFragment -> {}
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while adding a product to user's favorites.",
                    e
                )
            }
    }

    fun addToFavorites(activity: Activity, favorite: Favorite) {
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .document()
            .set(favorite, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding a product to user's favorites.",
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

    fun deleteFavoriteProduct(productDetailsActivity: ProductDetailsActivity, productId: String) {
        dbFirestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo(Constants.FIELD_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.FIELD_PRODUCT_ID, productId)
            .get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        dbFirestore
                            .collection(Constants.COLLECTION_FAVORITES)
                            .document(document.id)
                            .delete()
                    }
                }
                else {
                    Log.d(productDetailsActivity.javaClass.simpleName, "Error getting documents: ", task.exception);
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    productDetailsActivity.javaClass.simpleName,
                    "Error while getting product from user's favorites.",
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
}
