package com.unipi.mpsp21043.emarket.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * A data model class with required fields.
 */
@Keep
@Parcelize
@IgnoreExtraProperties
data class Favorite(
    var userId: String = "",
    var productId: String = "",
    val imgUrl: String = "",
    val name: String = "",
    val price: Double = 0.00,
    val sale: Double = 0.00,
    @ServerTimestamp
    val dateAdded: Date = Date(),
    var id: String = ""
) : Parcelable
