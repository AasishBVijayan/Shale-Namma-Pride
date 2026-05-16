package com.shale.nammapride

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class AITranslator private constructor() {

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.KANNADA)
        .build()

    private val englishToKannadaTranslator = Translation.getClient(options)
    
    private val reverseOptions = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.KANNADA)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()
        
    private val kannadaToEnglishTranslator = Translation.getClient(reverseOptions)

    fun downloadModelsIfNeeded(onComplete: (Boolean) -> Unit) {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        
        englishToKannadaTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                kannadaToEnglishTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun translate(text: String, toKannada: Boolean, onResult: (String) -> Unit) {
        val translator = if (toKannada) englishToKannadaTranslator else kannadaToEnglishTranslator
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                onResult(translatedText)
            }
            .addOnFailureListener {
                onResult(text) // Fallback to original text on failure
            }
    }

    companion object {
        @Volatile
        private var instance: AITranslator? = null

        fun getInstance(): AITranslator {
            return instance ?: synchronized(this) {
                instance ?: AITranslator().also { instance = it }
            }
        }
    }
}
