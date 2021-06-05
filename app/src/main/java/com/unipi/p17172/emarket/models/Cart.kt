package com.unipi.p17172.emarket.models

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
    var cartQuantity: Int = 1,
    val imgUrl: String = "",
    val name: String = "",
    val price: Double = 0.00,
    val sale: Float = 0f,
    val stock: Int = 0,
    val weight: Int = 0,
    val weightUnit: String = "",
    var id: String = "",
) : Parcelable