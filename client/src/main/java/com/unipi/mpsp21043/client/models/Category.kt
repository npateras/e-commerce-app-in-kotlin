package com.unipi.mpsp21043.client.models

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
data class Category(
    val name: String = "",
    val imgUrl: String = "",
    val description: String = "",
    var categoryId: String = "",
) : Parcelable
