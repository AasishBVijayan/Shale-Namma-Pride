package com.shale.nammapride

data class Facility(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val imageResId: Int? = null
)

data class StudentStar(
    val id: String = "",
    val name: String = "",
    val achievement: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val imageResId: Int? = null
)

data class WeeklyMeal(
    val id: String = "",
    val day: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val isToday: Boolean = false
)

data class DashboardData(
    val todayMealName: String = "",
    val nutritionReport: String = "",
    val imageUrl: String? = null
)
