package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationStep
import com.example.data.model.CivilTestReport
import com.example.data.model.PassStatus
import com.example.data.model.ReadingRow
import com.example.data.model.ValidationResult
import com.example.ui.theme.BlueprintPrimary
import com.example.ui.theme.BlueprintSecondary
import com.example.ui.theme.ConcreteBorder
import com.example.ui.theme.FailRed
import com.example.ui.theme.FailRedLight
import com.example.ui.theme.PassGreen
import com.example.ui.theme.PassGreenLight
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberLight
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarnOrange

@Composable
fun PassStampBadge(
    modifier: Modifier = Modifier,
    statusText: String = "IS CODE CONFORMING - PASS"
) {
    Surface(
        modifier = modifier
            .border(2.dp, PassGreen, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        color = PassGreenLight.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Pass Check",
                tint = PassGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusText,
                color = PassGreen,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ValidationBanner(
    validation: ValidationResult,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, icon, iconColor, title) = when {
        validation.hasErrors -> {
            Tuple5(
                FailRedLight,
                FailRed,
                Icons.Default.Error,
                FailRed,
                "IS Code Non-Conformance Detected"
            )
        }
        validation.warnings.isNotEmpty() -> {
            Tuple5(
                SafetyAmberLight,
                SafetyAmber,
                Icons.Default.Warning,
                WarnOrange,
                "Automatic Validation: Advisory Warning"
            )
        }
        else -> {
            Tuple5(
                PassGreenLight,
                PassGreen,
                Icons.Default.CheckCircle,
                PassGreen,
                "Automatic Validation: 100% IS Code Compliant"
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryDark
                )
            }

            if (validation.errors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                validation.errors.forEach { err ->
                    Text(
                        text = "• $err",
                        fontSize = 12.sp,
                        color = FailRed,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                    )
                }
            }

            if (validation.warnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                validation.warnings.forEach { warn ->
                    Text(
                        text = "• $warn",
                        fontSize = 12.sp,
                        color = WarnOrange,
                        modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                    )
                }
            }

            if (validation.recommendations.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = validation.recommendations,
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    lineHeight = 16.sp
                )
            }

            if (validation.clauseReferences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = BlueprintPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Standards: " + validation.clauseReferences.joinToString(", "),
                        fontSize = 11.sp,
                        color = BlueprintPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
fun ReadingsTableView(
    readings: List<ReadingRow>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = BlueprintPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Individual Test Readings Table",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SlateNavy
                    )
                }
                Surface(
                    color = PassGreenLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "All Readings Conforming",
                        color = PassGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = ConcreteBorder)

            readings.forEachIndexed { idx, row ->
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (idx % 2 == 0) Color(0xFFF8FAFC) else Color.White,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BlueprintPrimary
                        )
                        Surface(
                            color = when (row.status) {
                                PassStatus.PASS -> PassGreenLight
                                PassStatus.FAIL -> FailRedLight
                                PassStatus.WARNING -> SafetyAmberLight
                            },
                            shape = CircleShape
                        ) {
                            Text(
                                text = row.status.name,
                                color = when (row.status) {
                                    PassStatus.PASS -> PassGreen
                                    PassStatus.FAIL -> FailRed
                                    PassStatus.WARNING -> WarnOrange
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    row.parameters.entries.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            pair.forEach { entry ->
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.key,
                                        fontSize = 11.sp,
                                        color = TextSecondaryDark
                                    )
                                    Text(
                                        text = entry.value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (row.note.isNotBlank()) {
                        Text(
                            text = "Note: ${row.note}",
                            fontSize = 10.sp,
                            color = TextSecondaryDark,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculationBreakdownCard(
    step: CalculationStep,
    stepIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ConcreteBorder)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SlateNavy
                )
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = step.isCodeClause,
                        color = BlueprintPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mathematical formula box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "Formula: ${step.formula}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Substitution: ${step.substitution}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Result: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Text(
                    text = step.outcome,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PassGreen
                )
            }
        }
    }
}

@Composable
fun NablHeaderCard(
    report: CivilTestReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlueprintPrimary),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "QUALITY ASSURANCE & TESTING LAB",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CIVIL MATERIAL TEST CERTIFICATE",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Surface(
                    color = SafetyAmber,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "NABL / IS",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Report Number", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                    Text(report.reportNumber, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Standard IS Code", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                    Text(report.isCode, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Project / Site", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                    Text(report.projectName, color = Color.White, fontSize = 12.sp, maxLines = 1)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Testing Date", color = Color(0xFFBFDBFE), fontSize = 11.sp)
                    Text(report.testingDate, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
