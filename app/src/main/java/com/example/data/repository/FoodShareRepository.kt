package com.example.data.repository

import com.example.data.local.FoodShareDatabase
import com.example.data.model.AdminAuditLog
import com.example.data.model.Donation
import com.example.data.model.FoodRequest
import com.example.data.model.NotificationItem
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FoodShareRepository(
    private val database: FoodShareDatabase,
    private val scope: CoroutineScope
) {
    private val userDao = database.userDao()
    private val donationDao = database.donationDao()
    private val requestDao = database.foodRequestDao()
    private val notificationDao = database.notificationDao()
    private val auditDao = database.adminAuditDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        // Initialize with default logged in user (Green Garden Bistro for rich donor & browsing experience)
        scope.launch {
            val defaultUser = userDao.getUserById("usr_bistro") ?: userDao.getUserById("usr_admin")
            _currentUser.value = defaultUser
        }
    }

    val allDonations: Flow<List<Donation>> = donationDao.getAllDonationsFlow()
    val availableDonations: Flow<List<Donation>> = donationDao.getAvailableDonationsFlow()
    val allRequests: Flow<List<FoodRequest>> = requestDao.getAllRequestsFlow()
    val totalDonationsCount: Flow<Int> = donationDao.getTotalDonationsCount()
    val activeDonationsCount: Flow<Int> = donationDao.getActiveDonationsCount()
    val pendingReviewCount: Flow<Int> = donationDao.getPendingReviewCount()
    val completedExchangesCount: Flow<Int> = donationDao.getCompletedExchangesCount()
    val totalRequestsCount: Flow<Int> = requestDao.getTotalRequestsCount()
    val allUsers: Flow<List<User>> = userDao.getAllUsersFlow()
    val userCount: Flow<Int> = userDao.getUserCount()
    val recentAuditLogs: Flow<List<AdminAuditLog>> = auditDao.getRecentAuditLogs()

    fun getDonationsByDonor(donorId: String): Flow<List<Donation>> {
        return donationDao.getDonationsByDonorFlow(donorId)
    }

    fun getRequestsByRecipient(recipientId: String): Flow<List<FoodRequest>> {
        return requestDao.getRequestsByRecipientFlow(recipientId)
    }

    fun getNotifications(userId: String): Flow<List<NotificationItem>> {
        return notificationDao.getNotificationsForUser(userId)
    }

    suspend fun getDonationById(id: String): Donation? {
        return donationDao.getDonationById(id)
    }

    suspend fun submitDonation(donation: Donation) {
        donationDao.insertDonation(donation)
        // Add notification for donor
        notificationDao.insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = donation.donorId,
                title = "Donation Submitted: ${donation.foodName}",
                message = if (donation.status == "Available")
                    "Your listing is now live in the community discovery feed!"
                else
                    "Your donation is under safety review by our community coordinator.",
                type = "donation"
            )
        )
    }

    suspend fun updateDonationStatus(donationId: String, newStatus: String, adminName: String? = null) {
        val donation = donationDao.getDonationById(donationId)
        donationDao.updateDonationStatus(donationId, newStatus)
        if (donation != null) {
            notificationDao.insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    userId = donation.donorId,
                    title = "Donation Status: $newStatus",
                    message = "Status of '${donation.foodName}' updated to $newStatus.",
                    type = "status"
                )
            )
            if (adminName != null) {
                auditDao.insertAuditLog(
                    AdminAuditLog(
                        id = "LOG-" + UUID.randomUUID().toString().take(6).uppercase(),
                        adminName = adminName,
                        action = "STATUS_CHANGE_$newStatus",
                        targetType = "Donation",
                        targetId = donationId,
                        details = "Changed status of '${donation.foodName}' to $newStatus"
                    )
                )
            }
        }
    }

    suspend fun submitFoodRequest(request: FoodRequest) {
        requestDao.insertRequest(request)
        notificationDao.insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = request.recipientId,
                title = "Food Request Posted",
                message = "Your request for '${request.category}' (${request.quantity}) is published for local food donors.",
                type = "request"
            )
        )
    }

    suspend fun updateRequestStatus(requestId: String, status: String) {
        requestDao.updateRequestStatus(requestId, status)
    }

    suspend fun loginAsUser(user: User) {
        _currentUser.value = user
    }

    suspend fun loginByEmail(email: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return if (user != null) {
            _currentUser.value = user
            true
        } else {
            false
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        accountType: String,
        location: String
    ): User {
        val newUser = User(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            fullName = fullName,
            email = email,
            phone = phone,
            accountType = accountType,
            location = location,
            isAdmin = false
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        return newUser
    }

    fun logout() {
        // Fall back to a default view or clear
        scope.launch {
            val guest = userDao.getUserById("usr_sarah")
            _currentUser.value = guest
        }
    }

    suspend fun markNotificationsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
    }
}
