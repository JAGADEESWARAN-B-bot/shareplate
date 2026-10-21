package com.example.ui.screens.request

import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodRequest
import com.example.data.model.User
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodRequestScreen(
    requests: List<FoodRequest>,
    currentUser: User?,
    onSubmitRequest: (FoodRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    // Dialog fields
    var category by remember { mutableStateOf("Prepared Meals") }
    var quantity by remember { mutableStateOf("25 portions") }
    var servingsNeeded by remember { mutableStateOf("25") }
    var neededDateTime by remember { mutableStateOf("Today by 6:00 PM") }
    var location by remember { mutableStateOf(currentUser?.location ?: "Riverdale Community Center") }
    var dietaryRequirements by remember { mutableStateOf("Halal / Vegetarian options") }
    var allergies by remember { mutableStateOf("Nut-free preferred") }
    var additionalNotes by remember { mutableStateOf("For our community evening support program.") }
    var contactPreference by remember { mutableStateOf("WhatsApp") }
    var contactPhone by remember { mutableStateOf(currentUser?.phone ?: "+1 (555) 987-6543") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = EmeraldGreen,
                contentColor = PureWhite,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Request Food", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("request_food_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(WarmIvory)
                .padding(innerPadding)
                .testTag("food_requests_screen"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DeepForestGreen)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Community Food Requests",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Local NGOs, pantries, and community groups requesting surplus food for people in need.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftMint
                    )
                }
            }

            if (requests.isEmpty()) {
                item {
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
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Handshake, contentDescription = null, tint = MutedSage, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No open requests right now", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap 'Request Food' below if your community organization requires food assistance.", style = MaterialTheme.typography.bodySmall, color = Charcoal.copy(alpha = 0.7f))
                        }
                    }
                }
            } else {
                items(requests) { req ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        RequestCard(
                            request = req,
                            onContact = {
                                openWhatsAppChat(context, req.contactPhone, req.id, "Food Request: ${req.category}")
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet for creating a Food Request
    if (showCreateDialog) {
        ModalBottomSheet(
            onDismissRequest = { showCreateDialog = false },
            containerColor = PureWhite
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Submit Community Food Request",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    Text(
                        text = "Specify meal requirements so local donors can prepare matching surplus donations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.75f)
                    )
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Requested Category *") },
                        modifier = Modifier.fillMaxWidth().testTag("req_input_category"),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity Needed *") },
                            modifier = Modifier.weight(1f).testTag("req_input_quantity"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = servingsNeeded,
                            onValueChange = { servingsNeeded = it },
                            label = { Text("People to Serve *") },
                            modifier = Modifier.weight(1f).testTag("req_input_servings"),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = neededDateTime,
                        onValueChange = { neededDateTime = it },
                        label = { Text("Needed Date & Time *") },
                        modifier = Modifier.fillMaxWidth().testTag("req_input_date"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Delivery / Pickup Location *") },
                        modifier = Modifier.fillMaxWidth().testTag("req_input_location"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = dietaryRequirements,
                        onValueChange = { dietaryRequirements = it },
                        label = { Text("Special Dietary Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergy Safety Restrictions") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("WhatsApp Contact Number *") },
                        modifier = Modifier.fillMaxWidth().testTag("req_input_phone"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text("Additional Information") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (category.isBlank() || quantity.isBlank()) {
                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val newRequest = FoodRequest(
                                id = "REQ-2026-" + UUID.randomUUID().toString().take(6).uppercase(),
                                recipientId = currentUser?.id ?: "usr_pantry",
                                recipientName = currentUser?.fullName ?: "Community Care Center",
                                category = category,
                                quantity = quantity,
                                servingsNeeded = servingsNeeded.toIntOrNull() ?: 10,
                                neededDateTime = neededDateTime,
                                location = location,
                                dietaryRequirements = dietaryRequirements,
                                allergies = allergies,
                                additionalNotes = additionalNotes,
                                contactPreference = contactPreference,
                                contactPhone = contactPhone,
                                status = "Open"
                            )

                            onSubmitRequest(newRequest)
                            showCreateDialog = false
                            Toast.makeText(context, "Food request published to community!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_request_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
                    ) {
                        Text("Publish Food Request", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: FoodRequest,
    onContact: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("request_card_${request.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SoftMint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = request.category,
                        color = DeepForestGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = request.status,
                        color = Color(0xFFD97706),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${request.quantity} (Serves ~${request.servingsNeeded} people)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepForestGreen
            )

            Text(
                text = "Requested by: ${request.recipientName}",
                style = MaterialTheme.typography.bodySmall,
                color = Charcoal.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (request.additionalNotes.isNotBlank()) {
                Text(
                    text = request.additionalNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Charcoal
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                    Text(text = request.neededDateTime, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = Charcoal)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(15.dp))
                    Text(text = request.location, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = Charcoal)
                }
            }

            if (request.dietaryRequirements.isNotBlank() || request.allergies.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Dietary / Notes: ${request.dietaryRequirements} • ${request.allergies}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = Color(0xFF047857)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onContact,
                modifier = Modifier.fillMaxWidth().testTag("req_contact_btn_${request.id}"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PureWhite)
            ) {
                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Connect with Recipient via WhatsApp", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
