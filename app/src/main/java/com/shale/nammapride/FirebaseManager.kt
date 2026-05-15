package com.shale.nammapride

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FirebaseManager {

    private val db = FirebaseFirestore.getInstance()
    private val appDataCollection = db.collection("app_data")

    companion object {
        private var instance: FirebaseManager? = null
        fun getInstance(): FirebaseManager {
            if (instance == null) {
                instance = FirebaseManager()
            }
            return instance!!
        }
    }

    // Dashboard Data
    fun getDashboardData(onSuccess: (DashboardData) -> Unit) {
        appDataCollection.document("dashboard").addSnapshotListener { snapshot, _ ->
            snapshot?.toObject<DashboardData>()?.let { onSuccess(it) }
        }
    }

    fun updateDashboardData(data: DashboardData, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("dashboard").set(data).addOnCompleteListener { onComplete?.invoke() }
    }

    // Facilities
    fun getFacilities(onSuccess: (List<Facility>) -> Unit) {
        appDataCollection.document("content").collection("facilities").addSnapshotListener { snapshot, _ ->
            val facilities = snapshot?.documents?.mapNotNull { it.toObject<Facility>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(facilities)
        }
    }

    fun updateFacility(facility: Facility, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("facilities").document(facility.id).set(facility).addOnCompleteListener { onComplete?.invoke() }
    }

    // Student Stars
    fun getStudentStars(onSuccess: (List<StudentStar>) -> Unit) {
        appDataCollection.document("content").collection("stars").addSnapshotListener { snapshot, _ ->
            val stars = snapshot?.documents?.mapNotNull { it.toObject<StudentStar>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(stars)
        }
    }

    fun updateStudentStar(star: StudentStar, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("stars").document(star.id).set(star).addOnCompleteListener { onComplete?.invoke() }
    }

    // Weekly Meals
    fun getWeeklyMeals(onSuccess: (List<WeeklyMeal>) -> Unit) {
        appDataCollection.document("content").collection("weekly_meals").orderBy("id").addSnapshotListener { snapshot, _ ->
            val meals = snapshot?.documents?.mapNotNull { it.toObject<WeeklyMeal>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(meals)
        }
    }

    fun updateWeeklyMeal(meal: WeeklyMeal, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("weekly_meals").document(meal.id).set(meal).addOnCompleteListener { onComplete?.invoke() }
    }

    // Image Upload
    fun uploadImage(context: android.content.Context, path: String, uri: android.net.Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/$path/${System.currentTimeMillis()}.jpg")
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                onFailure(Exception("Could not open image stream"))
                return
            }
            
            imageRef.putStream(inputStream)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        onSuccess(url.toString())
                    }
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
