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
data class Popularity(
    val favoritesCount: Int = 0,
    val purchasedCount: Int = 0,
) : Parcelable
