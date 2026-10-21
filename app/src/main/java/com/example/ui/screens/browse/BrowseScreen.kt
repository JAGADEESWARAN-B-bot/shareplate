package com.example.ui.screens.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.Donation
import com.example.ui.components.DonationCard
import com.example.ui.components.MandatoryAiDisclaimerCard
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@Composable
fun BrowseScreen(
    donations: List<Donation>,
    onViewDonation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPricing by remember { mutableStateOf("All") }
    var onlyAvailable by remember { mutableStateOf(true) }

    val categories = listOf("All", "Prepared Meals", "Bakery", "Fresh Produce", "Dairy", "Pantry & Canned")
    val pricingOptions = listOf("All", "Free Donation", "Fixed-Price")

    val filteredList = remember(donations, searchQuery, selectedCategory, selectedPricing, onlyAvailable) {
        donations.filter { donation ->
            val matchesSearch = searchQuery.isBlank() ||
                    donation.foodName.contains(searchQuery, ignoreCase = true) ||
                    donation.location.contains(searchQuery, ignoreCase = true) ||
                    donation.description.contains(searchQuery, ignoreCase = true) ||
                    donation.donorName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" || donation.category.equals(selectedCategory, ignoreCase = true)
            val matchesPricing = selectedPricing == "All" || donation.pricingType.contains(selectedPricing, ignoreCase = true)
            val matchesStatus = if (onlyAvailable) donation.status == "Available" else true

            matchesSearch && matchesCategory && matchesPricing && matchesStatus
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
            .testTag("browse_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepForestGreen)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Find Surplus Food",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Search listings by proximity, food type, or handling standard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMint
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search meals, bakery, produce, or neighborhood...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = EmeraldGreen
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = Charcoal
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("food_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        // Filter chips: Categories
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "Category Filter",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldGreen,
                                selectedLabelColor = PureWhite,
                                containerColor = PureWhite,
                                labelColor = Charcoal
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) EmeraldGreen else CardBorder
                            ),
                            modifier = Modifier.testTag("filter_cat_$category")
                        )
                    }
                }
            }
        }

        // Filter chips: Pricing & Availability Toggles
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pricingOptions.forEach { priceType ->
                        val isSelected = selectedPricing == priceType
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPricing = priceType },
                            label = { Text(priceType, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepForestGreen,
                                selectedLabelColor = PureWhite,
                                containerColor = PureWhite,
                                labelColor = Charcoal
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Available Only",
                        style = MaterialTheme.typography.labelSmall,
                        color = Charcoal
                    )
                    Switch(
                        checked = onlyAvailable,
                        onCheckedChange = { onlyAvailable = it },
                        modifier = Modifier.testTag("available_only_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = EmeraldGreen
                        )
                    )
                }
            }
        }

        // Disclaimer Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                MandatoryAiDisclaimerCard()
            }
        }

        // Results Count Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredList.size} Results",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepForestGreen
                )

                if (searchQuery.isNotEmpty() || selectedCategory != "All" || selectedPricing != "All") {
                    TextButton(onClick = {
                        searchQuery = ""
                        selectedCategory = "All"
                        selectedPricing = "All"
                    }) {
                        Text("Reset Filters", color = EmeraldGreen, fontSize = 12.sp)
                    }
                }
            }
        }

        // Listings
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = MutedSage,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No food listings match your filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search keywords, category filters, or location criteria.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Charcoal.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredList) { donation ->
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
}
