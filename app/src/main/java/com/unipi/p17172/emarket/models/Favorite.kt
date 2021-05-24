package com.unipi.p17172.emarket.models

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

/**
 * A data model class with required fields.
 */
@Parcelize
@IgnoreExtraProperties
data class Favorite(
    val productId: String,
    var userId: String,
) : Parcelable