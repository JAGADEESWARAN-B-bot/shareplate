package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.FoodShareDatabase
import com.example.data.model.Donation
import com.example.data.model.FoodRequest
import com.example.data.model.User
import com.example.data.repository.FoodShareRepository
import com.example.ui.components.FoodShareBottomNav
import com.example.ui.components.FoodShareTopBar
import com.example.ui.screens.admin.AdminPortalScreen
import com.example.ui.screens.auth.AuthModal
import com.example.ui.screens.browse.BrowseScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.detail.DonationDetailScreen
import com.example.ui.screens.donate.DonateScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.request.FoodRequestScreen
import com.example.ui.theme.FoodShareTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: FoodShareDatabase
    private lateinit var repository: FoodShareRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = FoodShareDatabase.getDatabase(this, lifecycleScope)
        repository = FoodShareRepository(database, lifecycleScope)

        setContent {
            FoodShareTheme {
                MainAppScreen(repository = repository)
            }
        }
    }
}

@Composable
fun MainAppScreen(repository: FoodShareRepository) {
    val coroutineScope = rememberCoroutineScope()

    // Navigation and Modal State
    var currentRoute by remember { mutableStateOf("home") }
    var selectedDonationId by remember { mutableStateOf<String?>(null) }
    var showAuthModal by remember { mutableStateOf(false) }

    // Repository Flows
    val currentUser by repository.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val allDonations by repository.allDonations.collectAsStateWithLifecycle(initialValue = emptyList())
    val activeDonationsCount by repository.activeDonationsCount.collectAsStateWithLifecycle(initialValue = 0)
    val totalDonationsCount by repository.totalDonationsCount.collectAsStateWithLifecycle(initialValue = 0)
    val pendingReviewCount by repository.pendingReviewCount.collectAsStateWithLifecycle(initialValue = 0)
    val completedExchangesCount by repository.completedExchangesCount.collectAsStateWithLifecycle(initialValue = 0)
    val allRequests by repository.allRequests.collectAsStateWithLifecycle(initialValue = emptyList())
    val allUsers by repository.allUsers.collectAsStateWithLifecycle(initialValue = emptyList())
    val userCount by repository.userCount.collectAsStateWithLifecycle(initialValue = 0)
    val auditLogs by repository.recentAuditLogs.collectAsStateWithLifecycle(initialValue = emptyList())

    val myDonations = remember(allDonations, currentUser) {
        val uid = currentUser?.id ?: ""
        allDonations.filter { it.donorId == uid }
    }

    val myRequests = remember(allRequests, currentUser) {
        val uid = currentUser?.id ?: ""
        allRequests.filter { it.recipientId == uid }
    }

    val notifications by if (currentUser != null) {
        repository.getNotifications(currentUser!!.id).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    val selectedDonation = remember(selectedDonationId, allDonations) {
        allDonations.find { it.id == selectedDonationId }
    }

    // Handle back button when on sub-screens
    if (currentRoute == "detail" || currentRoute == "admin") {
        BackHandler {
            currentRoute = if (currentRoute == "detail") "browse" else "home"
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != "detail" && currentRoute != "admin") {
                FoodShareTopBar(
                    currentUser = currentUser,
                    onNavigateAdmin = { currentRoute = "admin" },
                    onOpenUserSwitch = { showAuthModal = true }
                )
            }
        },
        bottomBar = {
            if (currentRoute != "detail" && currentRoute != "admin") {
                FoodShareBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        currentRoute = route
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "home" -> {
                    HomeScreen(
                        donations = allDonations,
                        activeCount = activeDonationsCount,
                        totalCount = totalDonationsCount,
                        completedCount = completedExchangesCount,
                        onNavigateDonate = { currentRoute = "donate" },
                        onNavigateBrowse = { currentRoute = "browse" },
                        onNavigateRequests = { currentRoute = "requests" },
                        onViewDonation = { id ->
                            selectedDonationId = id
                            currentRoute = "detail"
                        }
                    )
                }

                "browse" -> {
                    BrowseScreen(
                        donations = allDonations,
                        onViewDonation = { id ->
                            selectedDonationId = id
                            currentRoute = "detail"
                        }
                    )
                }

                "donate" -> {
                    DonateScreen(
                        currentUser = currentUser,
                        onSubmitSuccess = {
                            currentRoute = "dashboard"
                        },
                        onSubmitDonation = { donation ->
                            coroutineScope.launch {
                                repository.submitDonation(donation)
                            }
                        }
                    )
                }

                "requests" -> {
                    FoodRequestScreen(
                        requests = allRequests,
                        currentUser = currentUser,
                        onSubmitRequest = { request ->
                            coroutineScope.launch {
                                repository.submitFoodRequest(request)
                            }
                        }
                    )
                }

                "dashboard" -> {
                    DashboardScreen(
                        currentUser = currentUser,
                        myDonations = myDonations,
                        myRequests = myRequests,
                        notifications = notifications,
                        onNavigateDonate = { currentRoute = "donate" },
                        onNavigateBrowse = { currentRoute = "browse" },
                        onViewDonation = { id ->
                            selectedDonationId = id
                            currentRoute = "detail"
                        },
                        onOpenRoleSwitcher = { showAuthModal = true }
                    )
                }

                "admin" -> {
                    AdminPortalScreen(
                        currentUser = currentUser,
                        donations = allDonations,
                        totalDonations = totalDonationsCount,
                        activeDonations = activeDonationsCount,
                        pendingReviews = pendingReviewCount,
                        completedExchanges = completedExchangesCount,
                        userCount = userCount,
                        auditLogs = auditLogs,
                        onUpdateStatus = { donationId, newStatus ->
                            coroutineScope.launch {
                                repository.updateDonationStatus(
                                    donationId = donationId,
                                    newStatus = newStatus,
                                    adminName = currentUser?.fullName ?: "Admin"
                                )
                            }
                        },
                        onBack = { currentRoute = "home" }
                    )
                }

                "detail" -> {
                    DonationDetailScreen(
                        donation = selectedDonation,
                        currentUser = currentUser,
                        onBack = { currentRoute = "browse" },
                        onUpdateStatus = { donationId, newStatus ->
                            coroutineScope.launch {
                                repository.updateDonationStatus(
                                    donationId = donationId,
                                    newStatus = newStatus,
                                    adminName = currentUser?.fullName
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAuthModal) {
        AuthModal(
            currentUser = currentUser,
            allUsers = allUsers,
            onSelectUser = { user ->
                coroutineScope.launch {
                    repository.loginAsUser(user)
                }
            },
            onRegisterUser = { name, email, phone, type, loc ->
                coroutineScope.launch {
                    repository.registerUser(name, email, phone, type, loc)
                }
            },
            onDismiss = { showAuthModal = false }
        )
    }
}
