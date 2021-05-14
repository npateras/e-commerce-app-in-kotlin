package com.unipi.p17172.emarket.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unipi.p17172.emarket.models.CategoryModel

class DBQueries {
    var db = FirebaseFirestore.getInstance()
    var categoryModelList: List<CategoryModel> = ArrayList()

    private fun getProducts(): Query {
        return db.collection("Products")
    }

}