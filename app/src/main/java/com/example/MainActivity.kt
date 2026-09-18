package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CivilTestViewModel
import com.example.ui.screens.AiConsultantDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ReportDetailScreen
import com.example.ui.theme.ConcreteSurface
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    HOME,
    REPORT_DETAIL,
    HISTORY
}

class MainActivity : ComponentActivity() {

    private val viewModel: CivilTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CivilTestApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CivilTestApp(viewModel: CivilTestViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var showAiDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ConcreteSurface
    ) {
        when (currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    uiState = uiState,
                    onSelectTest = { viewModel.selectTest(it) },
                    onSelectGrade = { viewModel.selectGrade(it) },
                    onUpdateWeight = { viewModel.updateSampleWeight(it) },
                    onUpdateCount = { viewModel.updateSampleCount(it) },
                    onUpdateAge = { viewModel.updateAgeInDays(it) },
                    onGenerateReport = {
                        viewModel.generatePassingReport()
                        currentScreen = AppScreen.REPORT_DETAIL
                    },
                    onViewCurrentReport = {
                        if (uiState.currentReport != null) {
                            currentScreen = AppScreen.REPORT_DETAIL
                        }
                    },
                    onOpenHistory = { currentScreen = AppScreen.HISTORY },
                    onOpenAiConsultant = { showAiDialog = true },
                    onClearNotification = { viewModel.clearNotification() }
                )
            }

            AppScreen.REPORT_DETAIL -> {
                BackHandler { currentScreen = AppScreen.HOME }
                uiState.currentReport?.let { report ->
                    ReportDetailScreen(
                        report = report,
                        isAiAuditing = uiState.isAiAuditing,
                        onBack = { currentScreen = AppScreen.HOME },
                        onRunAiAudit = { viewModel.triggerAiAudit() }
                    )
                } ?: run {
                    currentScreen = AppScreen.HOME
                }
            }

            AppScreen.HISTORY -> {
                BackHandler { currentScreen = AppScreen.HOME }
                HistoryScreen(
                    reports = uiState.savedReports,
                    onSelectReport = {
                        viewModel.setCurrentReport(it)
                        currentScreen = AppScreen.REPORT_DETAIL
                    },
                    onDeleteReport = { viewModel.deleteReport(it) },
                    onBack = { currentScreen = AppScreen.HOME }
                )
            }
        }

        if (showAiDialog) {
            AiConsultantDialog(
                isLoading = uiState.isAiChatLoading,
                response = uiState.aiConsultantResponse,
                onAsk = { viewModel.askAi(it) },
                onDismiss = { showAiDialog = false }
            )
        }
    }
}
