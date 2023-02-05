package com.unipi.mpsp21043.emarketadmin.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

/**
 * A data model class with required fields.
 */
@Keep
@Parcelize
@IgnoreExtraProperties
data class Cart(
    var userId: String = "",
    var productId: String = "",

    val imgUrl: String = "",
    val name: String = "",
    val price: Double = 0.00,
    val sale: Double = 0.0,
    var stock: Int = 0,
    val weight: Int = 0,
    val weightUnit: String = "",
    var cartQuantity: Int = 0,
    var id: String = "",
) : Parcelable
