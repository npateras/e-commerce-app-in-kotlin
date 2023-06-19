package com.unipi.mpsp21043.admin.utils

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

    // Default Constants
    const val DEFAULT_CURRENCY: String = "€"
    val SNACKBAR_BEHAVIOR = BaseTransientBottomBar.Behavior().apply {
        setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY) }

    // General Constants
    const val PRODUCT_IMAGE: String = "PRODUCT_IMAGE"
    const val ROLE_USER = "user"
    const val ROLE_ADMIN = "admin"
    const val STATUS_SUCCESS = "success"
    const val STATUS_ERROR = "error"
    val standardSimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

    // Firebase Constants
    // Collections
    const val COLLECTION_ADDRESSES: String = "addresses"
    const val COLLECTION_ORDERS: String = "orders"
    const val COLLECTION_PRODUCTS: String = "Products"
    const val COLLECTION_USERS: String = "users"

    // Fields
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
    const val FIELD_EMAIL: String = "email"
    const val FIELD_NAME: String = "name"
    const val FIELD_ROLE: String = "role"
    const val FIELD_NOTIFICATIONS: String = "notifications"
    const val FIELD_PHONE_CODE: String = "phoneCode"
    const val FIELD_PHONE_NUMBER: String = "phoneNumber"
    const val FIELD_POPULARITY: String = "popularity"
    const val FIELD_PROF_IMG_URL: String = "profImgUrl"
    const val FIELD_SALE: String = "sale"
    const val FIELD_STOCK: String = "stock"
    const val FIELD_TITLE: String = "title"
    const val FIELD_ORDER_DATE: String = "orderDate"
    const val FIELD_ORDER_STATUS: String = "orderStatus"
    const val FIELD_PAYMENT_METHOD: String = "paymentMethod"
    const val FIELD_SUB_TOTAL_AMOUNT: String = "subTotalAmount"
    const val FIELD_TOTAL_AMOUNT: String = "totalAmount"
    const val FIELD_USER_ID: String = "userId"

    // Intent Extras
    const val EXTRA_PRODUCT_ID: String = "extraProductId"
    const val EXTRA_USER_ID: String = "extraUserId"
    const val EXTRA_PRODUCT_MODEL: String = "extra_product_model"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"
    const val EXTRA_AUTHENTICATE_CHANGE_PASSWORD: String = "extraAuthenticateChangePassword"
    const val EXTRA_SNACKBAR_MESSAGE: String = "extraSnackbarMessage"
    const val EXTRA_SNACKBAR_TYPE: String = "extraSnackbarType"
    const val EXTRA_USER_EMAIL: String = "extraUserEmail"
    const val EXTRA_USER_DETAILS: String = "extraUserDetails"
    const val EXTRA_ORDER_DETAILS: String = "extraOrderDetails"

    // Request Codes
    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val READ_STORAGE_PERMISSION_CODE = 2
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    // Storage Paths
    const val STORAGE_PATH_USERS: String = "Users/"

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
