package com.unipi.mpsp21043.admin.utils

import android.text.InputFilter
import android.text.Spanned


class InputFilterMinMax : InputFilter {
    private var min: Int
    private var max: Int

    constructor(min: Int, max: Int) {
        this.min = min
        this.max = max
    }

    constructor(min: String, max: String) {
        this.min = min.toInt()
        this.max = max.toInt()
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            // Removes string that is to be replaced from destination
            // and adds the new string in.
            val newVal =
                (dest.subSequence(0, dstart) // Note that below "toString()" is the only required:
                    .toString() + source.subSequence(start, end).toString()
                        + dest.subSequence(dend, dest.length))
            val input = newVal.toInt()
            if (isInRange(min, max, input)) return null
        } catch (_: NumberFormatException) { }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}
