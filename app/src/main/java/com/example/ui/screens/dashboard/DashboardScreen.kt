package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
import com.example.data.model.FoodRequest
import com.example.data.model.NotificationItem
import com.example.data.model.User
import com.example.ui.components.DonationCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    currentUser: User?,
    myDonations: List<Donation>,
    myRequests: List<FoodRequest>,
    notifications: List<NotificationItem>,
    onNavigateDonate: () -> Unit,
    onNavigateBrowse: () -> Unit,
    onViewDonation: (String) -> Unit,
    onOpenRoleSwitcher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Donations (${myDonations.size})", "My Requests (${myRequests.size})", "Activity (${notifications.size})", "Profile")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Welcome Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepForestGreen)
                    .padding(horizontal = 16.dp, vertical = 22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftMint
                            )
                            Text(
                                text = currentUser?.fullName ?: "Community Member",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }

                        Surface(
                            color = EmeraldGreen,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { onOpenRoleSwitcher() }
                        ) {
                            Text(
                                text = currentUser?.accountType ?: "Member",
                                color = PureWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Location: ${currentUser?.location ?: "Community Hub"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSage
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real Statistics Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardMetricCard(
                            count = myDonations.size.toString(),
                            label = "Donations",
                            icon = Icons.Filled.VolunteerActivism,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            count = myRequests.size.toString(),
                            label = "Requests",
                            icon = Icons.Filled.Handshake,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            count = myDonations.sumOf { it.servings }.toString(),
                            label = "Servings Shared",
                            icon = Icons.Filled.People,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Tab Row
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhite,
                contentColor = DeepForestGreen,
                edgePadding = 16.dp,
                divider = { Divider(color = CardBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) DeepForestGreen else Charcoal.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: MY DONATIONS
        if (selectedTab == 0) {
            if (myDonations.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No active donations yet",
                        message = "Help prevent food waste by sharing surplus prepared food, bakery items, or produce.",
                        actionLabel = "Create Food Donation",
                        onAction = onNavigateDonate
                    )
                }
            } else {
                items(myDonations) { donation ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        DonationCard(
                            donation = donation,
                            onViewDetails = { onViewDonation(donation.id) },
                            onContactWhatsApp = {
                                openWhatsAppChat(context, donation.contactPhone, donation.id, donation.foodName)
                            }
                        )
                    }
                }
            }
        }

        // TAB 1: MY REQUESTS
        if (selectedTab == 1) {
            if (myRequests.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No food requests logged",
                        message = "If you or your group need surplus meals, post a request so nearby donors can support.",
                        actionLabel = "Browse Available Food",
                        onAction = onNavigateBrowse
                    )
                }
            } else {
                items(myRequests) { req ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = req.category,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DeepForestGreen
                                    )
                                    StatusBadge(status = req.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Quantity: ${req.quantity} • Servings: ${req.servingsNeeded}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal
                                )
                                Text(
                                    text = "Needed: ${req.neededDateTime} at ${req.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: ACTIVITY & NOTIFICATIONS
        if (selectedTab == 2) {
            if (notifications.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = MutedSage, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No notifications yet", fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            Text("Updates regarding donation reviews and requests will appear here.", style = MaterialTheme.typography.bodySmall, color = Charcoal.copy(alpha = 0.7f))
                        }
                    }
                }
            } else {
                items(notifications) { notif ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SoftMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (notif.type == "donation") Icons.Filled.VolunteerActivism else Icons.Filled.Notifications,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForestGreen
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Charcoal.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 3: PROFILE & ACCOUNT
        if (selectedTab == 3) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileFieldRow("Full Name", currentUser?.fullName ?: "")
                            ProfileFieldRow("Email Address", currentUser?.email ?: "")
                            ProfileFieldRow("Contact Phone", currentUser?.phone ?: "")
                            ProfileFieldRow("Account Type", currentUser?.accountType ?: "")
                            ProfileFieldRow("Primary Location", currentUser?.location ?: "")
                            ProfileFieldRow("Admin Permissions", if (currentUser?.isAdmin == true) "Yes (Full Coordinator)" else "Standard Member")
                        }
                    }

                    Button(
                        onClick = onOpenRoleSwitcher,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen, contentColor = PureWhite)
                    ) {
                        Icon(Icons.Filled.SwitchAccount, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Active Profile / Role", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = EmeraldGreen.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = WarmIvory, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PureWhite)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = SoftMint, fontSize = 10.sp)
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.VolunteerActivism, contentDescription = null, tint = MutedSage, modifier = Modifier.size(44.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DeepForestGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, color = Charcoal.copy(alpha = 0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Charcoal.copy(alpha = 0.65f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = DeepForestGreen)
    }
}
