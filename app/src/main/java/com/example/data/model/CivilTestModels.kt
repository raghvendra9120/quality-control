package com.example.data.model

enum class TestCategory(val displayName: String, val iconName: String) {
    CONCRETE("Concrete Cube Test", "architecture"),
    CEMENT("Cement Testing", "science"),
    SAND("Sand / Fine Aggregate", "grain"),
    COARSE_AGGREGATE("Coarse Aggregate", "terrain"),
    STEEL_REBAR("TMT Steel Rebar", "hardware"),
    SOIL("Soil Compaction / FDD", "landscape"),
    BITUMEN("Bitumen / Asphalt", "opacity"),
    BRICKS("Brick / Masonry", "view_module")
}

enum class PassStatus {
    PASS,
    FAIL,
    WARNING
}

data class TestDefinition(
    val id: String,
    val title: String,
    val category: TestCategory,
    val isCode: String,
    val description: String,
    val grades: List<String>,
    val defaultGrade: String,
    val defaultWeight: Double,
    val weightUnit: String,
    val defaultSampleCount: Int,
    val sampleLabel: String
)

data class CalculationStep(
    val title: String,
    val formula: String,
    val substitution: String,
    val outcome: String,
    val isCodeClause: String
)

data class ReadingRow(
    val index: Int,
    val label: String,
    val parameters: Map<String, String>,
    val status: PassStatus = PassStatus.PASS,
    val note: String = ""
)

data class ValidationResult(
    val isPassing: Boolean,
    val hasErrors: Boolean,
    val warnings: List<String> = emptyList(),
    val errors: List<String> = emptyList(),
    val clauseReferences: List<String> = emptyList(),
    val recommendations: String = ""
)

data class CivilTestReport(
    val reportNumber: String,
    val testId: String,
    val testTitle: String,
    val category: TestCategory,
    val isCode: String,
    val grade: String,
    val sampleWeight: Double,
    val weightUnit: String,
    val sampleCount: Int,
    val clientName: String,
    val projectName: String,
    val location: String,
    val castingDate: String,
    val testingDate: String,
    val ageInDays: Int,
    val readings: List<ReadingRow>,
    val calculations: List<CalculationStep>,
    val summaryMetrics: Map<String, String>,
    val validation: ValidationResult,
    val aiRemarks: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
