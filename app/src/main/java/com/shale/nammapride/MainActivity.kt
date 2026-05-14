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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        setupLanguageToggle()
        setupViewPager(isAdmin)
        setupRecyclerView(isAdmin)
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

    private fun setupViewPager(isAdmin: Boolean) {
        val facilities = listOf(
            Facility(getString(R.string.facility_science)),
            Facility(getString(R.string.facility_library)),
            Facility(getString(R.string.facility_playground)),
            Facility(getString(R.string.facility_computer))
        )
        
        val adapter = FacilityAdapter(facilities, isAdmin)
        binding.facilityViewPager.adapter = adapter
        
        // Apply a scale/alpha PageTransformer to the ViewPager2 for a smooth visual effect
        binding.facilityViewPager.setPageTransformer { page, position ->
            val absPosition = abs(position)
            page.apply {
                val scale = if (absPosition > 1) 0.8f else 1 - absPosition * 0.2f
                scaleX = scale
                scaleY = scale
                alpha = if (absPosition > 1) 0.5f else 1 - absPosition * 0.5f
            }
        }
    }

    private fun setupRecyclerView(isAdmin: Boolean) {
        val stars = listOf(
            StudentStar(getString(R.string.star_aarav), getString(R.string.star_aarav_achieve)),
            StudentStar(getString(R.string.star_diya), getString(R.string.star_diya_achieve)),
            StudentStar(getString(R.string.star_kiran), getString(R.string.star_kiran_achieve)),
            StudentStar(getString(R.string.star_sneha), getString(R.string.star_sneha_achieve))
        )

        binding.studentStarsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.studentStarsRecyclerView.adapter = StarsAdapter(stars, isAdmin)
    }

    private fun setupAdminDashboard(isAdmin: Boolean) {
        if (isAdmin) {
            binding.btnEditDashboardNutrition.visibility = View.VISIBLE
            binding.btnEditDashboardNutrition.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Edit Today's Nutrition")
                    .setMessage("Admin editing options for Today's Nutrition would appear here.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        } else {
            binding.btnEditDashboardNutrition.visibility = View.GONE
        }
    }

}
