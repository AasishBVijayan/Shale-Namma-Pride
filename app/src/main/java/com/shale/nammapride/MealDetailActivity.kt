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

class MealDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        binding.btnBack.setOnClickListener {
            finish()
        }
        
        setupLanguageToggle()
        setupWeeklyTimetable()
        setupAdminFeatures()
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
    
    private fun setupWeeklyTimetable() {
        val meals = listOf(
            WeeklyMeal(getString(R.string.day_mon), getString(R.string.meal_mon)),
            WeeklyMeal(getString(R.string.day_tue), getString(R.string.meal_tue)),
            WeeklyMeal(getString(R.string.day_wed), getString(R.string.meal_wed)),
            WeeklyMeal(getString(R.string.day_thu), getString(R.string.meal_thu), isToday = true),
            WeeklyMeal(getString(R.string.day_fri), getString(R.string.meal_fri)),
            WeeklyMeal(getString(R.string.day_sat), getString(R.string.meal_sat))
        )
        
        binding.rvWeeklyTimetable.layoutManager = LinearLayoutManager(this)
        binding.rvWeeklyTimetable.adapter = WeeklyMealAdapter(meals)
    }

    private fun setupAdminFeatures() {
        if (isAdmin) {
            binding.btnEditMeal.visibility = View.VISIBLE
            binding.btnEditMeal.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Edit Meal Details")
                    .setMessage("Admin editing options would appear here (e.g., change status to Not Served).")
                    .setPositiveButton("OK", null)
                    .show()
            }
        } else {
            binding.btnEditMeal.visibility = View.GONE
        }
    }
}
