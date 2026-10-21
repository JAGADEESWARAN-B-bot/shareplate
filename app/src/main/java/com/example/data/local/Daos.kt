package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminAuditLog
import com.example.data.model.Donation
import com.example.data.model.FoodRequest
import com.example.data.model.NotificationItem
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY createdAt DESC")
    fun getAllDonationsFlow(): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE status = 'Available' ORDER BY createdAt DESC")
    fun getAvailableDonationsFlow(): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE donorId = :donorId ORDER BY createdAt DESC")
    fun getDonationsByDonorFlow(donorId: String): Flow<List<Donation>>

    @Query("SELECT * FROM donations WHERE id = :id LIMIT 1")
    suspend fun getDonationById(id: String): Donation?

    @Query("SELECT COUNT(*) FROM donations")
    fun getTotalDonationsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM donations WHERE status = 'Available'")
    fun getActiveDonationsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM donations WHERE status = 'Pending Review'")
    fun getPendingReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM donations WHERE status = 'Completed'")
    fun getCompletedExchangesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation)

    @Update
    suspend fun updateDonation(donation: Donation)

    @Query("UPDATE donations SET status = :status WHERE id = :id")
    suspend fun updateDonationStatus(id: String, status: String)

    @Query("DELETE FROM donations WHERE id = :id")
    suspend fun deleteDonation(id: String)
}

@Dao
interface FoodRequestDao {
    @Query("SELECT * FROM food_requests ORDER BY createdAt DESC")
    fun getAllRequestsFlow(): Flow<List<FoodRequest>>

    @Query("SELECT * FROM food_requests WHERE recipientId = :recipientId ORDER BY createdAt DESC")
    fun getRequestsByRecipientFlow(recipientId: String): Flow<List<FoodRequest>>

    @Query("SELECT COUNT(*) FROM food_requests")
    fun getTotalRequestsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: FoodRequest)

    @Update
    suspend fun updateRequest(request: FoodRequest)

    @Query("UPDATE food_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}

@Dao
interface AdminAuditDao {
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentAuditLogs(): Flow<List<AdminAuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLog)
}
