package com.example

import com.example.data.ai.FoodQualityAiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMandatoryAiDisclaimerPresence() {
        val disclaimer = FoodQualityAiService.MANDATORY_DISCLAIMER
        assertTrue(disclaimer.contains("AI screening identifies possible visible warning signs only"))
        assertTrue(disclaimer.contains("It cannot confirm that food is safe to eat"))
    }

    @Test
    fun testAiQualityScreening_PerishableRoomTemperature_TriggersConcern() = runBlocking {
        val report = FoodQualityAiService.analyzeFoodQuality(
            foodName = "Chicken Stew",
            category = "Prepared Meals",
            storageMethod = "Room Temperature",
            packagingCondition = "Sealed Container",
            hygieneNotes = "Stored on kitchen counter",
            bitmap = null
        )

        assertEquals(FoodQualityAiService.STATUS_CONCERN, report.screeningStatus)
        assertTrue(report.potentialWarningSigns.contains("Danger Zone") || report.potentialWarningSigns.contains("Room Temperature"))
    }

    @Test
    fun testAiQualityScreening_DamagedPackaging_TriggersConcern() = runBlocking {
        val report = FoodQualityAiService.analyzeFoodQuality(
            foodName = "Pastry box",
            category = "Bakery",
            storageMethod = "Room Temperature",
            packagingCondition = "Damaged Container",
            hygieneNotes = "Dropped box",
            bitmap = null
        )

        assertEquals(FoodQualityAiService.STATUS_CONCERN, report.screeningStatus)
    }

    @Test
    fun testAiQualityScreening_MissingPhoto_TriggersUnableToAssess() = runBlocking {
        val report = FoodQualityAiService.analyzeFoodQuality(
            foodName = "Fresh Apples",
            category = "Fresh Produce",
            storageMethod = "Refrigerated",
            packagingCondition = "Sealed Container",
            hygieneNotes = "Clean crates",
            bitmap = null
        )

        assertEquals(FoodQualityAiService.STATUS_UNABLE, report.screeningStatus)
        assertTrue(report.limitations.contains("No image file provided"))
    }
}
