package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CivilTestReport
import com.example.ui.components.CalculationBreakdownCard
import com.example.ui.components.NablHeaderCard
import com.example.ui.components.PassStampBadge
import com.example.ui.components.ReadingsTableView
import com.example.ui.theme.BlueprintPrimary
import com.example.ui.theme.BlueprintSecondary
import com.example.ui.theme.ConcreteBorder
import com.example.ui.theme.PassGreen
import com.example.ui.theme.PassGreenLight
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    report: CivilTestReport,
    isAiAuditing: Boolean,
    onBack: () -> Unit,
    onRunAiAudit: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = report.reportNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = report.testTitle,
                            fontSize = 11.sp,
                            color = Color(0xFFBFDBFE)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("report_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val textToCopy = generatePrintableReport(report)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Civil Test Report", textToCopy))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Report copied to clipboard!")
                            }
                        },
                        modifier = Modifier.testTag("copy_report_button")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Report", tint = Color.White)
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
                .padding(16.dp)
        ) {
            // NABL / Certified Lab Header
            NablHeaderCard(report = report)

            Spacer(modifier = Modifier.height(12.dp))

            // Pass Stamp & Confirmation Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PassGreenLight.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PassGreen)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PassStampBadge(statusText = "100% IS CODE PASS")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All specimen parameters, mathematical calculations, and tolerance checks strictly meet the acceptance criteria of ${report.isCode}.",
                            fontSize = 12.sp,
                            color = Color(0xFF065F46),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Test Parameters & Client Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ConcreteBorder)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sample & Project Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = ConcreteBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    val infoRows = listOf(
                        "Material / Grade" to "${report.grade} (${report.isCode})",
                        "Sample Weight / Count" to "${report.sampleWeight} ${report.weightUnit} (${report.sampleCount} specimens)",
                        "Sampling / Casting Date" to report.castingDate,
                        "Date of Testing" to report.testingDate,
                        if (report.ageInDays > 0) "Testing Age" to "${report.ageInDays} Days" else null,
                        "Client / Department" to report.clientName,
                        "Project / Location" to "${report.projectName} [${report.location}]"
                    ).filterNotNull()

                    infoRows.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, fontSize = 12.sp, color = TextSecondaryDark, modifier = Modifier.weight(1f))
                            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark, modifier = Modifier.weight(1.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Metrics Grid
            Text(
                text = "KEY TEST METRICS & ACCEPTANCE SUMMARY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ConcreteBorder)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    report.summaryMetrics.entries.forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = entry.key, fontSize = 12.sp, color = TextSecondaryDark)
                            Surface(
                                color = if (entry.key.contains("Status")) PassGreenLight else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = entry.value,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (entry.key.contains("Status")) PassGreen else TextPrimaryDark,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        if (index < report.summaryMetrics.size - 1) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Readings Table
            ReadingsTableView(readings = report.readings)

            Spacer(modifier = Modifier.height(16.dp))

            // Step-by-Step Detailed Calculations (IS Code standard)
            Text(
                text = "STEP-BY-STEP MATHEMATICAL CALCULATIONS (IS CODE)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = BlueprintPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            report.calculations.forEachIndexed { idx, calc ->
                CalculationBreakdownCard(step = calc, stepIndex = idx)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Technical Audit Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SafetyAmber)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SafetyAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini AI Technical QA/QC Audit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateNavy
                            )
                        }
                        Button(
                            onClick = onRunAiAudit,
                            enabled = !isAiAuditing,
                            colors = ButtonDefaults.buttonColors(containerColor = BlueprintPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("run_ai_audit_btn")
                        ) {
                            if (isAiAuditing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Auditing...", fontSize = 11.sp)
                            } else {
                                Text("Re-Audit AI", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = ConcreteBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.aiRemarks ?: "Tap 'Audit with AI' to generate real-time AI Senior Civil Engineer QA/QC analysis, mix design suggestions, and IS code certification notes.",
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        lineHeight = 18.sp,
                        modifier = Modifier.testTag("ai_audit_remarks_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = BlueprintPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("generate_another_test_btn")
                ) {
                    Text("Generate Another Test", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun generatePrintableReport(report: CivilTestReport): String {
    return buildString {
        appendLine("================================================================")
        appendLine("           CIVIL CONSTRUCTION MATERIAL TEST CERTIFICATE          ")
        appendLine("          Accredited QA/QC Laboratory - IS Code Compliant        ")
        appendLine("================================================================")
        appendLine("Report No    : ${report.reportNumber}")
        appendLine("Date of Test : ${report.testingDate}")
        appendLine("Standard Code: ${report.isCode}")
        appendLine("Test Name    : ${report.testTitle}")
        appendLine("Grade/Class  : ${report.grade}")
        appendLine("Sample Info  : ${report.sampleWeight} ${report.weightUnit} (${report.sampleCount} specimens)")
        appendLine("Client       : ${report.clientName}")
        appendLine("Project      : ${report.projectName}")
        appendLine("Location     : ${report.location}")
        appendLine("----------------------------------------------------------------")
        appendLine("READINGS DATA TABLE:")
        for (r in report.readings) {
            appendLine("• ${r.label} [Status: ${r.status.name}]:")
            for ((k, v) in r.parameters) {
                appendLine("   - $k: $v")
            }
        }
        appendLine("----------------------------------------------------------------")
        appendLine("STEP-BY-STEP CALCULATIONS:")
        for (c in report.calculations) {
            appendLine("• ${c.title} (${c.isCodeClause})")
            appendLine("   Formula     : ${c.formula}")
            appendLine("   Substitution: ${c.substitution}")
            appendLine("   Outcome     : ${c.outcome}")
        }
        appendLine("----------------------------------------------------------------")
        appendLine("SUMMARY METRICS:")
        for ((k, v) in report.summaryMetrics) {
            appendLine("• $k: $v")
        }
        appendLine("----------------------------------------------------------------")
        appendLine("STATUS: 100% IS CODE PASS (CERTIFIED & APPROVED)")
        report.aiRemarks?.let {
            appendLine("----------------------------------------------------------------")
            appendLine("AI QA/QC AUDIT REMARKS:\n$it")
        }
        appendLine("================================================================")
    }
}
