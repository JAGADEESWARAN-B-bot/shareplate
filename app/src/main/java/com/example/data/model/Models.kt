package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val accountType: String, // Individual, Restaurant, NGO/Charity, Community Organization
    val location: String,
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey val id: String,
    val foodName: String,
    val category: String, // Prepared Meals, Fresh Produce, Bakery, Dairy, Pantry & Canned, Beverages
    val description: String,
    val quantity: Double,
    val unit: String, // portions, kg, lbs, items, boxes, liters
    val servings: Int,
    val prepDateTime: String,
    val storageMethod: String, // Refrigerated, Frozen, Room Temperature, Heated Container
    val location: String,
    val deadline: String,
    val contactMethod: String, // WhatsApp, Phone Call, In-App Chat
    val contactPhone: String,
    val packagingCondition: String, // Sealed Container, Original Packaging, Clean Box, Foil Wrapped
    val hygieneNotes: String,
    val allergenInfo: String, // None, Contains Nuts, Dairy, Gluten, etc.
    val pricingType: String, // Free Donation, Fixed-Price / Subsidized
    val priceAmount: Double = 0.0,
    val status: String, // Pending Review, Available, Reserved, Completed, Rejected
    val donorId: String,
    val donorName: String,
    val donorType: String,
    val imageUri: String = "",
    val aiScreeningStatus: String = "PENDING", // NO OBVIOUS VISUAL WARNING SIGNS, POTENTIAL QUALITY CONCERN, UNABLE TO ASSESS, PENDING
    val aiSummary: String = "",
    val aiWarningSigns: String = "",
    val aiNextSteps: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_requests")
data class FoodRequest(
    @PrimaryKey val id: String,
    val recipientId: String,
    val recipientName: String,
    val category: String,
    val quantity: String,
    val servingsNeeded: Int,
    val neededDateTime: String,
    val location: String,
    val dietaryRequirements: String, // Vegetarian, Vegan, Halal, Kosher, None
    val allergies: String,
    val additionalNotes: String,
    val contactPreference: String,
    val contactPhone: String,
    val status: String = "Open", // Open, Matched, Fulfilled, Cancelled
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // donation, request, safety, admin
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLog(
    @PrimaryKey val id: String,
    val adminName: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiScreeningReport(
    val id: String,
    val foodCategory: String,
    val visibleConditionSummary: String,
    val potentialWarningSigns: String,
    val limitations: String,
    val recommendedNextSteps: String,
    val screeningStatus: String, // NO OBVIOUS VISUAL WARNING SIGNS, POTENTIAL QUALITY CONCERN, UNABLE TO ASSESS
    val timestamp: Long = System.currentTimeMillis()
)
