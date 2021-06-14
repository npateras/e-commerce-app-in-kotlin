package com.unipi.p17172.emarket.models

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * A data model class with required fields.
 */
@Parcelize
@IgnoreExtraProperties
data class Favorite(
    @ServerTimestamp
    val dateAdded: Date = Date(),
    var userId: String = "",
    var productId: String = "",
    val imgUrl: String = "",
    val name: String = "",
    val price: Double = 0.00,
    val sale: Float = 0f,
    val stock: Int = 0,
    var id: String = "",
) : Parcelable