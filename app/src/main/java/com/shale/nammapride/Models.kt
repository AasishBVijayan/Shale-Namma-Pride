package com.shale.nammapride

data class Facility(
    val name: String,
    val imageResId: Int? = null
)

data class StudentStar(
    val name: String,
    val achievement: String,
    val imageResId: Int? = null
)
