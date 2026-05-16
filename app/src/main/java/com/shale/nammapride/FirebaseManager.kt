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
        appDataCollection.document("dashboard").addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("FirebaseManager", "Error listening to dashboard data", error)
                return@addSnapshotListener
            }
            snapshot?.toObject<DashboardData>()?.let { 
                android.util.Log.d("FirebaseManager", "Received dashboard data: $it")
                onSuccess(it) 
            }
        }
    }

    fun updateDashboardData(data: DashboardData, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("dashboard").set(data)
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { it.printStackTrace() }
    }

    // Facilities
    fun getFacilities(onSuccess: (List<Facility>) -> Unit) {
        appDataCollection.document("content").collection("facilities").addSnapshotListener { snapshot, _ ->
            val facilities = snapshot?.documents?.mapNotNull { it.toObject<Facility>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(facilities)
        }
    }

    fun updateFacility(facility: Facility, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("facilities").document(facility.id).set(facility)
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { it.printStackTrace() }
    }

    // Student Stars
    fun getStudentStars(onSuccess: (List<StudentStar>) -> Unit) {
        appDataCollection.document("content").collection("stars").addSnapshotListener { snapshot, _ ->
            val stars = snapshot?.documents?.mapNotNull { it.toObject<StudentStar>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(stars)
        }
    }

    fun updateStudentStar(star: StudentStar, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("stars").document(star.id).set(star)
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { it.printStackTrace() }
    }

    // Weekly Meals
    fun getWeeklyMeals(onSuccess: (List<WeeklyMeal>) -> Unit) {
        appDataCollection.document("content").collection("weekly_meals").orderBy("id").addSnapshotListener { snapshot, _ ->
            val meals = snapshot?.documents?.mapNotNull { it.toObject<WeeklyMeal>()?.copy(id = it.id) } ?: emptyList()
            onSuccess(meals)
        }
    }

    fun updateWeeklyMeal(meal: WeeklyMeal, onComplete: (() -> Unit)? = null) {
        appDataCollection.document("content").collection("weekly_meals").document(meal.id).set(meal)
            .addOnSuccessListener { onComplete?.invoke() }
            .addOnFailureListener { it.printStackTrace() }
    }

    // Image Upload
    fun uploadImage(context: android.content.Context, path: String, uri: android.net.Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$path/${java.util.UUID.randomUUID()}.jpg")
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                onFailure(Exception("Could not open image stream"))
                return
            }
            
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
            
            imageRef.putBytes(bytes, metadata)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        onSuccess(url.toString())
                    }.addOnFailureListener {
                        onFailure(Exception("Upload succeeded but failed to get download URL: ${it.message}"))
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
