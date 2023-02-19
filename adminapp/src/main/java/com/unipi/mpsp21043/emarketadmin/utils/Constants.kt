package com.unipi.mpsp21043.emarketadmin.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import java.text.SimpleDateFormat
import java.util.*

// Create a custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
/**
 * A custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
 */
object Constants {

    const val GROUP_KEY_FAVORITES: String = "com.unipi.mpsp21043.emarket.FAVORITES"
    const val NOTIFICATION_CHANNEL_ID: String = "com.unipi.mpsp21043.emarket"
    const val NOTIFICATION_ID : Int = 100

    // Default Constants
    const val DEFAULT_CURRENCY: String = "€"
    const val DEFAULT_MAX_ITEM_CART_QUANTITY: Int = 99
    const val DEFAULT_DELIVERY_COST: Double = 3.00
    val SNACKBAR_BEHAVIOR = BaseTransientBottomBar.Behavior().apply {
        setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY) }

    // General Constants
    const val TAG: String = "[eMarket]"
    const val EMARKET_PREFERENCES: String = "eMarketPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val PRODUCT_IMAGE: String = "PRODUCT_IMAGE"
    const val ROLE_USER = "user"
    const val ROLE_ADMIN = "admin"
    val standardSimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

    // Firebase Constants
    // This is used for the collection name for USERS.
    const val COLLECTION_ADDRESSES: String = "addresses"
    const val COLLECTION_CART_ITEMS: String = "cart_items"
    const val COLLECTION_CATEGORIES: String = "categories"
    const val COLLECTION_FAVORITES: String = "favorites"
    const val COLLECTION_ORDERS: String = "orders"
    const val COLLECTION_PRODUCTS: String = "Products"
    const val COLLECTION_USERS: String = "users"

    // Fields
    const val FIELD_CART_QUANTITY: String = "cartQuantity"
    const val FIELD_CATEGORY: String = "category"
    const val FIELD_ADDED_BY_USER: String = "addedByUser"
    const val FIELD_LAST_MODIFIED_BY: String = "lastModifiedBy"
    const val FIELD_PRICE: String = "price"
    const val FIELD_DESCRIPTION: String = "description"
    const val FIELD_WEIGHT: String = "weight"
    const val FIELD_WEIGHT_UNIT: String = "weightUnit"
    const val FIELD_ICON_URL: String = "iconUrl"

    const val FIELD_COMPLETE_PROFILE: String = "profileCompleted"
    const val FIELD_DATE_ADDED: String = "dateAdded"
    const val FIELD_FULL_NAME: String = "fullName"
    const val FIELD_NAME: String = "name"
    const val FIELD_NOTIFICATIONS: String = "notifications"
    const val FIELD_PHONE_CODE: String = "phoneCode"
    const val FIELD_PHONE_NUMBER: String = "phoneNumber"
    const val FIELD_POPULARITY: String = "popularity"
    const val FIELD_PRODUCT_ID: String = "productId"
    const val FIELD_PROF_IMG_URL: String = "profImgUrl"
    const val FIELD_REGISTRATION_TOKENS: String = "registrationTokens"
    const val FIELD_SALE: String = "sale"
    const val FIELD_STOCK: String = "stock"
    const val FIELD_USER_ID: String = "userId"

    // Intent Extras
    const val EXTRA_PRODUCT_ID: String = "extraProductId"
    const val EXTRA_USER_ID: String = "extraUserId"
    const val EXTRA_ORDER_ID: String = "extraOrderId"
    const val EXTRA_ADDRESS_MODEL: String = "extra_address_model"
    const val EXTRA_PRODUCT_MODEL: String = "extra_product_model"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_IS_IN_FAVORITES: String = "extraIsInFavorites"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"
    const val EXTRA_PROFILE_NOT_COMPLETED_SNACKBAR: String = "extraShowProfileNotCompletedSnackbar"
    const val EXTRA_SNACKBAR_MESSAGE: String = "extraSnackbarMessage"
    const val EXTRA_SNACKBAR_TYPE: String = "extraSnackbarType"
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_USER_DETAILS: String = "extraUserDetails"
    const val EXTRA_PRODUCT_DETAILS: String = "extraProductDetails"
    const val EXTRA_ORDER_DETAILS: String = "extraOrderDetails"
    const val EXTRA_FILTER: String = "extraFilter"
    const val EXTRA_CATEGORY_NAME: String = "extraCategoryName"
    const val EXTRA_NOTIFICATION_ID: String = "extraNotificationId"
    const val EXTRA_SHOW_ORDER_PLACED_SNACKBAR: String = "extraShowOrderPlacedSnackbar"

    const val PAYLOAD_PRODUCT_ID: String = "PRODUCT_ID"
    const val PAYLOAD_PRODUCT_IMG_URL: String = "PRODUCT_IMG_URL"
    const val PAYLOAD_PRODUCT_NAME: String = "PRODUCT_NAME"

    // Request Codes
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val READ_STORAGE_PERMISSION_CODE = 2
    //A unique code for asking the Location Permissions using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val LOCATION_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
    // A unique code of image selection from Phone Storage.
    const val GET_LOCATION_REQUEST_CODE = 1

    // Storage Paths
    const val STORAGE_PATH_USERS: String = "Users/"
    const val STORAGE_PATH_CATEGORY_BEVERAGES: String = "Beverages/"
    const val STORAGE_PATH_CATEGORY_CHILLED: String = "Chilled/"
    const val STORAGE_PATH_CATEGORY_FISH: String = "Fish/"
    const val STORAGE_PATH_CATEGORY_FROZEN: String = "Frozen/"
    const val STORAGE_PATH_CATEGORY_FRUITS: String = "Fruits/"
    const val STORAGE_PATH_CATEGORY_GROCERY: String = "Grocery/"
    const val STORAGE_PATH_CATEGORY_HOMEWARE: String = "Homeware/"
    const val STORAGE_PATH_CATEGORY_HOUSEHOLD: String = "Household/"
    const val STORAGE_PATH_CATEGORY_LIQUOR: String = "Liquor/"
    const val STORAGE_PATH_CATEGORY_MEAT: String = "Meat/"
    const val STORAGE_PATH_CATEGORY_PHARMACY: String = "Pharmacy/"
    const val STORAGE_PATH_CATEGORY_VEGETABLES: String = "Vegetables/"

    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooserV2(activityResultLauncher: ActivityResultLauncher<Intent>) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // galleryIntent.type = "image/*"
        activityResultLauncher.launch(galleryIntent)
    }

    /**
     * A function to get the image file extension of the selected image.
     *
     * @param activity Activity reference.
     * @param uri Image file uri.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
// END
