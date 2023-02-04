package com.unipi.mpsp21043.emarket.utils

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
    const val DEFAULT_VEILED_ITEMS_HORIZONTAL: Int = 4
    const val DEFAULT_VEILED_ITEMS_VERTICAL: Int = 15
    const val DEFAULT_MAX_ITEM_CART_QUANTITY: Int = 99
    const val DEFAULT_DELIVERY_COST: Double = 3.00
    val SNACKBAR_BEHAVIOR = BaseTransientBottomBar.Behavior().apply {
        setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY) }

    // General Constants
    const val TAG: String = "[eMarket]"
    const val EMARKET_PREFERENCES: String = "eMarketPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    val standardSimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

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
    const val FIELD_NAME: String = "name"
    const val FIELD_SALE: String = "sale"
    const val FIELD_USER_ID: String = "userId"
    const val FIELD_PRODUCT_ID: String = "productId"
    const val FIELD_CART_QUANTITY: String = "cartQuantity"
    const val FIELD_REGISTRATION_TOKENS: String = "registrationTokens"
    const val FIELD_STOCK: String = "stock"

    // Intent Extras
    const val EXTRA_PRODUCT_ID: String = "extraProductId"
    const val EXTRA_ADDRESS_MODEL: String = "extra_address_model"
    const val EXTRA_ADDRESS_DETAILS: String = "extra_address_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_IS_IN_FAVORITES: String = "extraIsInFavorites"
    const val EXTRA_REG_USERS_SNACKBAR: String = "extraShowRegisteredUserSnackbar"
    const val EXTRA_PROFILE_NOT_COMPLETED_SNACKBAR: String = "extraShowProfileNotCompletedSnackbar"
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

    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
}
// END
