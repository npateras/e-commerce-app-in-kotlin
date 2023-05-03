package com.unipi.mpsp21043.admin.models

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
data class Address(
    val userId: String = "",
    val fullName: String = "",

    val phoneNumber: String = "",
    val phoneCode: Int = 0,

    val address: String = "",
    val zipCode: String = "",
    val additionalNote: String = "",

    var id: String = "",
) : Parcelable
