package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminAuditLog
import com.example.data.model.Donation
import com.example.data.model.FoodRequest
import com.example.data.model.NotificationItem
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Donation::class,
        FoodRequest::class,
        NotificationItem::class,
        AdminAuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FoodShareDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun donationDao(): DonationDao
    abstract fun foodRequestDao(): FoodRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun adminAuditDao(): AdminAuditDao

    companion object {
        @Volatile
        private var INSTANCE: FoodShareDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FoodShareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodShareDatabase::class.java,
                    "food_share_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: FoodShareDatabase) {
            val userDao = database.userDao()
            val donationDao = database.donationDao()
            val requestDao = database.foodRequestDao()
            val auditDao = database.adminAuditDao()

            // Seed Users
            val adminUser = User(
                id = "usr_admin",
                fullName = "Admin Coordinator",
                email = "admin@foodshare.org",
                phone = "+1 (555) 019-2831",
                accountType = "NGO/Charity",
                location = "Central City Civic Center",
                isAdmin = true
            )
            val bistroUser = User(
                id = "usr_bistro",
                fullName = "Green Garden Bistro",
                email = "bistro@greengarden.com",
                phone = "+1 (555) 234-5678",
                accountType = "Restaurant",
                location = "Downtown District, 4th Ave"
            )
            val bakeryUser = User(
                id = "usr_bakery",
                fullName = "Artisan Hearth Bakery",
                email = "orders@artisanhearth.com",
                phone = "+1 (555) 876-5432",
                accountType = "Restaurant",
                location = "North Market Square"
            )
            val individualUser = User(
                id = "usr_sarah",
                fullName = "Sarah Jenkins",
                email = "sarah.j@example.com",
                phone = "+1 (555) 345-6789",
                accountType = "Individual",
                location = "Maplewood Community"
            )
            val pantryUser = User(
                id = "usr_hope",
                fullName = "Hope Community Center",
                email = "pantry@hopecommunity.org",
                phone = "+1 (555) 987-6543",
                accountType = "Community Organization",
                location = "Riverdale East District"
            )

            userDao.insertUser(adminUser)
            userDao.insertUser(bistroUser)
            userDao.insertUser(bakeryUser)
            userDao.insertUser(individualUser)
            userDao.insertUser(pantryUser)

            // Seed Donations
            val donation1 = Donation(
                id = "DON-2026-8041",
                foodName = "Fresh Vegetable Lentil Soup & Bread",
                category = "Prepared Meals",
                description = "Nutritious slow-simmered vegetable lentil soup prepared this afternoon in certified commercial kitchen. Packed in thermal containers.",
                quantity = 25.0,
                unit = "portions",
                servings = 25,
                prepDateTime = "Today, 12:30 PM",
                storageMethod = "Refrigerated",
                location = "Downtown District, 4th Ave",
                deadline = "Today by 8:00 PM",
                contactMethod = "WhatsApp",
                contactPhone = "+15552345678",
                packagingCondition = "Sealed Container",
                hygieneNotes = "Handled strictly following commercial hygiene standards. Heated and chilled per safety protocol.",
                allergenInfo = "Gluten (separate bread), Celery",
                pricingType = "Free Donation",
                priceAmount = 0.0,
                status = "Available",
                donorId = bistroUser.id,
                donorName = bistroUser.fullName,
                donorType = bistroUser.accountType,
                imageUri = "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=600&q=80",
                aiScreeningStatus = "NO OBVIOUS VISUAL WARNING SIGNS",
                aiSummary = "Clear broth, fresh diced vegetables, and intact sealed thermal containers. No signs of cloudiness or spoilage.",
                aiWarningSigns = "None identified from visible inspection.",
                aiNextSteps = "Ensure continuous refrigeration below 4°C until pickup."
            )

            val donation2 = Donation(
                id = "DON-2026-9122",
                foodName = "Artisanal Sourdough & Crusty Baguettes",
                category = "Bakery",
                description = "Baked fresh this morning. 14 crusty sourdough loaves and 10 French baguettes remaining after lunch closing.",
                quantity = 24.0,
                unit = "items",
                servings = 35,
                prepDateTime = "Today, 6:00 AM",
                storageMethod = "Room Temperature",
                location = "North Market Square",
                deadline = "Tomorrow, 11:00 AM",
                contactMethod = "WhatsApp",
                contactPhone = "+15558765432",
                packagingCondition = "Clean Paper Bakery Bags",
                hygieneNotes = "Stored in dry, covered bakery racks away from moisture.",
                allergenInfo = "Contains Gluten, Wheat",
                pricingType = "Free Donation",
                priceAmount = 0.0,
                status = "Available",
                donorId = bakeryUser.id,
                donorName = bakeryUser.fullName,
                donorType = bakeryUser.accountType,
                imageUri = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80",
                aiScreeningStatus = "NO OBVIOUS VISUAL WARNING SIGNS",
                aiSummary = "Golden brown crust, uniform crumb structure, completely free of mold or moisture blemishes.",
                aiWarningSigns = "None observed.",
                aiNextSteps = "Keep sealed in dry ambient packaging."
            )

            val donation3 = Donation(
                id = "DON-2026-7319",
                foodName = "Organic Honeycrisp Apples & Sweet Pears",
                category = "Fresh Produce",
                description = "Surplus orchard fruit from local farmers market crate. Firm, washed, and ready for immediate consumption.",
                quantity = 15.0,
                unit = "kg",
                servings = 40,
                prepDateTime = "Yesterday, 4:00 PM",
                storageMethod = "Refrigerated",
                location = "Maplewood Community Center",
                deadline = "In 2 days, 6:00 PM",
                contactMethod = "WhatsApp",
                contactPhone = "+15553456789",
                packagingCondition = "Clean Wooden & Cardboard Crates",
                hygieneNotes = "Stored in temperature-controlled cooler.",
                allergenInfo = "None",
                pricingType = "Free Donation",
                priceAmount = 0.0,
                status = "Available",
                donorId = individualUser.id,
                donorName = individualUser.fullName,
                donorType = individualUser.accountType,
                imageUri = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=600&q=80",
                aiScreeningStatus = "NO OBVIOUS VISUAL WARNING SIGNS",
                aiSummary = "Firm skin, natural healthy coloration, no visible bruising, fungal spots, or weeping skin.",
                aiWarningSigns = "None.",
                aiNextSteps = "Distribute promptly or keep chilled to maximize freshness."
            )

            val donation4 = Donation(
                id = "DON-2026-6450",
                foodName = "Roasted Mediterranean Herb Vegetables & Quinoa",
                category = "Prepared Meals",
                description = "Catering surplus from corporate lunch event: roasted bell peppers, zucchini, eggplant, and fluffy herb quinoa.",
                quantity = 12.0,
                unit = "boxes",
                servings = 20,
                prepDateTime = "Today, 1:15 PM",
                storageMethod = "Refrigerated",
                location = "Downtown District, 4th Ave",
                deadline = "Today by 9:00 PM",
                contactMethod = "In-App Chat",
                contactPhone = "+15552345678",
                packagingCondition = "Sealed Catering Trays",
                hygieneNotes = "Prepared and sealed in commercial pans.",
                allergenInfo = "Contains Soy (tamari herb seasoning)",
                pricingType = "Free Donation",
                priceAmount = 0.0,
                status = "Pending Review",
                donorId = bistroUser.id,
                donorName = bistroUser.fullName,
                donorType = bistroUser.accountType,
                imageUri = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80",
                aiScreeningStatus = "NO OBVIOUS VISUAL WARNING SIGNS",
                aiSummary = "Visual indicators look consistent with freshly roasted vegetables; trays are foil-sealed.",
                aiWarningSigns = "Pending manual coordinator checklist verification.",
                aiNextSteps = "Awaiting admin food-safety review before publishing."
            )

            donationDao.insertDonation(donation1)
            donationDao.insertDonation(donation2)
            donationDao.insertDonation(donation3)
            donationDao.insertDonation(donation4)

            // Seed Requests
            val request1 = FoodRequest(
                id = "REQ-2026-5501",
                recipientId = pantryUser.id,
                recipientName = pantryUser.fullName,
                category = "Prepared Meals",
                quantity = "30-40 portions",
                servingsNeeded = 35,
                neededDateTime = "Today by 6:30 PM",
                location = "Riverdale East Community Hall",
                dietaryRequirements = "Halal / Vegetarian options preferred",
                allergies = "Nut-free environment requested",
                additionalNotes = "For our evening community dinner program supporting vulnerable families.",
                contactPreference = "WhatsApp",
                contactPhone = "+15559876543",
                status = "Open"
            )

            val request2 = FoodRequest(
                id = "REQ-2026-4412",
                recipientId = "usr_senior_care",
                recipientName = "Senior Wellness Center",
                category = "Bakery",
                quantity = "15-20 loaves/packs",
                servingsNeeded = 25,
                neededDateTime = "Tomorrow morning by 9:00 AM",
                location = "Sunnybrook Senior Residences",
                dietaryRequirements = "Low sodium if available",
                allergies = "None",
                additionalNotes = "For morning breakfast tea and community gathering.",
                contactPreference = "Phone Call",
                contactPhone = "+15554321098",
                status = "Open"
            )

            requestDao.insertRequest(request1)
            requestDao.insertRequest(request2)

            // Seed Audit Log
            auditDao.insertAuditLog(
                AdminAuditLog(
                    id = "LOG-001",
                    adminName = adminUser.fullName,
                    action = "APPROVED_DONATION",
                    targetType = "Donation",
                    targetId = donation1.id,
                    details = "Approved after reviewing food safety guidelines & temperature records."
                )
            )
        }
    }
}
