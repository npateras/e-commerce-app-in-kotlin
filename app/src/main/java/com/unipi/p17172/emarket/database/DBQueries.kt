package com.unipi.p17172.emarket.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DBQueries {}

fun getProducts(): Query {
    return FirebaseFirestore.getInstance()
        .collection("Products")
}

fun getFavorites(userId: String): Query {
    return FirebaseFirestore.getInstance()
        .collection("Favorites")
        .whereEqualTo("userId", userId)
}

fun getCart(userId: String): Query {
    return FirebaseFirestore.getInstance()
        .collection("Carts")
        .whereEqualTo("userId", userId)
}

fun getCategories(): Query {
    return FirebaseFirestore.getInstance()
        .collection("Categories")
}

fun getOrders(userId: String, is_completed: Boolean, sortBy: String): Query {
    return when (is_completed) {
        true -> {
            FirebaseFirestore.getInstance()
                .collection("Orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("order_status", "completed")
                .orderBy(sortBy)
        }
        else -> {
            FirebaseFirestore.getInstance()
                .collection("Orders")
                .whereEqualTo("userId", userId)
                .whereNotEqualTo("order_status", "completed")
                .orderBy(sortBy)
        }
    }
}

fun getNotifications(userId: String, sortBy: String): Query {
    return FirebaseFirestore.getInstance()
        .collection("Notifications")
        .whereEqualTo("userId", userId)
        .orderBy(sortBy)
}