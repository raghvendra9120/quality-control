package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.CivilTestEngine
import com.example.data.model.TestCategory
import com.example.ui.CivilUiState
import com.example.ui.components.PassStampBadge
import com.example.ui.components.ValidationBanner
import com.example.ui.theme.BlueprintPrimary
import com.example.ui.theme.BlueprintSecondary
import com.example.ui.theme.ConcreteBorder
import com.example.ui.theme.PassGreen
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: CivilUiState,
    onSelectTest: (com.example.data.model.TestDefinition) -> Unit,
    onSelectGrade: (String) -> Unit,
    onUpdateWeight: (String) -> Unit,
    onUpdateCount: (Int) -> Unit,
    onUpdateAge: (Int) -> Unit,
    onGenerateReport: () -> Unit,
    onViewCurrentReport: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAiConsultant: () -> Unit,
    onClearNotification: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.notificationMessage) {
        uiState.notificationMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearNotification()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SafetyAmber,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CIVIL TEST PRO",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "IS Code QA/QC Testing & AI Auditor",
                                fontSize = 11.sp,
                                color = Color(0xFFBFDBFE)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenAiConsultant,
                        modifier = Modifier.testTag("ai_consultant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Civil Consultant",
                            tint = SafetyAmber
                        )
                    }
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Report History",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateDark)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Active Report quick banner if available
            uiState.currentReport?.let { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onViewCurrentReport() }
                        .testTag("current_report_banner"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PassGreen)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PassStampBadge(statusText = "IS PASS")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = report.reportNumber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueprintPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${report.testTitle} (${report.grade})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                        Button(
                            onClick = onViewCurrentReport,
                            colors = ButtonDefaults.buttonColors(containerColor = PassGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("view_active_report_button")
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Report", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 1. SELECT CIVIL TEST CATEGORY & TEST
            Text(
                text = "1. CHOOSE CIVIL CONSTRUCTION TEST",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Horizontal scrolling test selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                CivilTestEngine.allTests.forEach { test ->
                    val isSelected = test.id == uiState.selectedTest.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectTest(test) },
                        label = {
                            Text(
                                text = test.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueprintPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("test_chip_${test.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Test Info & Standard Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ConcreteBorder)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.selectedTest.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = uiState.selectedTest.isCode,
                                color = BlueprintPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.selectedTest.description,
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. GRADE / CLASSIFICATION SELECTION
            Text(
                text = "2. SPECIFY GRADE / CLASSIFICATION (IS CODE)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                uiState.selectedTest.grades.forEach { grade ->
                    val isSelected = grade == uiState.selectedGrade
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectGrade(grade) },
                        label = {
                            Text(
                                text = grade,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SafetyAmber,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("grade_chip_$grade")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. SAMPLE WEIGHT & SPECIMEN COUNT
            Text(
                text = "3. SAMPLE WEIGHT & SPECIMEN COUNT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Weight input
                OutlinedTextField(
                    value = uiState.sampleWeightInput,
                    onValueChange = onUpdateWeight,
                    label = { Text("${uiState.selectedTest.sampleLabel} (${uiState.selectedTest.weightUnit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("sample_weight_input")
                )

                // Specimen counter
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, ConcreteBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sample Count", fontSize = 10.sp, color = TextSecondaryDark)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { onUpdateCount(uiState.sampleCount - 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("decrease_count_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${uiState.sampleCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BlueprintPrimary
                        )
                        IconButton(
                            onClick = { onUpdateCount(uiState.sampleCount + 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("increase_count_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Quick Weight preset chips
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = when (uiState.selectedTest.id) {
                    "concrete_cube" -> listOf("8.10", "8.25", "8.40", "8.55")
                    "sand_sieve" -> listOf("500.0", "1000.0", "2000.0")
                    "coarse_aggregate_impact" -> listOf("350.0", "380.0", "500.0")
                    "steel_tensile" -> listOf("1.58", "2.47", "0.89", "0.62")
                    else -> listOf("100.0", "400.0", "1000.0", "2050.0")
                }
                Text("Quick presets:", fontSize = 11.sp, color = TextSecondaryDark, modifier = Modifier.align(Alignment.CenterVertically))
                presets.take(3).forEach { p ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onUpdateWeight(p) }
                            .border(1.dp, ConcreteBorder, RoundedCornerShape(6.dp)),
                        color = if (uiState.sampleWeightInput == p) Color(0xFFDBEAFE) else Color.White
                    ) {
                        Text(
                            text = "$p ${uiState.selectedTest.weightUnit}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (uiState.sampleWeightInput == p) BlueprintPrimary else TextPrimaryDark
                        )
                    }
                }
            }

            // Age selector (if applicable for Concrete / Cement)
            if (uiState.selectedTest.id == "concrete_cube" || uiState.selectedTest.id == "cement_physical") {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "SPECIMEN CURING AGE (DAYS)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = BlueprintPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(7, 14, 28).forEach { age ->
                        val isSelected = uiState.ageInDays == age
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUpdateAge(age) },
                            label = { Text("$age Days (${if (age == 28) "100% Target" else if (age == 7) "65% Early" else "88%"})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueprintPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("age_chip_${age}d")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. REAL-TIME AUTOMATIC VALIDATION & ERROR DETECTION PANEL
            Text(
                text = "4. AUTOMATIC VALIDATION & ERROR DETECTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            ValidationBanner(
                validation = uiState.liveValidation,
                modifier = Modifier.testTag("validation_banner")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 5. PROMINENT GENERATE BUTTON
            Button(
                onClick = onGenerateReport,
                enabled = !uiState.isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PassGreen,
                    disabledContainerColor = Color(0xFFA7F3D0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_report_button")
            ) {
                if (uiState.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "COMPUTING IS READINGS & MATH...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GENERATE TEST REPORT (PASS RESULT)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Consultation & Helper Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenAiConsultant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ask_ai_btn")
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = SafetyAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI Engineer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenHistory,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("view_history_btn")
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = BlueprintPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Saved (${uiState.savedReports.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
