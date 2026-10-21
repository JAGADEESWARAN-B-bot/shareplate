package com.example.ui.screens.detail

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ai.FoodQualityAiService
import com.example.data.model.Donation
import com.example.data.model.User
import com.example.ui.components.AiScreeningBadge
import com.example.ui.components.MandatoryAiDisclaimerCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDetailScreen(
    donation: Donation?,
    currentUser: User?,
    onBack: () -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    if (donation == null) {
        Box(
            modifier = modifier.fillMaxSize().background(WarmIvory),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Donation not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Back") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donation Details", color = PureWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepForestGreen)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PureWhite,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (donation.contactMethod.contains("WhatsApp", ignoreCase = true) || donation.contactPhone.isNotBlank()) {
                        Button(
                            onClick = {
                                openWhatsAppChat(context, donation.contactPhone, donation.id, donation.foodName)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_whatsapp_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
                        ) {
                            Icon(imageVector = Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Donor", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (donation.status == "Available") {
                        Button(
                            onClick = {
                                onUpdateStatus(donation.id, "Reserved")
                                Toast.makeText(context, "Listing reserved for pickup coordination!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_reserve_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen, contentColor = PureWhite)
                        ) {
                            Icon(imageVector = Icons.Filled.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reserve Food", fontWeight = FontWeight.Bold)
                        }
                    } else if (donation.status == "Reserved") {
                        Button(
                            onClick = {
                                onUpdateStatus(donation.id, "Completed")
                                Toast.makeText(context, "Donation exchange marked completed!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark Completed", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmIvory)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Photo
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color(0xFFE2EBE4))
                ) {
                    if (donation.imageUri.isNotBlank()) {
                        AsyncImage(
                            model = donation.imageUri,
                            contentDescription = donation.foodName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Overlay ID & Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = DeepForestGreen.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = donation.id,
                                color = PureWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        StatusBadge(status = donation.status)
                    }
                }
            }

            // Food Title & Category
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SoftMint,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = donation.category,
                                color = DeepForestGreen,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        AiScreeningBadge(screeningStatus = donation.aiScreeningStatus)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = donation.foodName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = donation.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Charcoal,
                        lineHeight = 22.sp
                    )
                }
            }

            // Allergen Warning Card if allergens present
            if (donation.allergenInfo.isNotBlank() && donation.allergenInfo != "None") {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WarningContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WarningAmber,
                                contentDescription = "Allergens",
                                tint = WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Allergen Advisory",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen
                                )
                                Text(
                                    text = "Contains: ${donation.allergenInfo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal
                                )
                            }
                        }
                    }
                }
            }

            // Key Specs Grid
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Specifications & Storage Requirements",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen
                        )

                        Divider(color = CardBorder)

                        SpecRow(icon = Icons.Filled.Inventory2, label = "Quantity", value = "${donation.quantity.toInt()} ${donation.unit} (${donation.servings} people)")
                        SpecRow(icon = Icons.Filled.Thermostat, label = "Storage Method", value = donation.storageMethod)
                        SpecRow(icon = Icons.Filled.AllInbox, label = "Packaging Condition", value = donation.packagingCondition)
                        SpecRow(icon = Icons.Filled.CalendarMonth, label = "Preparation Time", value = donation.prepDateTime)
                        SpecRow(icon = Icons.Filled.AccessTime, label = "Pickup Deadline", value = donation.deadline)
                        SpecRow(icon = Icons.Filled.LocationOn, label = "Approximate Location", value = donation.location)
                        SpecRow(icon = Icons.Filled.Store, label = "Donor Entity", value = "${donation.donorName} (${donation.donorType})")
                        SpecRow(icon = Icons.Filled.CleanHands, label = "Hygiene Notes", value = donation.hygieneNotes)
                    }
                }
            }

            // AI Screening Report Card & Safety Disclaimer
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Food Quality Screening Report",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepForestGreen
                            )
                            AiScreeningBadge(screeningStatus = donation.aiScreeningStatus)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Visible Condition Summary:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = DeepForestGreen)
                        Text(donation.aiSummary.ifBlank { "Surface features consistent with standard baseline." }, style = MaterialTheme.typography.bodySmall, color = Charcoal)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Potential Warning Signs:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = DeepForestGreen)
                        Text(donation.aiWarningSigns.ifBlank { "No obvious visible warning signs detected." }, style = MaterialTheme.typography.bodySmall, color = Charcoal)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Recommended Next Steps:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = DeepForestGreen)
                        Text(donation.aiNextSteps.ifBlank { "Maintain storage temperature; ensure allergen disclosure." }, style = MaterialTheme.typography.bodySmall, color = Charcoal)
                    }
                }
            }

            // Mandatory AI Disclaimer
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    MandatoryAiDisclaimerCard()
                }
            }
        }
    }
}

@Composable
private fun SpecRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Charcoal.copy(alpha = 0.65f))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Charcoal, fontWeight = FontWeight.Medium)
        }
    }
}
