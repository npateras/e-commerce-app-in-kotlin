package com.unipi.mpsp21043.client.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * A data model class with required fields.
 */
@Keep
@Parcelize
@IgnoreExtraProperties
data class Notification(
    @ServerTimestamp
    val dateCreated: Date = Date(),
    val userId: String = "",
    var id: String = "",
) : Parcelable
