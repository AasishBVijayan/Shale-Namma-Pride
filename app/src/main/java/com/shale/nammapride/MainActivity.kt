package com.shale.nammapride

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.shale.nammapride.databinding.ActivityMainBinding
import kotlin.math.abs

import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseManager = FirebaseManager.getInstance()
    private var currentDashboardData = DashboardData()
    
    private var selectedFacilityForImage: Facility? = null
    private var selectedStarForImage: StudentStar? = null
    private var isUploadingDashboardImage = false

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadSelectedImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        setupLanguageToggle()
        
        // Set User Name
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !user.isAnonymous) {
            binding.tvUserName.text = user.displayName ?: "Admin"
        } else {
            binding.tvUserName.text = getString(R.string.dashboard_guest)
        }

        loadData(isAdmin)
        setupAdminDashboard(isAdmin)
        
        // Navigation to Meal Detail
        binding.cardNutrition.setOnClickListener {
            val intentDetail = Intent(this, MealDetailActivity::class.java)
            intentDetail.putExtra("IS_ADMIN", isAdmin)
            startActivity(intentDetail)
        }

        // Logout logic
        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun loadData(isAdmin: Boolean) {
        // Load Dashboard Nutrition
        firebaseManager.getDashboardData { data ->
            currentDashboardData = data
            binding.tvTodayMealName.text = data.todayMealName
            if (data.imageUrl != null) {
                // Assuming there's an ImageView for the dashboard meal. 
                // In activity_main.xml, line 105 is an ImageView in FrameLayout.
                // It doesn't have an ID. I should add one or find it.
                // Wait, line 105: <ImageView android:layout_width="match_parent" ... />
                // I'll add an ID to it in the XML or just find it by index if needed.
                // Let's assume I added android:id="@+id/ivDashboardMeal"
                findViewById<ImageView>(R.id.ivDashboardMeal)?.load(data.imageUrl)
            }
        }

        // Load Facilities
        firebaseManager.getFacilities { facilities ->
            if (facilities.isEmpty() && isAdmin) {
                initializeDefaultFacilities()
            } else {
                val adapter = FacilityAdapter(facilities, isAdmin) { facility ->
                    selectedFacilityForImage = facility
                    pickImageLauncher.launch("image/*")
                }
                binding.facilityViewPager.adapter = adapter
            }
        }

        // Load Student Stars
        firebaseManager.getStudentStars { stars ->
            if (stars.isEmpty() && isAdmin) {
                initializeDefaultStars()
            } else {
                binding.studentStarsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.studentStarsRecyclerView.adapter = StarsAdapter(stars, isAdmin) { star ->
                    selectedStarForImage = star
                    pickImageLauncher.launch("image/*")
                }
            }
        }
    }

    private fun uploadSelectedImage(uri: android.net.Uri) {
        val toast = Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT)
        toast.show()
        
        val path = when {
            isUploadingDashboardImage -> "dashboard"
            selectedFacilityForImage != null -> "facilities"
            selectedStarForImage != null -> "stars"
            else -> "misc"
        }

        firebaseManager.uploadImage(this, path, uri, { url ->
            toast.cancel()
            Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
            
            when {
                isUploadingDashboardImage -> {
                    firebaseManager.updateDashboardData(currentDashboardData.copy(imageUrl = url))
                    isUploadingDashboardImage = false
                }
                selectedFacilityForImage != null -> {
                    firebaseManager.updateFacility(selectedFacilityForImage!!.copy(imageUrl = url))
                    selectedFacilityForImage = null
                }
                selectedStarForImage != null -> {
                    firebaseManager.updateStudentStar(selectedStarForImage!!.copy(imageUrl = url))
                    selectedStarForImage = null
                }
            }
        }, {
            toast.cancel()
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun initializeDefaultFacilities() {
        val facilities = listOf(
            Facility("f1", getString(R.string.facility_science)),
            Facility("f2", getString(R.string.facility_library)),
            Facility("f3", getString(R.string.facility_playground)),
            Facility("f4", getString(R.string.facility_computer))
        )
        facilities.forEach { firebaseManager.updateFacility(it) }
    }

    private fun initializeDefaultStars() {
        val stars = listOf(
            StudentStar("s1", getString(R.string.star_aarav), getString(R.string.star_aarav_achieve)),
            StudentStar("s2", getString(R.string.star_diya), getString(R.string.star_diya_achieve)),
            StudentStar("s3", getString(R.string.star_kiran), getString(R.string.star_kiran_achieve)),
            StudentStar("s4", getString(R.string.star_sneha), getString(R.string.star_sneha_achieve))
        )
        stars.forEach { firebaseManager.updateStudentStar(it) }
    }

    private fun performLogout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Sign out from Google to clear the account selection cache
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupLanguageToggle() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val isKannada = currentLocales.toLanguageTags() == "kn"
        
        binding.tvCurrentLanguage.text = if (isKannada) "KN" else "EN"
        
        binding.btnLanguageToggle.setOnClickListener {
            if (isKannada) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("kn"))
            }
        }
    }

    private fun setupAdminDashboard(isAdmin: Boolean) {
        if (isAdmin) {
            binding.btnEditDashboardNutrition.visibility = View.VISIBLE
            binding.btnEditDashboardNutrition.setOnClickListener {
                showEditDashboardDialog()
            }
        } else {
            binding.btnEditDashboardNutrition.visibility = View.GONE
        }
    }

    private fun showEditDashboardDialog() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
        }
        
        val nameEdit = android.widget.EditText(this).apply {
            hint = "Meal Name"
            setText(currentDashboardData.todayMealName)
        }
        val reportEdit = android.widget.EditText(this).apply {
            hint = "Nutrition Report"
            setText(currentDashboardData.nutritionReport)
        }
        val btnPickImage = android.widget.Button(this).apply {
            text = "Change Image"
            setOnClickListener {
                isUploadingDashboardImage = true
                pickImageLauncher.launch("image/*")
            }
        }
        
        layout.addView(nameEdit)
        layout.addView(reportEdit)
        layout.addView(btnPickImage)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.dashboard_nutrition_header))
            .setView(layout)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newName = nameEdit.text.toString()
                val newReport = reportEdit.text.toString()
                if (newName.isNotEmpty()) {
                    val progressToast = Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT)
                    progressToast.show()
                    firebaseManager.updateDashboardData(
                        currentDashboardData.copy(todayMealName = newName, nutritionReport = newReport)
                    )
                    progressToast.cancel()
                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

}
