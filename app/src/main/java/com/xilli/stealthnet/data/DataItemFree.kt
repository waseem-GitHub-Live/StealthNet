package com.xilli.stealthnet.data

import android.widget.RadioButton

data class DataItemFree(
    val title: String,
    val IPdescription: String,
    val flagimageUrl: Int,
    val signal:Int,
    var radioButtonChecked: Boolean = false
)
