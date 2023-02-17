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
data class Search(
    val id: String = "",
    val label1: String = "",
    val label2: String = "",
    val imgUrl: String = "",
) : Parcelable
