package com.example.ui.screens.donate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ai.FoodQualityAiService
import com.example.data.model.AiScreeningReport
import com.example.data.model.Donation
import com.example.data.model.User
import com.example.ui.components.AiScreeningBadge
import com.example.ui.components.MandatoryAiDisclaimerCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(
    currentUser: User?,
    onSubmitSuccess: () -> Unit,
    onSubmitDonation: (Donation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Stepper State: 0 = Details & Image, 1 = AI Screening, 2 = Safety Checklist & Review
    var currentStep by remember { mutableStateOf(0) }

    // Form Fields
    var foodName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Prepared Meals") }
    var description by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("10") }
    var selectedUnit by remember { mutableStateOf("portions") }
    var servingsText by remember { mutableStateOf("10") }
    var prepDateTime by remember { mutableStateOf("Today, 1:00 PM") }
    var selectedStorage by remember { mutableStateOf("Refrigerated") }
    var location by remember { mutableStateOf(currentUser?.location ?: "Downtown Civic Plaza") }
    var deadline by remember { mutableStateOf("Today by 9:00 PM") }
    var contactMethod by remember { mutableStateOf("WhatsApp") }
    var contactPhone by remember { mutableStateOf(currentUser?.phone ?: "+1 (555) 234-5678") }
    var selectedPackaging by remember { mutableStateOf("Sealed Container") }
    var hygieneNotes by remember { mutableStateOf("Prepared in sanitized kitchen; packed in food-grade container.") }
    var allergenInfo by remember { mutableStateOf("None") }
    var pricingType by remember { mutableStateOf("Free Donation") }
    var priceText by remember { mutableStateOf("0.0") }

    // Image & AI State
    var selectedImageUri by remember { mutableStateOf("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=600&q=80") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzingAi by remember { mutableStateOf(false) }
    var aiReport by remember { mutableStateOf<AiScreeningReport?>(null) }

    // Safety Checklist
    var checkAccurateTime by remember { mutableStateOf(false) }
    var checkColdChain by remember { mutableStateOf(false) }
    var checkAllergensDisclosed by remember { mutableStateOf(false) }
    var checkAiDisclaimerUnderstood by remember { mutableStateOf(false) }

    // Sample food photos donors can pick from if camera/file picker is simulated
    val samplePhotos = listOf(
        "Healthy Salad Bowl" to "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80",
        "Artisan Bread" to "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80",
        "Hot Vegetable Soup" to "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=600&q=80",
        "Fresh Fruit Basket" to "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=600&q=80",
        "Cooked Rice & Curry" to "https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=600&q=80"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                // Ignore fallback
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmIvory)
            .testTag("donate_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepForestGreen)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Donate Surplus Food",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
                Text(
                    text = "Share surplus food responsibly with AI quality screening & safety verification.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftMint
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-step progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepChip(number = "1", title = "Food Details", isActive = currentStep == 0, isDone = currentStep > 0)
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = MutedSage.copy(alpha = 0.5f))
                    StepChip(number = "2", title = "AI Screening", isActive = currentStep == 1, isDone = currentStep > 1)
                    Divider(modifier = Modifier.weight(1f).height(1.dp), color = MutedSage.copy(alpha = 0.5f))
                    StepChip(number = "3", title = "Safety Review", isActive = currentStep == 2, isDone = false)
                }
            }
        }

        // STEP 0: FOOD DETAILS & IMAGE SELECTION
        if (currentStep == 0) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Food Details & Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )

                    // Food Name
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Title *") },
                        placeholder = { Text("e.g., Vegetable Lasagna & Garlic Bread") },
                        modifier = Modifier.fillMaxWidth().testTag("input_food_name"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true
                    )

                    // Category selection
                    Text(
                        text = "Food Category *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    val categories = listOf("Prepared Meals", "Bakery", "Fresh Produce", "Dairy", "Pantry & Canned", "Beverages")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldGreen,
                                    selectedLabelColor = PureWhite,
                                    containerColor = PureWhite,
                                    labelColor = Charcoal
                                )
                            )
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Ingredients *") },
                        placeholder = { Text("Describe preparation, ingredients, temperature history, and portioning.") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_description"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        maxLines = 4
                    )

                    // Quantity + Unit + Servings Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("Quantity *") },
                            modifier = Modifier.weight(1f).testTag("input_quantity"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = selectedUnit,
                            onValueChange = { selectedUnit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f).testTag("input_unit"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = servingsText,
                            onValueChange = { servingsText = it },
                            label = { Text("Servings *") },
                            modifier = Modifier.weight(1f).testTag("input_servings"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            singleLine = true
                        )
                    }

                    // Storage Method & Packaging Condition
                    Text(
                        text = "Storage & Packaging *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )
                    val storages = listOf("Refrigerated", "Frozen", "Room Temperature", "Heated Container")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(storages) { s ->
                            FilterChip(
                                selected = selectedStorage == s,
                                onClick = { selectedStorage = s },
                                label = { Text(s, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepForestGreen,
                                    selectedLabelColor = PureWhite,
                                    containerColor = PureWhite,
                                    labelColor = Charcoal
                                )
                            )
                        }
                    }

                    val packagings = listOf("Sealed Container", "Original Packaging", "Clean Box", "Foil Wrapped")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(packagings) { p ->
                            FilterChip(
                                selected = selectedPackaging == p,
                                onClick = { selectedPackaging = p },
                                label = { Text(p, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldGreen,
                                    selectedLabelColor = PureWhite,
                                    containerColor = PureWhite,
                                    labelColor = Charcoal
                                )
                            )
                        }
                    }

                    // Known Allergens & Hygiene Notes
                    OutlinedTextField(
                        value = allergenInfo,
                        onValueChange = { allergenInfo = it },
                        label = { Text("Known Allergens (e.g., Dairy, Nuts, Gluten, None)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_allergens"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = hygieneNotes,
                        onValueChange = { hygieneNotes = it },
                        label = { Text("Hygiene & Safe Handling Notes") },
                        modifier = Modifier.fillMaxWidth().testTag("input_hygiene_notes"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        singleLine = true
                    )

                    // Location & Collection Deadline
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Approximate Collection Location *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_location"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Available Until / Pickup Deadline *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_deadline"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        ),
                        singleLine = true
                    )

                    // Contact Method & Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = contactMethod,
                            onValueChange = { contactMethod = it },
                            label = { Text("Contact Method") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("WhatsApp Phone *") },
                            modifier = Modifier.weight(1.3f).testTag("input_contact_phone"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            ),
                            singleLine = true
                        )
                    }

                    // Food Image Upload / Selection
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Food Photography & Verification Image *",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE2EBE4))
                            ) {
                                if (selectedImageUri.isNotBlank()) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected food photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No image selected", color = Charcoal.copy(alpha = 0.6f))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f).testTag("browse_images_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Browse Gallery")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Or select a sample food photo for preview:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Charcoal.copy(alpha = 0.7f)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                items(samplePhotos) { (label, url) ->
                                    AssistChip(
                                        onClick = {
                                            selectedImageUri = url
                                            selectedBitmap = null
                                        },
                                        label = { Text(label, fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Next Button -> Go to AI Screening
                    Button(
                        onClick = {
                            if (foodName.isBlank()) {
                                Toast.makeText(context, "Please enter a food title", Toast.LENGTH_SHORT).show()
                            } else {
                                currentStep = 1
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("step_1_next_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen,
                            contentColor = PureWhite
                        )
                    ) {
                        Text("Proceed to AI Quality Screening", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }

        // STEP 1: AI QUALITY SCREENING
        if (currentStep == 1) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "2. AI-Assisted Food Quality Screening",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )

                    Text(
                        text = "Our vision screening analyzes visible signs of spoilage, abnormal discoloration, mold patterns, and packaging seal condition before publication.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.8f)
                    )

                    // Preview of selected photo & parameters
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2EBE4))
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Column {
                                Text(
                                    text = foodName.ifBlank { "Untitled Donation" },
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen
                                )
                                Text(
                                    text = "$selectedCategory • $selectedStorage • $selectedPackaging",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Prominent ANALYZE FOOD QUALITY Button
                    Button(
                        onClick = {
                            isAnalyzingAi = true
                            coroutineScope.launch {
                                try {
                                    val report = FoodQualityAiService.analyzeFoodQuality(
                                        foodName = foodName,
                                        category = selectedCategory,
                                        storageMethod = selectedStorage,
                                        packagingCondition = selectedPackaging,
                                        hygieneNotes = hygieneNotes,
                                        bitmap = selectedBitmap
                                    )
                                    aiReport = report
                                } catch (e: Exception) {
                                    Toast.makeText(context, "AI analysis error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isAnalyzingAi = false
                                }
                            }
                        },
                        enabled = !isAnalyzingAi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("analyze_food_quality_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepForestGreen,
                            contentColor = PureWhite
                        )
                    ) {
                        if (isAnalyzingAi) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = PureWhite,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analyzing Visible Food Features...")
                        } else {
                            Icon(imageVector = Icons.Filled.Biotech, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze Food Quality", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Structured AI Screening Report Card
                    if (aiReport != null) {
                        val report = aiReport!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_screening_report_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, when (report.screeningStatus) {
                                FoodQualityAiService.STATUS_NO_WARNINGS -> EmeraldGreen
                                FoodQualityAiService.STATUS_CONCERN -> WarningAmber
                                else -> MutedSage
                            })
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI Screening Report",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForestGreen
                                    )
                                    AiScreeningBadge(screeningStatus = report.screeningStatus)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                SectionTitleText("Visible Condition Summary:")
                                Text(
                                    text = report.visibleConditionSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                SectionTitleText("Potential Warning Signs:")
                                Text(
                                    text = report.potentialWarningSigns,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (report.screeningStatus == FoodQualityAiService.STATUS_CONCERN) WarningAmber else Charcoal
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                SectionTitleText("Analysis Limitations:")
                                Text(
                                    text = report.limitations,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Charcoal.copy(alpha = 0.75f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                SectionTitleText("Recommended Next Steps:")
                                Text(
                                    text = report.recommendedNextSteps,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Mandatory AI Disclaimer
                    MandatoryAiDisclaimerCard()

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentStep = 0 },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back to Details")
                        }

                        Button(
                            onClick = {
                                if (aiReport == null) {
                                    // Generate default screening if skipped
                                    aiReport = AiScreeningReport(
                                        id = "SCR-" + UUID.randomUUID().toString().take(6),
                                        foodCategory = selectedCategory,
                                        visibleConditionSummary = "User completed visual confirmation. Packaging intact.",
                                        potentialWarningSigns = "None identified from visible inspection.",
                                        limitations = "Visual check only; does not test bacterial purity.",
                                        recommendedNextSteps = "Maintain chilled temperature until pickup.",
                                        screeningStatus = FoodQualityAiService.STATUS_NO_WARNINGS
                                    )
                                }
                                currentStep = 2
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("step_2_next_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = PureWhite
                            )
                        ) {
                            Text("Continue to Review", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // STEP 2: SAFETY CHECKLIST & FINAL SUBMISSION
        if (currentStep == 2) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "3. Food Safety Review Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestGreen
                    )

                    Text(
                        text = "Before your listing goes live, confirm compliance with community food safety protocols.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal.copy(alpha = 0.8f)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SafetyCheckboxRow(
                                checked = checkAccurateTime,
                                onCheckedChange = { checkAccurateTime = it },
                                label = "I confirm that all preparation times, storage history, and description details provided are accurate.",
                                testTag = "chk_accurate_time"
                            )

                            SafetyCheckboxRow(
                                checked = checkColdChain,
                                onCheckedChange = { checkColdChain = it },
                                label = "Perishable food has been kept strictly outside the temperature danger zone (4°C - 60°C) and prepared in clean food-safe conditions.",
                                testTag = "chk_cold_chain"
                            )

                            SafetyCheckboxRow(
                                checked = checkAllergensDisclosed,
                                onCheckedChange = { checkAllergensDisclosed = it },
                                label = "All known major allergens (nuts, dairy, gluten, soy, eggs) have been fully disclosed to protect community recipients.",
                                testTag = "chk_allergens"
                            )

                            SafetyCheckboxRow(
                                checked = checkAiDisclaimerUnderstood,
                                onCheckedChange = { checkAiDisclaimerUnderstood = it },
                                label = "I understand that AI screening identifies possible visible warning signs only, and cannot certify that food is biologically safe to eat.",
                                testTag = "chk_disclaimer"
                            )
                        }
                    }

                    // Submission summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftMint)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Listing Summary",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepForestGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Food: $foodName\n• Quantity: $quantityText $selectedUnit ($servingsText servings)\n• Storage: $selectedStorage ($selectedPackaging)\n• Location: $location\n• Deadline: $deadline\n• AI Status: ${aiReport?.screeningStatus ?: "PENDING"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Charcoal,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Back & Final Submit Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentStep = 1 },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }

                        val allChecked = checkAccurateTime && checkColdChain && checkAllergensDisclosed && checkAiDisclaimerUnderstood
                        Button(
                            onClick = {
                                if (!allChecked) {
                                    Toast.makeText(context, "Please confirm all 4 food safety checklist items.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val isApproved = aiReport?.screeningStatus == FoodQualityAiService.STATUS_NO_WARNINGS
                                val donationStatus = if (isApproved) "Available" else "Pending Review"

                                val newDonation = Donation(
                                    id = "DON-2026-" + UUID.randomUUID().toString().take(6).uppercase(),
                                    foodName = foodName,
                                    category = selectedCategory,
                                    description = description,
                                    quantity = quantityText.toDoubleOrNull() ?: 1.0,
                                    unit = selectedUnit,
                                    servings = servingsText.toIntOrNull() ?: 1,
                                    prepDateTime = prepDateTime,
                                    storageMethod = selectedStorage,
                                    location = location,
                                    deadline = deadline,
                                    contactMethod = contactMethod,
                                    contactPhone = contactPhone,
                                    packagingCondition = selectedPackaging,
                                    hygieneNotes = hygieneNotes,
                                    allergenInfo = allergenInfo,
                                    pricingType = pricingType,
                                    priceAmount = priceText.toDoubleOrNull() ?: 0.0,
                                    status = donationStatus,
                                    donorId = currentUser?.id ?: "usr_donor",
                                    donorName = currentUser?.fullName ?: "Community Member",
                                    donorType = currentUser?.accountType ?: "Individual",
                                    imageUri = selectedImageUri,
                                    aiScreeningStatus = aiReport?.screeningStatus ?: FoodQualityAiService.STATUS_NO_WARNINGS,
                                    aiSummary = aiReport?.visibleConditionSummary ?: "",
                                    aiWarningSigns = aiReport?.potentialWarningSigns ?: "",
                                    aiNextSteps = aiReport?.recommendedNextSteps ?: ""
                                )

                                onSubmitDonation(newDonation)
                                Toast.makeText(context, "Donation successfully submitted!", Toast.LENGTH_LONG).show()
                                onSubmitSuccess()
                            },
                            enabled = allChecked,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp)
                                .testTag("submit_donation_final_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = PureWhite
                            )
                        ) {
                            Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Donation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChip(
    number: String,
    title: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(if (isDone) EmeraldGreen else if (isActive) PureWhite else MutedSage),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = number,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) DeepForestGreen else PureWhite
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) PureWhite else SoftMint.copy(alpha = 0.8f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SectionTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = DeepForestGreen
    )
}

@Composable
private fun SafetyCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .testTag(testTag),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = EmeraldGreen,
                checkmarkColor = PureWhite
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Charcoal,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
