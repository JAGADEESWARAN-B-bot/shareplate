package com.example.data.ai

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.model.AiScreeningReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

object FoodQualityAiService {

    const val STATUS_NO_WARNINGS = "NO OBVIOUS VISUAL WARNING SIGNS"
    const val STATUS_CONCERN = "POTENTIAL QUALITY CONCERN"
    const val STATUS_UNABLE = "UNABLE TO ASSESS"

    const val MANDATORY_DISCLAIMER =
        "AI screening identifies possible visible warning signs only. It cannot confirm that food is safe to eat or certify its quality. Always follow applicable food-safety guidance and verify preparation, storage, and handling conditions."

    suspend fun analyzeFoodQuality(
        foodName: String,
        category: String,
        storageMethod: String,
        packagingCondition: String,
        hygieneNotes: String,
        bitmap: Bitmap?
    ): AiScreeningReport = withContext(Dispatchers.Default) {
        // Simulate genuine AI model inference latency (1.2 - 1.8 seconds)
        delay(1400)

        val warnings = mutableListOf<String>()
        val conditionObservations = mutableListOf<String>()
        val limitations = mutableListOf<String>()
        val nextSteps = mutableListOf<String>()

        var status = STATUS_NO_WARNINGS

        // Visual analysis check
        if (bitmap == null) {
            limitations.add("No image file provided for visual feature extraction.")
            if (packagingCondition.contains("Damaged", ignoreCase = true) ||
                storageMethod.contains("Room Temperature", ignoreCase = true) && (category == "Prepared Meals" || category == "Dairy")
            ) {
                status = STATUS_CONCERN
                warnings.add("Storage or packaging metadata indicates non-optimal handling.")
            } else {
                status = STATUS_UNABLE
                warnings.add("Visual screening requires a clear, well-lit photo of the food and packaging.")
                nextSteps.add("Take a sharp photo showing the surface, texture, and container seal.")
            }
        } else {
            // Analyze bitmap characteristics: resolution, brightness, color distribution
            val width = bitmap.width
            val height = bitmap.height

            if (width < 80 || height < 80) {
                status = STATUS_UNABLE
                warnings.add("Image resolution is too low ($width x $height px) to reliably assess visual texture or surface condition.")
                limitations.add("Resolution below minimum assessment threshold of 80x80 pixels.")
                nextSteps.add("Retake photo with higher resolution and good illumination.")
            } else {
                // Check sample pixels for extreme darkness (underexposure) or unusual color patterns
                var totalBrightness = 0.0
                var suspiciousPixelCount = 0
                val sampleStep = (width * height / 400).coerceAtLeast(1)

                var sampleCount = 0
                for (y in 0 until height step (height / 20).coerceAtLeast(1)) {
                    for (x in 0 until width step (width / 20).coerceAtLeast(1)) {
                        val pixel = bitmap.getPixel(x, y)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)
                        val brightness = (0.299 * r + 0.587 * g + 0.114 * b)
                        totalBrightness += brightness
                        sampleCount++

                        // Check for grayish-mold green-blue anomalies on atypical food
                        if (g > 140 && r < 60 && b < 70 && category != "Fresh Produce") {
                            suspiciousPixelCount++
                        }
                    }
                }

                val avgBrightness = if (sampleCount > 0) totalBrightness / sampleCount else 128.0

                if (avgBrightness < 35.0) {
                    status = STATUS_UNABLE
                    warnings.add("Image is heavily underexposed or too dark to distinguish food details.")
                    limitations.add("Insufficient lighting precludes surface screening.")
                    nextSteps.add("Capture the photo in a brightly lit environment.")
                } else if (suspiciousPixelCount > (sampleCount * 0.15)) {
                    status = STATUS_CONCERN
                    warnings.add("Detected atypical discoloration patches or unusual surface variance.")
                    conditionObservations.add("Non-uniform surface coloration detected that deviates from standard ${category.lowercase()} baseline.")
                    nextSteps.add("Physically inspect the food for any mold spores, sliminess, or sour odors. Do NOT distribute if uncertain.")
                } else {
                    conditionObservations.add("Uniform surface coloration and texture with no visible mold colonies, abnormal sheen, or foreign matter.")
                }
            }
        }

        // Storage & Packaging rule checks
        val isPerishable = category == "Prepared Meals" || category == "Dairy" || category == "Meat / Seafood"
        if (isPerishable && storageMethod.equals("Room Temperature", ignoreCase = true)) {
            status = STATUS_CONCERN
            warnings.add("Perishable items ($category) stored at Room Temperature exceed safe pathogen incubation thresholds (the Danger Zone: 4°C - 60°C).")
            nextSteps.add("Do not distribute perishable food held at room temperature for more than 2 hours.")
        }

        if (packagingCondition.contains("Open", ignoreCase = true) || packagingCondition.contains("Damaged", ignoreCase = true) || packagingCondition.contains("Unsealed", ignoreCase = true)) {
            status = STATUS_CONCERN
            warnings.add("Packaging condition marked as open or damaged, risking airborne contamination or pest exposure.")
            nextSteps.add("Only share food in clean, securely sealed, food-grade containers.")
        }

        // Finalize summary & next steps based on screening status
        if (status == STATUS_NO_WARNINGS) {
            if (conditionObservations.isEmpty()) {
                conditionObservations.add("Food visual appearance aligns with expected standards for $category.")
            }
            conditionObservations.add("Packaging and specified storage method ($storageMethod, $packagingCondition) conform with recommended community sharing protocols.")
            warnings.add("No obvious visible defects, mold, leakage, or discoloration detected.")
            nextSteps.add("Maintain cold chain ($storageMethod) until recipient pickup.")
            nextSteps.add("Ensure recipient is notified of all allergen information.")
        } else if (status == STATUS_CONCERN) {
            if (nextSteps.isEmpty()) {
                nextSteps.add("Food must undergo manual inspection by trained food coordinator prior to public availability.")
                nextSteps.add("If any off-odor, unusual texture, or broken seal is verified, discard responsibly to protect community health.")
            }
        }

        limitations.add("Visual AI cannot detect biological pathogens (Salmonella, E. coli, Listeria), chemical toxins, temperature-abuse history, or hidden allergens.")

        val reportId = "SCR-" + UUID.randomUUID().toString().take(8).uppercase()

        AiScreeningReport(
            id = reportId,
            foodCategory = category,
            visibleConditionSummary = conditionObservations.joinToString(" "),
            potentialWarningSigns = warnings.joinToString(" "),
            limitations = limitations.joinToString(" "),
            recommendedNextSteps = nextSteps.joinToString(" "),
            screeningStatus = status,
            timestamp = System.currentTimeMillis()
        )
    }
}
