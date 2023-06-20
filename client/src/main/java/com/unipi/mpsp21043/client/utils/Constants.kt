package com.unipi.mpsp21043.client.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import java.text.SimpleDateFormat
import java.util.Locale

// Create a custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
/**
 * A custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
 */
object Constants {

    const val FCM_BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAJ9iX3YA:APA91bFfDW7Q4i9gtbDDbOpYuv6hHUHqOL-H9lLWU5uv6_qb7SiSrbnGX7zmStKwyXPzFDvOG4WitQ5aE0pbNOzl7MBbrtPAlO-uyXxrWs1quqwDHCyVTeMEiAR2w4XZyxQRbE4RphY4"
    const val CONTENT_TYPE = "application/json"

    const val GROUP_KEY_FAVORITES: String = "com.unipi.mpsp21043.emarket.favorites"
    const val NOTIFICATION_CHANNEL_ID: String = "eMarketNotificationChannelId"
    const val NOTIFICATION_CHANNEL_NAME: String = "eMarketNotificationChannelName"
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
    const val PREF_NIGHT_MODE: String = "NIGHT_MODE"
    const val PREF_LANGUAGE: String = "LANGUAGE"
    const val PREF_IS_FIRST_TIME_OPENING: String = "IS_FIRST_TIME_OPENING"

    val DATE_TIME_FORMAT = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
    val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    // Firebase Constants
    // This is used for the collection name for USERS.
    const val COLLECTION_USERS: String = "users"
    const val COLLECTION_CATEGORIES: String = "categories"
    const val COLLECTION_PRODUCTS: String = "Products"
    const val COLLECTION_FAVORITES: String = "favorites"
    const val COLLECTION_ADDRESSES: String = "addresses"
    const val COLLECTION_CART_ITEMS: String = "cart_items"
    const val COLLECTION_ORDERS: String = "orders"

    // Fields
    const val FIELD_POPULARITY: String = "popularity"
    const val FIELD_CATEGORY: String = "category"
    const val FIELD_DATE_ADDED: String = "dateAdded"
    const val FIELD_ORDER_DATE: String = "orderDate"
    const val FIELD_NAME: String = "name"
    const val FIELD_SALE: String = "sale"
    const val FIELD_ID: String = "id"
    const val FIELD_USER_ID: String = "userId"
    const val FIELD_PRODUCT_ID: String = "productId"
    const val FIELD_CART_QUANTITY: String = "cartQuantity"
    const val FIELD_TOKENS: String = "tokens"
    const val FIELD_STOCK: String = "stock"
    const val FIELD_FULL_NAME: String = "fullName"
    const val FIELD_EMAIL: String = "email"
    const val FIELD_NOTIFICATIONS: String = "notifications"
    const val FIELD_PHONE_CODE: String = "phoneCode"
    const val FIELD_PHONE_NUMBER: String = "phoneNumber"
    const val FIELD_PROF_IMG_URL: String = "profImgUrl"
    const val FIELD_COMPLETE_PROFILE: String = "profileCompleted"

    // Intent Extras
    const val EXTRA_PRODUCT_ID: String = "extraProductId"
    const val EXTRA_ADDRESS_MODEL: String = "extra_address_model"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_IS_IN_FAVORITES: String = "extraIsInFavorites"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"
    const val EXTRA_PROFILE_NOT_COMPLETED_SNACKBAR: String = "extraShowProfileNotCompletedSnackbar"
    const val EXTRA_AUTHENTICATE_CHANGE_PASSWORD: String = "extraAuthenticateChangePassword"
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_USER_DETAILS: String = "extraUserDetails"
    const val EXTRA_ORDER_DETAILS: String = "extraOrderDetails"
    const val EXTRA_FILTER: String = "extraFilter"
    const val EXTRA_CATEGORY_NAME: String = "extraCategoryName"
    const val EXTRA_NOTIFICATION_ID: String = "extraNotificationId"
    const val EXTRA_SHOW_ORDER_PLACED_SNACKBAR: String = "extraShowOrderPlacedSnackbar"

    const val PAYLOAD_PRODUCT_ID: String = "PRODUCT_ID"
    const val PAYLOAD_PRODUCT_IMG_URL: String = "PRODUCT_IMG_URL"
    const val PAYLOAD_PRODUCT_NAME: String = "PRODUCT_NAME"

    // Request Codes
    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val READ_STORAGE_PERMISSION_CODE = 2
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    // Storage Paths
    const val STORAGE_PATH_USERS: String = "Users/"

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
