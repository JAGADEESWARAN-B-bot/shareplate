package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ai.FoodQualityAiService
import com.example.data.model.Donation
import com.example.data.model.User
import com.example.ui.theme.*
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodShareTopBar(
    currentUser: User?,
    onNavigateAdmin: () -> Unit,
    onOpenUserSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("app_top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepForestGreen,
            titleContentColor = PureWhite,
            actionIconContentColor = PureWhite
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = "Logo",
                        tint = WarmIvory,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "COMMUNITY FOOD SHARE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Care Together • Waste Less",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSage,
                        fontSize = 11.sp
                    )
                }
            }
        },
        actions = {
            // Admin portal quick link if current user has admin rights
            if (currentUser?.isAdmin == true) {
                IconButton(
                    onClick = onNavigateAdmin,
                    modifier = Modifier.testTag("admin_portal_button")
                ) {
                    Badge {
                        Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // User Profile / Switch role chip
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenUserSwitch() }
                    .testTag("user_profile_chip"),
                color = EmeraldGreen.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "User Account",
                        tint = WarmIvory,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = currentUser?.accountType ?: "Guest",
                        color = WarmIvory,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )
}

@Composable
fun FoodShareBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .testTag("bottom_navigation_bar")
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = PureWhite,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple("home", "Home", Icons.Filled.Home to Icons.Outlined.Home),
            Triple("browse", "Find Food", Icons.Filled.Search to Icons.Outlined.Search),
            Triple("donate", "Donate", Icons.Filled.AddCircle to Icons.Outlined.AddCircleOutline),
            Triple("requests", "Requests", Icons.Filled.Handshake to Icons.Outlined.Handshake),
            Triple("dashboard", "Dashboard", Icons.Filled.Dashboard to Icons.Outlined.Dashboard)
        )

        items.forEach { (route, label, icons) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PureWhite,
                    selectedTextColor = DeepForestGreen,
                    indicatorColor = EmeraldGreen,
                    unselectedIconColor = Charcoal.copy(alpha = 0.65f),
                    unselectedTextColor = Charcoal.copy(alpha = 0.65f)
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Available" -> SoftMint to EmeraldGreen
        "Pending Review" -> WarningContainer to WarningAmber
        "Reserved" -> InfoContainer to InfoBlue
        "Completed" -> Color(0xFFF1F5F9) to Color(0xFF475569)
        "Rejected" -> ErrorContainer to ErrorRed
        else -> SoftMint to DeepForestGreen
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AiScreeningBadge(screeningStatus: String) {
    val (bgColor, textColor, icon) = when (screeningStatus) {
        FoodQualityAiService.STATUS_NO_WARNINGS -> Triple(SoftMint, EmeraldGreen, Icons.Filled.CheckCircle)
        FoodQualityAiService.STATUS_CONCERN -> Triple(WarningContainer, WarningAmber, Icons.Filled.Warning)
        FoodQualityAiService.STATUS_UNABLE -> Triple(Color(0xFFF3F4F6), Charcoal.copy(alpha = 0.7f), Icons.Filled.HelpOutline)
        else -> Triple(SoftMint, DeepForestGreen, Icons.Filled.Psychology)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = when (screeningStatus) {
                    FoodQualityAiService.STATUS_NO_WARNINGS -> "AI: Clean Visual"
                    FoodQualityAiService.STATUS_CONCERN -> "AI: Quality Note"
                    FoodQualityAiService.STATUS_UNABLE -> "AI: Unassessed"
                    else -> "AI Screening"
                },
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MandatoryAiDisclaimerCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_safety_disclaimer_card"),
        color = WarmIvory,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Food Safety Shield",
                tint = EmeraldGreen,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = "Responsible Food Safety Notice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = FoodQualityAiService.MANDATORY_DISCLAIMER,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun DonationCard(
    donation: Donation,
    onViewDetails: () -> Unit,
    onContactWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donation_card_${donation.id}")
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Food Image + Overlay Category & Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFE2EBE4))
            ) {
                if (donation.imageUri.isNotBlank()) {
                    AsyncImage(
                        model = donation.imageUri,
                        contentDescription = donation.foodName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            tint = MutedSage,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Top row inside image: Category badge and Donation Status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = DeepForestGreen.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = donation.category,
                            color = PureWhite,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    StatusBadge(status = donation.status)
                }

                // Bottom row inside image: Pricing badge
                Surface(
                    color = if (donation.pricingType == "Free Donation") EmeraldGreen else DeepForestGreen,
                    shape = RoundedCornerShape(topStart = 8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = if (donation.pricingType == "Free Donation") "FREE" else "$${String.format("%.2f", donation.priceAmount)}",
                        color = PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Card details
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = donation.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = donation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stats row: Quantity + Location + Deadline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${donation.quantity.toInt()} ${donation.unit} (${donation.servings} servings)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Charcoal
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = Charcoal.copy(alpha = 0.6f),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = donation.deadline,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Charcoal.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Location row & Donor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = DeepForestGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${donation.location} • ${donation.donorName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Charcoal.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // AI Screening tag & Allergens
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AiScreeningBadge(screeningStatus = donation.aiScreeningStatus)

                    if (donation.allergenInfo.isNotBlank() && donation.allergenInfo != "None") {
                        Surface(
                            color = WarningContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Allergens: ${donation.allergenInfo.take(16)}...",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = WarningAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("view_details_btn_${donation.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DeepForestGreen
                        )
                    ) {
                        Text("View Details", style = MaterialTheme.typography.labelMedium)
                    }

                    if (donation.contactMethod.contains("WhatsApp", ignoreCase = true) || donation.contactPhone.isNotBlank()) {
                        Button(
                            onClick = onContactWhatsApp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("whatsapp_btn_${donation.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = PureWhite
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contact", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

fun openWhatsAppChat(context: Context, phone: String, donationId: String, foodName: String) {
    try {
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        val message = "Hello! I saw your food donation listing on Community Food Share: '$foodName' (ID: $donationId). Is it still available for pickup?"
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch WhatsApp. Phone: $phone", Toast.LENGTH_LONG).show()
    }
}
