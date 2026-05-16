package com.shale.nammapride

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.shale.nammapride.databinding.ActivityMealDetailBinding

import androidx.activity.result.contract.ActivityResultContracts
import coil.load

class MealDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding
    private val firebaseManager = FirebaseManager.getInstance()
    private var isAdmin: Boolean = false
    private var currentMealTitle: String = ""
    private var currentDashboardData = DashboardData()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadMealImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        binding.btnBack.setOnClickListener {
            finish()
        }
        
        setupLanguageToggle()
        loadData()
        setupAdminFeatures()
    }
    
    private fun loadData() {
        // Load Today's Meal Title
        firebaseManager.getDashboardData { data ->
            currentDashboardData = data
            currentMealTitle = data.todayMealName
            binding.tvMealTitle.text = currentMealTitle
            
            if (data.imageUrl != null) {
                binding.ivMealImage.load(data.imageUrl)
            }
        }

        // Load Weekly Timetable
        firebaseManager.getWeeklyMeals { meals ->
            if (meals.isEmpty() && isAdmin) {
                initializeDefaultMeals()
            } else {
                binding.rvWeeklyTimetable.layoutManager = LinearLayoutManager(this)
                binding.rvWeeklyTimetable.adapter = WeeklyMealAdapter(meals)
            }
        }
    }

    private fun uploadMealImage(uri: android.net.Uri) {
        val toast = Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT)
        toast.show()

        firebaseManager.uploadImage(this, "dashboard", uri, { url ->
            toast.cancel()
            Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
            firebaseManager.updateDashboardData(currentDashboardData.copy(imageUrl = url))
        }, {
            toast.cancel()
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun initializeDefaultMeals() {
        val meals = listOf(
            WeeklyMeal("m1", getString(R.string.day_mon), getString(R.string.meal_mon)),
            WeeklyMeal("m2", getString(R.string.day_tue), getString(R.string.meal_tue)),
            WeeklyMeal("m3", getString(R.string.day_wed), getString(R.string.meal_wed)),
            WeeklyMeal("m4", getString(R.string.day_thu), getString(R.string.meal_thu), isToday = true),
            WeeklyMeal("m5", getString(R.string.day_fri), getString(R.string.meal_fri)),
            WeeklyMeal("m6", getString(R.string.day_sat), getString(R.string.meal_sat))
        )
        meals.forEach { firebaseManager.updateWeeklyMeal(it) }
    }

    private fun setupLanguageToggle() {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val isKannada = currentLocales.toLanguageTags() == "kn"
        
        if (isKannada) {
            binding.btnLangEn.setBackgroundResource(R.drawable.pill_white)
            binding.btnLangEn.setTextColor(android.graphics.Color.parseColor("#888888"))
            binding.btnLangKn.setBackgroundResource(R.drawable.pill_blue)
            binding.btnLangKn.setTextColor(android.graphics.Color.WHITE)
        } else {
            binding.btnLangEn.setBackgroundResource(R.drawable.pill_blue)
            binding.btnLangEn.setTextColor(android.graphics.Color.WHITE)
            binding.btnLangKn.setBackgroundResource(R.drawable.pill_white)
            binding.btnLangKn.setTextColor(android.graphics.Color.parseColor("#888888"))
        }

        binding.btnLangEn.setOnClickListener {
            if (isKannada) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
            }
        }
        
        binding.btnLangKn.setOnClickListener {
            if (!isKannada) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("kn"))
            }
        }
    }
    
    private fun setupAdminFeatures() {
        if (isAdmin) {
            binding.btnEditMeal.visibility = View.VISIBLE
            binding.btnEditMeal.setOnClickListener {
                showEditMealDialog()
            }
        } else {
            binding.btnEditMeal.visibility = View.GONE
        }
    }

    private fun showEditMealDialog() {
        val dialogBinding = com.shale.nammapride.databinding.DialogEditGenericBinding.inflate(layoutInflater)
        
        dialogBinding.tvDialogTitle.text = getString(R.string.meal_nutrition_report)
        dialogBinding.etName.setText(currentMealTitle)
        dialogBinding.tilName.hint = "Meal Title"
        
        if (currentDashboardData.imageUrl != null) {
            dialogBinding.ivPreview.load(currentDashboardData.imageUrl)
        }
        
        dialogBinding.btnChangeImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        
        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newName = dialogBinding.etName.text.toString()
                if (newName.isNotEmpty()) {
                    val progressToast = Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT)
                    progressToast.show()
                    firebaseManager.updateDashboardData(currentDashboardData.copy(todayMealName = newName)) {
                        progressToast.cancel()
                        Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
