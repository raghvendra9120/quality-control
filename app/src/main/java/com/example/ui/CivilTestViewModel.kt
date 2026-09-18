package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiCivilService
import com.example.data.db.CivilReportDatabase
import com.example.data.db.CivilReportEntity
import com.example.data.engine.CivilTestEngine
import com.example.data.model.CivilTestReport
import com.example.data.model.TestDefinition
import com.example.data.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CivilUiState(
    val selectedTest: TestDefinition = CivilTestEngine.allTests.first(),
    val selectedGrade: String = CivilTestEngine.allTests.first().defaultGrade,
    val sampleWeightInput: String = CivilTestEngine.allTests.first().defaultWeight.toString(),
    val sampleCount: Int = CivilTestEngine.allTests.first().defaultSampleCount,
    val ageInDays: Int = 28,
    val clientName: String = "National Highways Authority of India (NHAI)",
    val projectName: String = "Six-Lane Expressway Project (Pkg-IV)",
    val location: String = "Structure P-14 / Batching Plant QC Lab",
    val liveValidation: ValidationResult = ValidationResult(isPassing = true, hasErrors = false),
    val currentReport: CivilTestReport? = null,
    val savedReports: List<CivilTestReport> = emptyList(),
    val isGenerating: Boolean = false,
    val isAiAuditing: Boolean = false,
    val isAiChatLoading: Boolean = false,
    val aiConsultantResponse: String? = null,
    val notificationMessage: String? = null
)

class CivilTestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CivilReportDatabase.getDatabase(application)
    private val dao = db.reportDao()
    private val aiService = GeminiCivilService()

    private val _uiState = MutableStateFlow(CivilUiState())
    val uiState: StateFlow<CivilUiState> = _uiState.asStateFlow()

    init {
        // Run initial live validation
        revalidate()

        // Observe saved reports
        viewModelScope.launch {
            dao.getAllReports().collectLatest { entities ->
                val domainReports = entities.map { it.toDomain() }
                _uiState.update { it.copy(savedReports = domainReports) }
            }
        }

        // Auto-generate one default pristine passing report on first launch
        viewModelScope.launch {
            val count = dao.getReportCount()
            if (count == 0) {
                generatePassingReport()
            }
        }
    }

    fun selectTest(test: TestDefinition) {
        _uiState.update {
            it.copy(
                selectedTest = test,
                selectedGrade = test.defaultGrade,
                sampleWeightInput = test.defaultWeight.toString(),
                sampleCount = test.defaultSampleCount
            )
        }
        revalidate()
    }

    fun selectGrade(grade: String) {
        _uiState.update { it.copy(selectedGrade = grade) }
        revalidate()
    }

    fun updateSampleWeight(weightStr: String) {
        _uiState.update { it.copy(sampleWeightInput = weightStr) }
        revalidate()
    }

    fun updateSampleCount(count: Int) {
        _uiState.update { it.copy(sampleCount = count.coerceIn(1, 10)) }
        revalidate()
    }

    fun updateAgeInDays(days: Int) {
        _uiState.update { it.copy(ageInDays = days) }
        revalidate()
    }

    fun updateProjectDetails(client: String, project: String, loc: String) {
        _uiState.update {
            it.copy(
                clientName = client,
                projectName = project,
                location = loc
            )
        }
    }

    private fun revalidate() {
        val state = _uiState.value
        val weight = state.sampleWeightInput.toDoubleOrNull() ?: 0.0
        val validation = CivilTestEngine.validateCustomInputs(
            testId = state.selectedTest.id,
            grade = state.selectedGrade,
            sampleWeight = weight,
            sampleCount = state.sampleCount
        )
        _uiState.update { it.copy(liveValidation = validation) }
    }

    fun generatePassingReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            val state = _uiState.value
            val weight = state.sampleWeightInput.toDoubleOrNull() ?: state.selectedTest.defaultWeight

            val report = CivilTestEngine.generatePassingReport(
                testId = state.selectedTest.id,
                grade = state.selectedGrade,
                sampleWeight = weight,
                sampleCount = state.sampleCount,
                ageInDays = state.ageInDays,
                clientName = state.clientName,
                projectName = state.projectName,
                location = state.location
            )

            // Save report to Room DB
            dao.insertReport(CivilReportEntity.fromDomain(report))

            _uiState.update {
                it.copy(
                    isGenerating = false,
                    currentReport = report,
                    notificationMessage = "Test report ${report.reportNumber} successfully generated with 100% IS Code PASS compliance!"
                )
            }
        }
    }

    fun triggerAiAudit() {
        val current = _uiState.value.currentReport ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAiAuditing = true) }
            val auditRemarks = aiService.auditReport(current)
            val updatedReport = current.copy(aiRemarks = auditRemarks)
            dao.insertReport(CivilReportEntity.fromDomain(updatedReport))
            _uiState.update {
                it.copy(
                    isAiAuditing = false,
                    currentReport = updatedReport,
                    notificationMessage = "AI Technical Audit completed successfully!"
                )
            }
        }
    }

    fun askAi(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAiChatLoading = true) }
            val answer = aiService.askCivilEngineer(question)
            _uiState.update {
                it.copy(
                    isAiChatLoading = false,
                    aiConsultantResponse = answer
                )
            }
        }
    }

    fun setCurrentReport(report: CivilTestReport) {
        _uiState.update { it.copy(currentReport = report) }
    }

    fun deleteReport(reportNo: String) {
        viewModelScope.launch {
            dao.deleteReport(reportNo)
            if (_uiState.value.currentReport?.reportNumber == reportNo) {
                _uiState.update { it.copy(currentReport = null) }
            }
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }
}
