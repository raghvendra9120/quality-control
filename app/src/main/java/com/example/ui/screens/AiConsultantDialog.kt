package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BlueprintPrimary
import com.example.ui.theme.ConcreteBorder
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SlateNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AiConsultantDialog(
    isLoading: Boolean,
    response: String?,
    onAsk: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var queryText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "IS 456 Table 11 concrete acceptance criteria",
        "IS 383 Sand Zone II grading limits & FM",
        "Fe 500D rebar yield stress & elongation (IS 1786)",
        "Minimum 7-day & 28-day strength of M25 concrete",
        "Aggregate Impact Value (AIV) max limit for highway"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("ai_consultant_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SafetyAmber,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini AI Civil Consultant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SlateNavy
                            )
                            Text(
                                text = "IS Codes, QA/QC testing & Mix Design",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ConcreteBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Quick chips
                Text("Quick IS Code Questions:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueprintPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickQuestions.forEach { q ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.clickable {
                                queryText = q
                                onAsk(q)
                            }
                        ) {
                            Text(
                                text = q,
                                fontSize = 11.sp,
                                color = TextPrimaryDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Query input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = { Text("Ask any IS Code / testing question...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_query_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (queryText.isNotBlank()) {
                                onAsk(queryText)
                            }
                        },
                        enabled = !isLoading && queryText.isNotBlank(),
                        modifier = Modifier
                            .background(BlueprintPrimary, RoundedCornerShape(8.dp))
                            .size(48.dp)
                            .testTag("send_ai_query_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Response Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = BlueprintPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consulting Gemini Civil Engineering Knowledge...", fontSize = 12.sp, color = BlueprintPrimary)
                        }
                    } else if (!response.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SafetyAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IS Code Technical Analysis:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateNavy)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = response,
                            fontSize = 12.sp,
                            color = TextPrimaryDark,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "Ask any question about concrete strength criteria, steel tensile limits, sand sieve zone boundaries, silt content, soil compaction, or NABL laboratory standards.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
