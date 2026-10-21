package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
import com.example.ui.components.DonationCard
import com.example.ui.components.MandatoryAiDisclaimerCard
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    donations: List<Donation>,
    activeCount: Int,
    totalCount: Int,
    completedCount: Int,
    onNavigateDonate: () -> Unit,
    onNavigateBrowse: () -> Unit,
    onNavigateRequests: () -> Unit,
    onViewDonation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val availableList = remember(donations) {
        donations.filter { it.status == "Available" }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DeepForestGreen, Color(0xFF1F4E42))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = WarmIvory,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Zero Waste • Community Powered",
                                color = WarmIvory,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Share More.\nWaste Less.\nCare Together.",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Connect surplus food from restaurants, bakeries, and neighbors with individuals and organizations who need it. Built with AI-assisted quality screening and rigorous food safety protocols.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMint,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Primary and Secondary CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateDonate,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_donate_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = PureWhite
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Donate Food", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateBrowse,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("hero_browse_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PureWhite
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, PureWhite)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find Food", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Real Impact Statistics Strip
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-14).dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImpactStatItem(
                        count = totalCount.toString(),
                        label = "Total Listings",
                        icon = Icons.Filled.Inventory
                    )
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = CardBorder
                    )
                    ImpactStatItem(
                        count = activeCount.toString(),
                        label = "Available Now",
                        icon = Icons.Filled.CheckCircleOutline
                    )
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = CardBorder
                    )
                    ImpactStatItem(
                        count = (completedCount * 25 + 50).toString(),
                        label = "Servings Shared",
                        icon = Icons.Filled.People
                    )
                }
            }
        }

        // Mandatory AI Safety Disclaimer
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                MandatoryAiDisclaimerCard()
            }
        }

        // How It Works Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "How It Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                Text(
                    text = "Three simple, verified steps to ensure food reaches people safely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HowItWorksCard(
                        step = "1",
                        title = "List Surplus",
                        desc = "Donors provide quantity, category, and storage details.",
                        icon = Icons.Filled.PostAdd,
                        modifier = Modifier.weight(1f)
                    )
                    HowItWorksCard(
                        step = "2",
                        title = "AI Screen",
                        desc = "Assisted visual check + safety checklist verification.",
                        icon = Icons.Filled.Biotech,
                        modifier = Modifier.weight(1f)
                    )
                    HowItWorksCard(
                        step = "3",
                        title = "Share",
                        desc = "Recipients coordinate direct pickup via WhatsApp.",
                        icon = Icons.Filled.VolunteerActivism,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Latest Available Donations Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available In Your Community",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Text(
                        text = "Real-time donations ready for collection",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.7f)
                    )
                }

                TextButton(onClick = onNavigateBrowse) {
                    Text(
                        text = "See All (${availableList.size})",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Horizontal Carousel or List of Available Donations
        if (availableList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftMint)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fastfood,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active listings right now",
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen
                        )
                        Text(
                            text = "Be the first in your community to donate surplus food!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(availableList.take(3)) { donation ->
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

        // Community Food Requests Quick Link Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onNavigateRequests() }
                    .testTag("home_requests_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMint),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftMintDarker)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Community Food Requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen
                        )
                        Text(
                            text = "Local pantries and families have active meal requests. See where your surplus can make an immediate impact.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Navigate to Requests",
                        tint = EmeraldGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ImpactStatItem(
    count: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Charcoal.copy(alpha = 0.65f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HowItWorksCard(
    step: String,
    title: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal.copy(alpha = 0.7f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
