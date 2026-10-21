package com.example.ui.screens.admin

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminAuditLog
import com.example.data.model.Donation
import com.example.data.model.User
import com.example.ui.components.AiScreeningBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    currentUser: User?,
    donations: List<Donation>,
    totalDonations: Int,
    activeDonations: Int,
    pendingReviews: Int,
    completedExchanges: Int,
    userCount: Int,
    auditLogs: List<AdminAuditLog>,
    onUpdateStatus: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val pendingList = remember(donations) { donations.filter { it.status == "Pending Review" } }
    val allList = remember(donations) { donations }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = PureWhite)
                        Text("Admin Safety Portal", fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepForestGreen)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(WarmIvory)
                .padding(innerPadding)
                .testTag("admin_portal_screen"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Metrics Overview Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepForestGreen)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Platform Verification & Audit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Real-time metrics from live database records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMint
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard("Pending", pendingReviews.toString(), WarningAmber, Modifier.weight(1f))
                        AdminStatCard("Active", activeDonations.toString(), EmeraldGreen, Modifier.weight(1f))
                        AdminStatCard("Exchanges", completedExchanges.toString(), Color(0xFF0284C7), Modifier.weight(1f))
                        AdminStatCard("Users", userCount.toString(), SoftMint, Modifier.weight(1f))
                    }
                }
            }

            // Tabs: Pending Reviews, All Listings, Audit Logs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PureWhite,
                    contentColor = DeepForestGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Pending Review (${pendingList.size})", fontWeight = FontWeight.Bold)
                                if (pendingList.isNotEmpty()) {
                                    Badge(containerColor = WarningAmber) { Text(pendingList.size.toString(), color = PureWhite) }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("All Listings (${allList.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Audit Trail", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // TAB 0: PENDING REVIEW
            if (selectedTab == 0) {
                if (pendingList.isEmpty()) {
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
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Zero Pending Safety Reviews", fontWeight = FontWeight.Bold, color = DeepForestGreen)
                                Text("All submitted food donations are verified and accounted for.", style = MaterialTheme.typography.bodySmall, color = Charcoal.copy(alpha = 0.7f))
                            }
                        }
                    }
                } else {
                    items(pendingList) { donation ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            AdminDonationReviewCard(
                                donation = donation,
                                onApprove = {
                                    onUpdateStatus(donation.id, "Available")
                                    Toast.makeText(context, "Listing approved and published!", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    onUpdateStatus(donation.id, "Rejected")
                                    Toast.makeText(context, "Listing marked as rejected for safety non-compliance.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            // TAB 1: ALL LISTINGS
            if (selectedTab == 1) {
                items(allList) { donation ->
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${donation.foodName} (${donation.id})",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DeepForestGreen
                                    )
                                    StatusBadge(status = donation.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Donor: ${donation.donorName} • ${donation.category} • ${donation.quantity.toInt()} ${donation.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (donation.status != "Available") {
                                        TextButton(onClick = { onUpdateStatus(donation.id, "Available") }) {
                                            Text("Set Available", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (donation.status != "Completed") {
                                        TextButton(onClick = { onUpdateStatus(donation.id, "Completed") }) {
                                            Text("Mark Completed", color = Color(0xFF0284C7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (donation.status != "Rejected") {
                                        TextButton(onClick = { onUpdateStatus(donation.id, "Rejected") }) {
                                            Text("Reject/Archive", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: AUDIT TRAIL
            if (selectedTab == 2) {
                items(auditLogs) { log ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.action,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                    Text(
                                        text = "By ${log.adminName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Charcoal.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = log.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = EmeraldGreen.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = PureWhite)
            Text(label, style = MaterialTheme.typography.labelSmall, color = SoftMint, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AdminDonationReviewCard(
    donation: Donation,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, WarningAmber)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = donation.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                AiScreeningBadge(screeningStatus = donation.aiScreeningStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Donor: ${donation.donorName} (${donation.donorType}) • ${donation.location}",
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Storage: ${donation.storageMethod} • Packaging: ${donation.packagingCondition}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = DeepForestGreen
            )

            if (donation.allergenInfo.isNotBlank()) {
                Text(
                    text = "Disclosed Allergens: ${donation.allergenInfo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningAmber
                )
            }

            if (donation.aiSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SoftMint,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("AI Inspection Finding:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                        Text(donation.aiSummary, style = MaterialTheme.typography.bodySmall, color = Charcoal, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Reject")
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
                ) {
                    Text("Approve Listing", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
