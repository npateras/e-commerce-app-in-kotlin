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
data class User(
    val address: String,
    val country: String,
    val zipCode: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileCompleted: Boolean,
    @ServerTimestamp
    val dateRegistered: Date,
    val phone: String,
    val phoneCode: Int,
    val pictureUrl: String,
    var id: String,
) : Parcelable