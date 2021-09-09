package com.unipi.p17172.emarket.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar

// Create a custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
/**
 * A custom object to declare all the constant values in a single file. The constant values declared here is can be used in whole application.
 */
object Constants {

    const val GROUP_KEY_FAVORITES: String = "com.unipi.p17172.emarket.FAVORITES"
    const val NOTIFICATION_CHANNEL_ID: String = "com.unipi.p17172.emarket"
    const val NOTIFICATION_ID : Int = 100

    // Default Constants
    const val DEFAULT_CURRENCY: String = "€"
    const val DEFAULT_VEILED_ITEMS_HORIZONTAL: Int = 4
    const val DEFAULT_VEILED_ITEMS_VERTICAL: Int = 15
    const val DEFAULT_CART_ITEM_QUANTITY: Int = 1
    const val DEFAULT_MAX_ITEM_CART_QUANTITY: Int = 99
    val SNACKBAR_BEHAVIOR = BaseTransientBottomBar.Behavior().apply {
        setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY) }

    // General Constants
    const val TAG: String = "[eMarket]"
    const val EMARKET_PREFERENCES: String = "eMarketPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"


    // Firebase Constants
    // This is used for the collection name for USERS.
    const val COLLECTION_USERS: String = "users"
    const val COLLECTION_CATEGORIES: String = "categories"
    const val COLLECTION_PRODUCTS: String = "Products"
    const val COLLECTION_FAVORITES: String = "favorites"
    const val COLLECTION_NOTIFICATIONS: String = "notifications"
    const val COLLECTION_CART_ITEMS: String = "cart_items"

    // Fields
    const val FIELD_ADDED_BY_USER: String = "addedByUser"
    const val FIELD_ADDRESS: String = "address"
    const val FIELD_CATEGORY: String = "category"
    const val FIELD_CATEGORY_ID: String = "categoryId"
    const val FIELD_COUNTRY: String = "country"
    const val FIELD_DATE_ADDED: String = "dateAdded"
    const val FIELD_DATE_REGISTERED: String = "dateRegistered"
    const val FIELD_DESCRIPTION: String = "description"
    const val FIELD_EMAIL: String = "email"
    const val FIELD_FULL_NAME: String = "fullName"
    const val FIELD_ICON_URL: String = "iconUrl"
    const val FIELD_ID: String = "id"
    const val FIELD_LAST_NAME: String = "lastName"
    const val FIELD_NAME: String = "name"
    const val FIELD_PHONE_CODE: String = "phoneCode"
    const val FIELD_PHONE_NUMBER: String = "phoneNumber"
    const val FIELD_PRICE: String = "price"
    const val FIELD_PROFILE_COMPLETED: String = "profileCompleted"
    const val FIELD_SALE: String = "sale"
    const val FIELD_USER_ID: String = "userId"
    const val FIELD_PRODUCT_ID: String = "productId"
    const val FIELD_STOCK: String = "stock"
    const val FIELD_WEIGHT: String = "weight"
    const val FIELD_WEIGHT_UNIT: String = "weightUnit"
    const val FIELD_ZIP_CODE: String = "zipCode"
    const val FIELD_CART_QUANTITY: String = "cartQuantity"
    const val FIELD_CART_ID: String = "cartId"
    const val FIELD_REGISTRATION_TOKENS: String = "registrationTokens"

    // Intent Extras
    const val EXTRA_PRODUCT_ID: String = "extraProductId"
    const val EXTRA_IS_IN_FAVORITES: String = "extraIsInFavorites"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"
    const val EXTRA_PROFILE_NOT_COMPLETED_SNACKBAR: String = "extraShowProfileNotCompletedSnackbar"
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_USER_DETAILS: String = "extraUserDetails"
    const val EXTRA_FILTER: String = "extraFilter"
    const val EXTRA_CATEGORY_ID: String = "extraCategoryId"
    const val EXTRA_CATEGORY_NAME: String = "extraCategoryName"
    const val EXTRA_NOTIFICATION_ID: String = "extraNotificationId"

    const val PAYLOAD_PRODUCT_ID: String = "PRODUCT_ID"
    const val PAYLOAD_PRODUCT_IMG_URL: String = "PRODUCT_IMG_URL"
    const val PAYLOAD_PRODUCT_NAME: String = "PRODUCT_NAME"

    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val USER_REGISTERED_REQUEST_CODE = 1
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

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
