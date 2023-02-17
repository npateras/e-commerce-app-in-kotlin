package com.unipi.mpsp21043.emarketadmin.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
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
data class Product(
    var addedByUser: String = "",
    val lastModifiedBy: String = "",
    val category: String = "",
    @ServerTimestamp
    val dateAdded: Date = Date(),
    val description: String = "",
    var iconUrl: String = "",
    val name: String = "",
    val popularity: Int = 0,
    val price: Double = 0.00,
    val sale: Double = 0.00,
    val stock: Int = 0,
    val weight: Int = 0,
    val weightUnit: String = "",
    var id: String = "",
) : Parcelable
