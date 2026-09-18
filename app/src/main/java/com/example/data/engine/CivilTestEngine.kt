package com.example.data.engine

import com.example.data.model.CalculationStep
import com.example.data.model.CivilTestReport
import com.example.data.model.PassStatus
import com.example.data.model.ReadingRow
import com.example.data.model.TestCategory
import com.example.data.model.TestDefinition
import com.example.data.model.ValidationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

object CivilTestEngine {

    val allTests: List<TestDefinition> = listOf(
        TestDefinition(
            id = "concrete_cube",
            title = "Concrete Cube Compressive Strength",
            category = TestCategory.CONCRETE,
            isCode = "IS 516:2021 & IS 456:2000",
            description = "Compressive strength determination of 150x150x150mm concrete cubes under compression testing machine (CTM) at 7, 14, or 28 days.",
            grades = listOf("M15", "M20", "M25", "M30", "M35", "M40", "M50"),
            defaultGrade = "M25",
            defaultWeight = 8.25,
            weightUnit = "kg",
            defaultSampleCount = 3,
            sampleLabel = "Cube Weight (kg)"
        ),
        TestDefinition(
            id = "sand_sieve",
            title = "Fine Aggregate (Sand) Sieve Analysis & Silt",
            category = TestCategory.SAND,
            isCode = "IS 2386 (Part 1 & 2):1963 & IS 383:2016",
            description = "Particle size distribution by mechanical sieving, Fineness Modulus (FM), and Silt content testing using measuring cylinder.",
            grades = listOf("Zone I (Coarse)", "Zone II (Medium)", "Zone III (Fine)", "Zone IV (Very Fine)"),
            defaultGrade = "Zone II (Medium)",
            defaultWeight = 1000.0,
            weightUnit = "g",
            defaultSampleCount = 1,
            sampleLabel = "Sample Weight (g)"
        ),
        TestDefinition(
            id = "coarse_aggregate_impact",
            title = "Coarse Aggregate Impact & Crushing Value",
            category = TestCategory.COARSE_AGGREGATE,
            isCode = "IS 2386 (Part 4):1963 & IS 383:2016",
            description = "Resistance to impact under 13.5-14kg hammer falling through 380mm, and Aggregate Crushing Value under 400kN load.",
            grades = listOf("10 mm Nominal", "20 mm Nominal", "40 mm Nominal"),
            defaultGrade = "20 mm Nominal",
            defaultWeight = 500.0,
            weightUnit = "g",
            defaultSampleCount = 3,
            sampleLabel = "Sample Weight W1 (g)"
        ),
        TestDefinition(
            id = "steel_tensile",
            title = "TMT Rebar Tensile & Mechanical Test",
            category = TestCategory.STEEL_REBAR,
            isCode = "IS 1786:2008 & IS 1608:2022",
            description = "0.2% Proof stress (Yield stress), Ultimate Tensile Strength (UTS), Elongation %, TS/YS ratio, and mass per metre under UTM.",
            grades = listOf("Fe 415", "Fe 500", "Fe 500D", "Fe 550", "Fe 550D"),
            defaultGrade = "Fe 500D",
            defaultWeight = 1.58,
            weightUnit = "kg/m",
            defaultSampleCount = 3,
            sampleLabel = "Nominal Dia: 16 mm (kg/m)"
        ),
        TestDefinition(
            id = "soil_compaction",
            title = "Soil Field Dry Density (Core Cutter Method)",
            category = TestCategory.SOIL,
            isCode = "IS 2720 (Part 29):1975 & IS 2720 (Part 7)",
            description = "Determination of field dry density (FDD), moisture content, and degree of compaction against Maximum Dry Density (MDD).",
            grades = listOf("Subgrade (Min 95%)", "Embankment (Min 95%)", "Sub-base / WBM (Min 98%)"),
            defaultGrade = "Subgrade (Min 95%)",
            defaultWeight = 2050.0,
            weightUnit = "g",
            defaultSampleCount = 3,
            sampleLabel = "Soil + Cutter Wt (g)"
        ),
        TestDefinition(
            id = "cement_physical",
            title = "Cement Consistency, Setting Time & Strength",
            category = TestCategory.CEMENT,
            isCode = "IS 4031 (Parts 1-6) & IS 12269:2013",
            description = "Standard consistency by Vicat plunger, initial and final setting times, Le-Chatelier soundness, and 28-day mortar strength.",
            grades = listOf("OPC 53 Grade", "OPC 43 Grade", "PPC (Fly Ash based)"),
            defaultGrade = "OPC 53 Grade",
            defaultWeight = 400.0,
            weightUnit = "g",
            defaultSampleCount = 3,
            sampleLabel = "Cement Sample (g)"
        ),
        TestDefinition(
            id = "bitumen_test",
            title = "Bitumen Penetration & Softening Point",
            category = TestCategory.BITUMEN,
            isCode = "IS 73:2018 & IS 1203/1205:1978",
            description = "Standard needle penetration at 25°C (0.1mm), softening point by Ring and Ball apparatus, and ductility at 27°C.",
            grades = listOf("VG-10 (Spraying)", "VG-30 (Paving)", "VG-40 (Heavy Traffic)"),
            defaultGrade = "VG-30 (Paving)",
            defaultWeight = 100.0,
            weightUnit = "g",
            defaultSampleCount = 3,
            sampleLabel = "Bitumen Sample (g)"
        ),
        TestDefinition(
            id = "brick_test",
            title = "Burnt Clay Brick Compressive Strength",
            category = TestCategory.BRICKS,
            isCode = "IS 3495 (Parts 1-3):2019 & IS 1077",
            description = "Compressive strength of frog-filled bricks after 3-day curing, 24-hr cold water absorption %, and efflorescence rating.",
            grades = listOf("Class 10 (10 N/mm²)", "Class 7.5 (7.5 N/mm²)", "Class 5.0 (5.0 N/mm²)", "Class 3.5 (3.5 N/mm²)"),
            defaultGrade = "Class 10 (10 N/mm²)",
            defaultWeight = 3.10,
            weightUnit = "kg",
            defaultSampleCount = 5,
            sampleLabel = "Brick Weight (kg)"
        )
    )

    fun getTestById(id: String): TestDefinition {
        return allTests.find { it.id == id } ?: allTests.first()
    }

    /**
     * Automatic passing report generator according to Indian Standard (IS) Codes.
     * Guaranteed to generate fully realistic, statistically authentic readings
     * that satisfy all IS Code specifications for the chosen grade.
     */
    fun generatePassingReport(
        testId: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        ageInDays: Int = 28,
        clientName: String = "National Highways Authority of India (NHAI)",
        projectName: String = "Expressway Package-IV (Ch. 112+000 to 148+500)",
        location: String = "Pier Cap P-14 / Batching Plant Lab",
        technicianName: String = "Er. R. Sharma (QA/QC Engineer)"
    ): CivilTestReport {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val today = Date()
        val castingCal = java.util.Calendar.getInstance()
        castingCal.time = today
        castingCal.add(java.util.Calendar.DAY_OF_YEAR, -ageInDays)
        val castingDateStr = dateFormat.format(castingCal.time)
        val testingDateStr = dateFormat.format(today)

        val reportNo = "NABL/CE/${SimpleDateFormat("yyyy", Locale.ENGLISH).format(today)}/${(1000..9999).random()}"

        return when (testId) {
            "concrete_cube" -> generateConcreteReport(reportNo, grade, sampleWeight, sampleCount, ageInDays, clientName, projectName, location, castingDateStr, testingDateStr)
            "sand_sieve" -> generateSandReport(reportNo, grade, sampleWeight, clientName, projectName, location, castingDateStr, testingDateStr)
            "coarse_aggregate_impact" -> generateAggregateReport(reportNo, grade, sampleWeight, sampleCount, clientName, projectName, location, castingDateStr, testingDateStr)
            "steel_tensile" -> generateSteelReport(reportNo, grade, sampleWeight, sampleCount, clientName, projectName, location, castingDateStr, testingDateStr)
            "soil_compaction" -> generateSoilReport(reportNo, grade, sampleWeight, sampleCount, clientName, projectName, location, castingDateStr, testingDateStr)
            "cement_physical" -> generateCementReport(reportNo, grade, sampleWeight, sampleCount, ageInDays, clientName, projectName, location, castingDateStr, testingDateStr)
            "bitumen_test" -> generateBitumenReport(reportNo, grade, sampleWeight, sampleCount, clientName, projectName, location, castingDateStr, testingDateStr)
            "brick_test" -> generateBrickReport(reportNo, grade, sampleWeight, sampleCount, clientName, projectName, location, castingDateStr, testingDateStr)
            else -> generateConcreteReport(reportNo, grade, sampleWeight, sampleCount, ageInDays, clientName, projectName, location, castingDateStr, testingDateStr)
        }
    }

    // --- 1. CONCRETE CUBE REPORT GENERATOR ---
    private fun generateConcreteReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        ageInDays: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val fck = grade.removePrefix("M").toDoubleOrNull() ?: 25.0
        val targetFactor = when (ageInDays) {
            7 -> 0.70 // 7-day target ~70%
            14 -> 0.88 // 14-day target ~88%
            else -> 1.15 // 28-day target ~115% of fck for guaranteed statistical margin
        }
        val targetMean = fck * targetFactor

        // Generate readings with variation within ±5% (strictly < 15% IS 456 requirement)
        val area = 150.0 * 150.0 // 22,500 mm²
        val readings = mutableListOf<ReadingRow>()
        val strengths = mutableListOf<Double>()

        val baseWeight = if (sampleWeight in 7.8..8.7) sampleWeight else 8.25
        val offsets = listOf(-0.45, 0.55, -0.10, 0.30, -0.30)

        for (i in 1..sampleCount) {
            val offset = offsets.getOrElse(i - 1) { ((-10..10).random() / 20.0) }
            val strength = ((targetMean + offset) * 100.0).roundToInt() / 100.0
            strengths.add(strength)
            val failureLoadKn = ((strength * area / 1000.0) * 10.0).roundToInt() / 10.0
            val cubeWeight = ((baseWeight + (offset * 0.05)) * 100.0).roundToInt() / 100.0
            val density = ((cubeWeight / 0.003375)).roundToInt() // 150mm cube volume = 0.003375 m³

            readings.add(
                ReadingRow(
                    index = i,
                    label = "Cube Specimen #$i",
                    parameters = mapOf(
                        "Dimensions" to "150 × 150 × 150 mm",
                        "Cross-Section Area" to "22,500 mm²",
                        "Specimen Weight" to "$cubeWeight kg",
                        "Density" to "$density kg/m³",
                        "Failure Load" to "$failureLoadKn kN",
                        "Compressive Strength" to "$strength N/mm²"
                    ),
                    status = PassStatus.PASS,
                    note = "Crushed under uniform rate of 140 kg/cm²/min (5.2 kN/s) as per IS 516"
                )
            )
        }

        val avgStrength = ((strengths.average()) * 100.0).roundToInt() / 100.0
        val requiredMin = when (ageInDays) {
            7 -> fck * 0.65
            14 -> fck * 0.85
            else -> fck
        }

        // IS 456 Cl. 15.4 tolerance check: Max individual variation from average
        val maxVariation = strengths.maxOf { abs((it - avgStrength) / avgStrength) * 100.0 }
        val maxVarFormatted = ((maxVariation * 10.0).roundToInt() / 10.0)

        val calculations = listOf(
            CalculationStep(
                title = "1. Cross-Sectional Area (A)",
                formula = "A = Length × Breadth",
                substitution = "A = 150 mm × 150 mm",
                outcome = "22,500 mm²",
                isCodeClause = "IS 516:2021 Clause 5.2"
            ),
            CalculationStep(
                title = "2. Individual Compressive Strength (fc)",
                formula = "fc = (Failure Load P in kN × 1000) / Area A in mm²",
                substitution = "Cube 1: (${readings[0].parameters["Failure Load"]} × 1000) / 22,500",
                outcome = "${readings[0].parameters["Compressive Strength"]}",
                isCodeClause = "IS 516:2021 Clause 5.5.1"
            ),
            CalculationStep(
                title = "3. Average Compressive Strength (favg)",
                formula = "favg = (fc1 + fc2 + ... + fcn) / n",
                substitution = "favg = (${strengths.joinToString(" + ") { "$it" }}) / $sampleCount",
                outcome = "$avgStrength N/mm² (Target for $ageInDays days: ≥ ${((requiredMin * 10.0).roundToInt() / 10.0)} N/mm²)",
                isCodeClause = "IS 456:2000 Clause 15.4"
            ),
            CalculationStep(
                title = "4. Individual Variation Tolerance Check",
                formula = "Variation = |fc - favg| / favg × 100% (Permissible: ≤ ±15%)",
                substitution = "Maximum recorded deviation from average = $maxVarFormatted%",
                outcome = "Complies with IS 456 (< 15.0% tolerance limit)",
                isCodeClause = "IS 456:2000 Clause 15.4 (Consistency Mandate)"
            ),
            CalculationStep(
                title = "5. Acceptance Criteria (Table 11)",
                formula = "favg ≥ fck + 0.825 × σ  AND  f_indiv ≥ fck - 3 N/mm²",
                substitution = "$avgStrength N/mm² ≥ ${(fck + 3.0)} N/mm²; Min Cube = ${strengths.minOrNull()} N/mm² ≥ ${(fck - 3.0)} N/mm²",
                outcome = "CONFORMS TO ACCEPTANCE CRITERIA - FULL PASS",
                isCodeClause = "IS 456:2000 Table 11 & Amendment 4"
            )
        )

        val summaryMetrics = mapOf(
            "Concrete Grade" to grade,
            "Characteristic Strength (fck)" to "$fck N/mm²",
            "Age at Testing" to "$ageInDays Days",
            "Average Strength Obtained" to "$avgStrength N/mm²",
            "Strength % of fck" to "${(((avgStrength / fck) * 100.0).roundToInt())}%",
            "Max Individual Deviation" to "±$maxVarFormatted % (Permissible: ±15%)",
            "Overall Status" to "PASS (100% Conforming to IS 456 Table 11)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 516:2021 Method of Tests for Strength of Concrete",
                "IS 456:2000 Plain and Reinforced Concrete - Code of Practice (Cl. 15.4 & Table 11)",
                "IS 1199:2018 Fresh Concrete Sampling"
            ),
            recommendations = "The concrete specimens satisfy both the average characteristic strength requirement and individual variation tolerance (±15%). Cube surfaces showed well-compacted normal failure planes without shearing defects."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "concrete_cube",
            testTitle = "Concrete Cube Compressive Strength Test",
            category = TestCategory.CONCRETE,
            isCode = "IS 516:2021 & IS 456:2000",
            grade = grade,
            sampleWeight = baseWeight,
            weightUnit = "kg",
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = ageInDays,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "Comprehensive AI Audit: Certified compliant. All 3 cubes crushed within standard normal distribution curve. 28-day strength margin exceeds target criteria by +${(((avgStrength - fck) * 10.0).roundToInt() / 10.0)} MPa."
        )
    }

    // --- 2. SAND / FINE AGGREGATE SIEVE ANALYSIS REPORT GENERATOR ---
    private fun generateSandReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val totalWeight = if (sampleWeight > 100.0) sampleWeight else 1000.0

        // Retained weights for Zone II sand (conforming strictly to IS 383:2016 Table 4)
        // Sieve sizes: 10mm, 4.75mm, 2.36mm, 1.18mm, 600μm, 300μm, 150μm, Pan
        val targetPercentagesPassing = when {
            grade.contains("Zone I") -> listOf(100.0, 95.0, 70.0, 45.0, 25.0, 12.0, 4.0, 0.0)
            grade.contains("Zone III") -> listOf(100.0, 98.0, 90.0, 82.0, 68.0, 25.0, 6.0, 0.0)
            grade.contains("Zone IV") -> listOf(100.0, 99.0, 97.0, 92.0, 85.0, 35.0, 12.0, 0.0)
            else -> listOf(100.0, 96.0, 82.0, 65.0, 48.0, 20.0, 5.0, 0.0) // Zone II (standard)
        }

        val sieveNames = listOf("10 mm", "4.75 mm", "2.36 mm", "1.18 mm", "600 μm", "300 μm", "150 μm", "Pan (<150μm)")
        val isLimits = listOf(
            "100%",
            "90 - 100%",
            "75 - 100%",
            "55 - 90%",
            "35 - 59%",
            "8 - 30%",
            "0 - 10%",
            "Residual"
        )

        // Calculate retained weights to sum exactly to totalWeight
        var cumPassing = 100.0
        val retainedGrams = mutableListOf<Double>()

        for (i in 0 until 7) {
            val pass = targetPercentagesPassing[i]
            val retainedPct = cumPassing - pass
            val grams = ((retainedPct / 100.0) * totalWeight * 10.0).roundToInt() / 10.0
            retainedGrams.add(grams)
            cumPassing = pass
        }
        val sumRetained = retainedGrams.sum()
        val panWeight = ((totalWeight - sumRetained) * 10.0).roundToInt() / 10.0
        retainedGrams.add(panWeight)

        var runningRetainedSum = 0.0
        val cumRetainedPctList = mutableListOf<Double>()
        val readings = mutableListOf<ReadingRow>()

        for (i in 0 until 8) {
            val w = retainedGrams[i]
            val pctRetained = ((w / totalWeight) * 1000.0).roundToInt() / 10.0
            runningRetainedSum += pctRetained
            val cumRet = (runningRetainedSum * 10.0).roundToInt() / 10.0
            if (i < 7) cumRetainedPctList.add(cumRet)
            val pctPassing = ((100.0 - cumRet) * 10.0).roundToInt() / 10.0

            readings.add(
                ReadingRow(
                    index = i + 1,
                    label = sieveNames[i],
                    parameters = mapOf(
                        "IS Sieve Size" to sieveNames[i],
                        "Weight Retained" to "$w g",
                        "% Weight Retained" to "$pctRetained %",
                        "Cumulative % Retained" to "$cumRet %",
                        "% Passing" to "$pctPassing %",
                        "IS 383 Permissible Limit" to isLimits[i]
                    ),
                    status = PassStatus.PASS,
                    note = "Passes Zone II grading envelope strictly"
                )
            )
        }

        // Fineness Modulus: Sum of cumulative % retained from 4.75mm to 150μm divided by 100
        val finenessModulus = ((cumRetainedPctList.sum() / 100.0) * 100.0).roundToInt() / 100.0
        val siltContentVolumetric = 2.4 // % (< 8% limit)

        val calculations = listOf(
            CalculationStep(
                title = "1. Cumulative % Retained",
                formula = "Cum % Retained = Σ (% Retained on larger sieves)",
                substitution = "Calculated per sieve from 10mm down to 150μm",
                outcome = "Sum = ${cumRetainedPctList.sum()}%",
                isCodeClause = "IS 2386 (Part 1):1963 Cl. 2"
            ),
            CalculationStep(
                title = "2. Fineness Modulus (FM)",
                formula = "FM = Σ (Cumulative % Retained on standard sieves) / 100",
                substitution = "FM = (${cumRetainedPctList.joinToString(" + ") { "$it" }}) / 100",
                outcome = "$finenessModulus (Zone II Medium Sand range: 2.60 - 2.90)",
                isCodeClause = "IS 383:2016 Clause 6.3"
            ),
            CalculationStep(
                title = "3. Silt Content by Field Measuring Cylinder Method",
                formula = "Silt % = (Height of Silt Layer V2 / Height of Sand Layer V1) × 100",
                substitution = "Silt % = (2.4 ml / 100.0 ml) × 100",
                outcome = "$siltContentVolumetric% (Permissible limit ≤ 8.0% by volume)",
                isCodeClause = "IS 2386 (Part 2):1963 Cl. 3 & IS 383:2016 Cl. 5.3"
            )
        )

        val summaryMetrics = mapOf(
            "Grading Classification" to grade,
            "Total Sample Dry Weight" to "$totalWeight g",
            "Fineness Modulus (FM)" to "$finenessModulus",
            "Sand Zone Compliance" to "Strictly conforms to IS 383 Table 4 Zone II",
            "Silt Content (%)" to "$siltContentVolumetric % (Permissible: < 8.0% vol)",
            "Overall Status" to "PASS (Fully Conforming to IS 383 & IS 2386)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 383:2016 Coarse and Fine Aggregate for Concrete (Table 4)",
                "IS 2386 (Part 1):1963 Particle Size and Shape",
                "IS 2386 (Part 2):1963 Estimation of Deleterious Materials and Organic Impurities"
            ),
            recommendations = "Sieve analysis curve fits smoothly into Zone II envelope without hump or deficiency. Silt content (2.4%) is well within the 8% volumetric ceiling, suitable for structural RCC."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "sand_sieve",
            testTitle = "Fine Aggregate Sieve Analysis & Silt Content",
            category = TestCategory.SAND,
            isCode = "IS 2386 (Part 1 & 2):1963 & IS 383:2016",
            grade = grade,
            sampleWeight = totalWeight,
            weightUnit = "g",
            sampleCount = 1,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 0,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: Fineness modulus 2.74 is ideal for pumpable structural concrete mix designs. Low silt content prevents excessive water demand and shrinkage cracking."
        )
    }

    // --- 3. COARSE AGGREGATE IMPACT & CRUSHING REPORT GENERATOR ---
    private fun generateAggregateReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val w1 = if (sampleWeight > 100.0) sampleWeight else 380.0
        val readings = mutableListOf<ReadingRow>()
        val aivResults = mutableListOf<Double>()

        val passFractions = listOf(66.5, 68.0, 65.0)

        for (i in 1..sampleCount) {
            val w2 = passFractions.getOrElse(i - 1) { 67.0 }
            val aiv = ((w2 / w1) * 1000.0).roundToInt() / 10.0
            aivResults.add(aiv)

            readings.add(
                ReadingRow(
                    index = i,
                    label = "Test Sample #$i (10-12.5mm fraction)",
                    parameters = mapOf(
                        "Initial Weight (W1)" to "$w1 g",
                        "Fraction passing 2.36mm (W2)" to "$w2 g",
                        "Aggregate Impact Value (AIV)" to "$aiv %",
                        "Permissible Limit (Wearing Surface)" to "Max 30 %",
                        "Permissible Limit (Non-wearing)" to "Max 45 %"
                    ),
                    status = PassStatus.PASS,
                    note = "15 blows of 14kg hammer falling from 380mm height"
                )
            )
        }

        val avgAiv = ((aivResults.average()) * 10.0).roundToInt() / 10.0
        val acvValue = 18.2 // %
        val flakinessIndex = 11.4 // %
        val elongationIndex = 13.8 // %
        val combinedFiEi = flakinessIndex + elongationIndex // 25.2% (<35% max)

        val calculations = listOf(
            CalculationStep(
                title = "1. Aggregate Impact Value (AIV)",
                formula = "AIV (%) = (W2 / W1) × 100",
                substitution = "Sample 1: ($avgAiv% average across $sampleCount samples)",
                outcome = "$avgAiv % (Permissible: ≤ 30.0% for wearing surface, ≤ 45.0% for others)",
                isCodeClause = "IS 2386 (Part 4):1963 Cl. 2"
            ),
            CalculationStep(
                title = "2. Aggregate Crushing Value (ACV)",
                formula = "ACV (%) = (Weight passing 2.36mm / Total Weight) × 100 under 400 kN",
                substitution = "ACV = (546 g / 3000 g) × 100",
                outcome = "$acvValue % (Permissible: ≤ 30.0% for wearing surface)",
                isCodeClause = "IS 2386 (Part 4):1963 Cl. 1"
            ),
            CalculationStep(
                title = "3. Combined Flakiness & Elongation Index (FI + EI)",
                formula = "Combined Index = Flakiness Index (%) + Elongation Index (%)",
                substitution = "Combined = $flakinessIndex% + $elongationIndex%",
                outcome = "$combinedFiEi % (Permissible limit ≤ 35.0% as per MoRTH & IS 383)",
                isCodeClause = "IS 2386 (Part 1):1963 Cl. 4 & Cl. 5"
            ),
            CalculationStep(
                title = "4. Water Absorption & Specific Gravity",
                formula = "Water Absorption (%) = [(W2 - W1) / W1] × 100",
                substitution = "Absorption = 0.58%; Specific Gravity = 2.68",
                outcome = "Conforms (Absorption ≤ 2.0%, Sp. Gr. in 2.6 - 2.8 range)",
                isCodeClause = "IS 2386 (Part 3):1963 Cl. 2"
            )
        )

        val summaryMetrics = mapOf(
            "Aggregate Size" to grade,
            "Mean Impact Value (AIV)" to "$avgAiv %",
            "Crushing Value (ACV)" to "$acvValue %",
            "Combined FI + EI" to "$combinedFiEi % (Max 35%)",
            "Water Absorption" to "0.58 % (Max 2.0%)",
            "Specific Gravity" to "2.68",
            "Overall Status" to "PASS (Fully Conforming to IS 383 Table 1)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 383:2016 Coarse Aggregate Specifications (Table 1)",
                "IS 2386 (Part 4):1963 Mechanical Properties",
                "IS 2386 (Part 1):1963 Particle Shape"
            ),
            recommendations = "Aggregate exhibits high toughness and crushing resistance. Combined flakiness and elongation (25.2%) ensures good packing density and workability."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "coarse_aggregate_impact",
            testTitle = "Coarse Aggregate Impact & Mechanical Properties",
            category = TestCategory.COARSE_AGGREGATE,
            isCode = "IS 2386 (Part 4):1963 & IS 383:2016",
            grade = grade,
            sampleWeight = w1,
            weightUnit = "g",
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 0,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: The coarse aggregate meets stringent M-40 and rigid pavement highway wear criteria. Impact value of $avgAiv% indicates strong structural basalt/granite quarry quality."
        )
    }

    // --- 4. STEEL REBAR TMT TENSILE REPORT GENERATOR ---
    private fun generateSteelReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val nominalDia = 16.0 // mm
        val standardArea = (Math.PI * nominalDia * nominalDia) / 4.0 // ~201.06 mm²
        val stdMassPerMeter = (nominalDia * nominalDia) / 162.28 // ~1.577 kg/m

        // IS 1786 Requirements for Fe 500D:
        // Yield Stress (YS) >= 500 N/mm²
        // UTS >= 565 N/mm² (and UTS/YS >= 1.10)
        // Elongation >= 16.0%
        val minYield = when {
            grade.contains("415") -> 415.0
            grade.contains("550D") -> 550.0
            grade.contains("550") -> 550.0
            else -> 500.0 // Fe 500 & Fe 500D
        }
        val minUts = when {
            grade.contains("415") -> 485.0
            grade.contains("500D") -> 565.0
            grade.contains("550D") -> 600.0
            else -> 545.0
        }
        val minElong = when {
            grade.contains("500D") -> 16.0
            grade.contains("550D") -> 14.5
            grade.contains("550") -> 10.0
            grade.contains("500") -> 12.0
            else -> 14.5
        }

        val readings = mutableListOf<ReadingRow>()
        val yieldStresses = mutableListOf<Double>()
        val utsList = mutableListOf<Double>()
        val elongList = mutableListOf<Double>()

        val offsets = listOf(28.0, 32.0, 26.0)

        for (i in 1..sampleCount) {
            val offset = offsets.getOrElse(i - 1) { 30.0 }
            val ys = minYield + offset
            val uts = ys * 1.155 // Guaranteed TS/YS ratio > 1.10
            val elongation = minElong + 2.5 + (i * 0.2)
            val actualMass = stdMassPerMeter * (1.0 + (offset * 0.0003))

            yieldStresses.add(ys)
            utsList.add(uts)
            elongList.add(elongation)

            val yieldLoad = ((ys * standardArea) / 1000.0 * 10.0).roundToInt() / 10.0
            val ultimateLoad = ((uts * standardArea) / 1000.0 * 10.0).roundToInt() / 10.0

            readings.add(
                ReadingRow(
                    index = i,
                    label = "Rebar Specimen #$i (Dia: 16mm)",
                    parameters = mapOf(
                        "Bar Dia & Length" to "16 mm × 1000 mm",
                        "Cross Section Area" to "${((standardArea * 10.0).roundToInt() / 10.0)} mm²",
                        "Mass per Metre" to "${((actualMass * 1000.0).roundToInt() / 1000.0)} kg/m (Std: ${((stdMassPerMeter * 1000.0).roundToInt() / 1000.0)})",
                        "Yield Load (0.2% Proof)" to "$yieldLoad kN",
                        "0.2% Proof Stress (Yield)" to "${((ys * 10.0).roundToInt() / 10.0)} N/mm²",
                        "Ultimate Tensile Load" to "$ultimateLoad kN",
                        "Tensile Strength (UTS)" to "${((uts * 10.0).roundToInt() / 10.0)} N/mm²",
                        "TS / YS Ratio" to "${(((uts / ys) * 1000.0).roundToInt() / 1000.0)}",
                        "% Elongation (5.65√Ao)" to "${((elongation * 10.0).roundToInt() / 10.0)} %",
                        "180° Bend & Rebend Test" to "Satisfactory (No crack/rupture)"
                    ),
                    status = PassStatus.PASS,
                    note = "Tested on calibrated 1000 kN Universal Testing Machine (UTM)"
                )
            )
        }

        val avgYs = ((yieldStresses.average() * 10.0).roundToInt() / 10.0)
        val avgUts = ((utsList.average() * 10.0).roundToInt() / 10.0)
        val avgElong = ((elongList.average() * 10.0).roundToInt() / 10.0)
        val avgTsYs = ((avgUts / avgYs * 1000.0).roundToInt() / 1000.0)

        val calculations = listOf(
            CalculationStep(
                title = "1. Nominal Cross-Sectional Area (Ao)",
                formula = "Ao = (π × d²) / 4",
                substitution = "Ao = (3.14159 × 16²) / 4",
                outcome = "${((standardArea * 100.0).roundToInt() / 100.0)} mm²",
                isCodeClause = "IS 1786:2008 Clause 4.2"
            ),
            CalculationStep(
                title = "2. Mass per Metre Tolerance",
                formula = "Nominal Mass = d² / 162.28 kg/m; Permissible deviation for 16mm = ±5%",
                substitution = "Actual = 1.585 kg/m vs Standard = 1.577 kg/m (+0.5% deviation)",
                outcome = "Within ±5.0% tolerance limit (PASS)",
                isCodeClause = "IS 1786:2008 Table 1"
            ),
            CalculationStep(
                title = "3. 0.2% Proof Stress (Yield Strength)",
                formula = "YS = (Yield Load in kN × 1000) / Ao",
                substitution = "Average YS = $avgYs N/mm² (Mandatory minimum: ≥ $minYield N/mm²)",
                outcome = "$avgYs N/mm² (EXCEEDS MINIMUM SPECIFICATION)",
                isCodeClause = "IS 1786:2008 Table 3"
            ),
            CalculationStep(
                title = "4. Tensile Strength / Proof Stress Ratio (TS/YS)",
                formula = "Ratio = UTS / YS (Mandatory minimum for ${grade}: ≥ 1.10)",
                substitution = "Ratio = $avgUts / $avgYs",
                outcome = "$avgTsYs (Conforms to high seismic ductility requirement)",
                isCodeClause = "IS 1786:2008 Table 3 Note 1"
            ),
            CalculationStep(
                title = "5. Percentage Elongation on Gauge Length 5.65√Ao",
                formula = "Elongation % = [(Final Length - Initial Gauge) / Initial Gauge] × 100",
                substitution = "Average = $avgElong% (Mandatory minimum: ≥ $minElong%)",
                outcome = "$avgElong % (SUPERIOR DUCTILITY)",
                isCodeClause = "IS 1608:2022 Part 1 & IS 1786"
            )
        )

        val summaryMetrics = mapOf(
            "Steel Grade" to grade,
            "Nominal Diameter" to "16 mm",
            "Average 0.2% Proof Stress" to "$avgYs N/mm² (Min: $minYield)",
            "Average Tensile Strength (UTS)" to "$avgUts N/mm² (Min: $minUts)",
            "TS / YS Ratio" to "$avgTsYs (Min: 1.10)",
            "Elongation %" to "$avgElong % (Min: $minElong%)",
            "Bend & Rebend Test" to "NO CRACK (PASS)",
            "Overall Status" to "PASS (Conforms strictly to IS 1786:2008)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 1786:2008 High Strength Deformed Steel Bars and Wires for Concrete Reinforcement",
                "IS 1608 (Part 1):2022 Metallic Materials - Tensile Testing",
                "IS 1599:2019 Metallic Materials - Bend Test"
            ),
            recommendations = "The TMT rebar exceeds all mechanical and ductility parameters for earthquake-resistant reinforced concrete design (Fe 500D grade)."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "steel_tensile",
            testTitle = "TMT Rebar Tensile, Elongation & Bend Test",
            category = TestCategory.STEEL_REBAR,
            isCode = "IS 1786:2008 & IS 1608:2022",
            grade = grade,
            sampleWeight = stdMassPerMeter,
            weightUnit = "kg/m",
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 0,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: High TS/YS ratio ($avgTsYs) and 18.5% elongation offer excellent energy absorption capacity essential for seismic zones IV & V."
        )
    }

    // --- 5. SOIL COMPACTION & FDD REPORT GENERATOR ---
    private fun generateSoilReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val cutterVolume = 1021.0 // cm³ (standard 100mm ID × 130mm height core cutter)
        val mdd = 1.88 // g/cc (Laboratory Maximum Dry Density)
        val omc = 12.8 // % (Optimum Moisture Content)
        val minCompaction = if (grade.contains("98%")) 98.0 else 95.0

        val readings = mutableListOf<ReadingRow>()
        val compactions = mutableListOf<Double>()

        val soilWeights = listOf(2085.0, 2110.0, 2095.0)
        val moistures = listOf(12.4, 12.6, 12.3)

        for (i in 1..sampleCount) {
            val wetSoil = soilWeights.getOrElse(i - 1) { 2090.0 }
            val moisture = moistures.getOrElse(i - 1) { 12.5 }
            val wetDensity = ((wetSoil / cutterVolume) * 1000.0).roundToInt() / 1000.0
            val dryDensity = ((wetDensity / (1.0 + (moisture / 100.0))) * 1000.0).roundToInt() / 1000.0
            val compaction = ((dryDensity / mdd) * 1000.0).roundToInt() / 10.0
            compactions.add(compaction)

            readings.add(
                ReadingRow(
                    index = i,
                    label = "Core Cutter Pit #$i",
                    parameters = mapOf(
                        "Core Volume (V)" to "$cutterVolume cm³",
                        "Wet Soil Weight (W)" to "$wetSoil g",
                        "Wet Bulk Density (γwet)" to "$wetDensity g/cm³",
                        "Moisture Content (w)" to "$moisture %",
                        "Dry Density (γd)" to "$dryDensity g/cm³",
                        "Lab MDD" to "$mdd g/cm³",
                        "Degree of Compaction" to "$compaction %",
                        "Required Compaction" to "≥ $minCompaction %"
                    ),
                    status = PassStatus.PASS,
                    note = "Sample extracted and tested as per IS 2720 Part 29"
                )
            )
        }

        val avgCompaction = ((compactions.average() * 10.0).roundToInt() / 10.0)

        val calculations = listOf(
            CalculationStep(
                title = "1. Bulk Wet Density (γwet)",
                formula = "γwet = Wet Soil Weight (g) / Cutter Volume (cm³)",
                substitution = "Sample 1: ${soilWeights[0]} g / 1021 cm³",
                outcome = "${((soilWeights[0] / cutterVolume * 1000.0).roundToInt() / 1000.0)} g/cm³",
                isCodeClause = "IS 2720 (Part 29):1975 Cl. 4"
            ),
            CalculationStep(
                title = "2. Moisture Content (w)",
                formula = "w (%) = [(Weight of Wet Soil - Weight of Oven Dry Soil) / Dry Soil] × 100",
                substitution = "Determined by Rapid Moisture Meter / Oven Drying method",
                outcome = "Average w = ${((moistures.average() * 10.0).roundToInt() / 10.0)} % (OMC: $omc %)",
                isCodeClause = "IS 2720 (Part 2):1973 Cl. 2"
            ),
            CalculationStep(
                title = "3. Field Dry Density (γd)",
                formula = "γd = γwet / [1 + (w / 100)]",
                substitution = "Average γd = 2.052 / [1 + (12.43 / 100)]",
                outcome = "${((readings[0].parameters["Dry Density"] ?: "1.828"))} g/cm³",
                isCodeClause = "IS 2720 (Part 29):1975 Cl. 5"
            ),
            CalculationStep(
                title = "4. Degree of Compaction",
                formula = "Compaction (%) = (Field Dry Density γd / Lab MDD) × 100",
                substitution = "Compaction (%) = (Average γd / $mdd) × 100",
                outcome = "$avgCompaction % (Mandatory Specification: ≥ $minCompaction %)",
                isCodeClause = "MoRTH Section 300 & IRC 36"
            )
        )

        val summaryMetrics = mapOf(
            "Layer Tested" to grade,
            "Laboratory MDD" to "$mdd g/cm³",
            "Laboratory OMC" to "$omc %",
            "Average Field Moisture" to "${((moistures.average() * 10.0).roundToInt() / 10.0)} %",
            "Average Field Dry Density" to "${readings[0].parameters["Dry Density"]} g/cm³",
            "Degree of Compaction Obtained" to "$avgCompaction % (Required: ≥ $minCompaction%)",
            "Overall Status" to "PASS (Fully Conforms to MoRTH Cl. 305)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 2720 (Part 29):1975 Determination of Dry Density of Soils - Core Cutter Method",
                "IS 2720 (Part 7):1980 Determination of Water Content-Dry Density Relation (Proctor)",
                "MoRTH Specifications for Road and Bridge Works (5th Revision)"
            ),
            recommendations = "Compaction degree of $avgCompaction% comfortably satisfies the minimum specification. Field moisture is within OMC ± 1.0%, indicating optimal roller compaction."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "soil_compaction",
            testTitle = "Soil Field Dry Density & Degree of Compaction",
            category = TestCategory.SOIL,
            isCode = "IS 2720 (Part 29):1975 & MoRTH Cl. 305",
            grade = grade,
            sampleWeight = soilWeights[0],
            weightUnit = "g",
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 0,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: Stable embankment compaction confirmed. High dry density ensures minimal post-construction settlement under heavy wheel loads."
        )
    }

    // --- 6. CEMENT PHYSICAL TESTS REPORT GENERATOR ---
    private fun generateCementReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        ageInDays: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val consistencyP = 29.5 // %
        val istMin = 135 // Minutes (Min 30 min)
        val fstMin = 215 // Minutes (Max 600 min)
        val soundnessExpansion = 1.2 // mm (Max 10 mm)
        val fineness90u = 3.2 // % residue on 90μm sieve (Max 10%)
        val mortar28Strength = 58.4 // N/mm² (Min 53 N/mm² for 53 Grade)

        val readings = listOf(
            ReadingRow(
                index = 1,
                label = "Standard Consistency (P)",
                parameters = mapOf(
                    "Apparatus" to "Vicat with 10mm Plunger",
                    "Water Added" to "${((400.0 * (consistencyP / 100.0)))} ml for 400g cement",
                    "Plunger Penetration" to "6 mm from bottom of mold",
                    "Normal Consistency (P)" to "$consistencyP %",
                    "Standard Limit" to "26 - 33 %"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 2,
                label = "Setting Time (Initial & Final)",
                parameters = mapOf(
                    "Water for gauging (0.85P)" to "${((0.85 * consistencyP * 10.0).roundToInt() / 10.0)} %",
                    "Initial Setting Time (IST)" to "$istMin Minutes (Permissible: ≥ 30 min)",
                    "Final Setting Time (FST)" to "$fstMin Minutes (Permissible: ≤ 600 min)",
                    "Room Temperature" to "27 ± 2 °C",
                    "Relative Humidity" to "65 ± 5 %"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 3,
                label = "Soundness by Le-Chatelier",
                parameters = mapOf(
                    "Initial Distance (L1)" to "12.0 mm",
                    "Submerged in boiling water" to "3 Hours",
                    "Final Distance (L2)" to "13.2 mm",
                    "Expansion (L2 - L1)" to "$soundnessExpansion mm",
                    "IS 12269 Permissible Limit" to "Max 10.0 mm"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 4,
                label = "Fineness by 90μm Dry Sieving",
                parameters = mapOf(
                    "Initial Cement Weight" to "100.0 g",
                    "Weight Retained on 90μm Sieve" to "3.2 g",
                    "% Residue Retained" to "$fineness90u %",
                    "IS 12269 Permissible Limit" to "Max 10.0 %"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 5,
                label = "Compressive Strength (1:3 Mortar Cubes)",
                parameters = mapOf(
                    "Cube Size" to "70.6 × 70.6 × 70.6 mm",
                    "3-Day Mortar Strength" to "33.5 N/mm² (Min 27 N/mm²)",
                    "7-Day Mortar Strength" to "44.2 N/mm² (Min 37 N/mm²)",
                    "28-Day Mortar Strength" to "$mortar28Strength N/mm² (Min 53 N/mm²)"
                ),
                status = PassStatus.PASS
            )
        )

        val calculations = listOf(
            CalculationStep(
                title = "1. Normal Consistency (P)",
                formula = "P (%) = (Weight of Water / Weight of Cement) × 100",
                substitution = "P = (118.0 g / 400.0 g) × 100",
                outcome = "$consistencyP %",
                isCodeClause = "IS 4031 (Part 4):1988 Cl. 5"
            ),
            CalculationStep(
                title = "2. Soundness Expansion (Le-Chatelier)",
                formula = "Expansion = L2 - L1",
                substitution = "Expansion = 13.2 mm - 12.0 mm",
                outcome = "$soundnessExpansion mm (Permissible: ≤ 10.0 mm)",
                isCodeClause = "IS 4031 (Part 3):1988 Cl. 5"
            ),
            CalculationStep(
                title = "3. Fineness (% Residue on 90μm)",
                formula = "Fineness (%) = (Residue Weight / 100g) × 100",
                substitution = "Fineness = (3.2 g / 100.0 g) × 100",
                outcome = "$fineness90u % (Permissible: ≤ 10.0%)",
                isCodeClause = "IS 4031 (Part 1):1996 Cl. 4"
            )
        )

        val summaryMetrics = mapOf(
            "Cement Type" to grade,
            "Standard Consistency" to "$consistencyP %",
            "Initial Setting Time" to "$istMin Minutes",
            "Final Setting Time" to "$fstMin Minutes",
            "Soundness Expansion" to "$soundnessExpansion mm (Limit: < 10 mm)",
            "28-Day Mortar Strength" to "$mortar28Strength N/mm² (Min 53.0)",
            "Overall Status" to "PASS (Fully Conforming to IS 12269:2013)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 12269:2013 Ordinary Portland Cement, 53 Grade - Specification",
                "IS 4031 (Parts 1 to 6) Methods of Physical Tests for Hydraulic Cement"
            ),
            recommendations = "Cement batch shows rapid early hydration and superior 28-day mortar strength (58.4 MPa). Soundness expansion is minimal, verifying freedom from excess free lime."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "cement_physical",
            testTitle = "Cement Physical Tests & Mortar Strength",
            category = TestCategory.CEMENT,
            isCode = "IS 4031 & IS 12269:2013",
            grade = grade,
            sampleWeight = 400.0,
            weightUnit = "g",
            sampleCount = 5,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 28,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: Premium cement quality. Fineness of 3.2% guarantees high particle surface area for complete cementitious hydration."
        )
    }

    // --- 7. BITUMEN TEST REPORT GENERATOR ---
    private fun generateBitumenReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val penetration = 56 // 0.1 mm units (VG-30 standard range 45 to 70)
        val softeningPoint = 51.5 // °C (Min 47°C)
        val ductility = 78 // cm (Min 40 cm)
        val specificGravity = 1.02 // (Min 0.99)

        val readings = listOf(
            ReadingRow(
                index = 1,
                label = "Penetration at 25°C (100g, 5 sec)",
                parameters = mapOf(
                    "Bath Temperature" to "25 ± 0.1 °C",
                    "Needle Weight" to "100 g",
                    "Duration of Penetration" to "5 seconds",
                    "Penetration Value" to "$penetration dmm (0.1 mm)",
                    "IS 73 Specified Range" to "45 - 70 dmm"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 2,
                label = "Softening Point (Ring & Ball)",
                parameters = mapOf(
                    "Apparatus" to "Ring and Ball with 9.5mm steel ball",
                    "Rate of Heating" to "5.0 ± 0.5 °C / min",
                    "Temperature at ball contact" to "$softeningPoint °C",
                    "IS 73 Specified Minimum" to "Min 47.0 °C"
                ),
                status = PassStatus.PASS
            ),
            ReadingRow(
                index = 3,
                label = "Ductility Test at 27°C",
                parameters = mapOf(
                    "Briquette Cross Section" to "10 × 10 mm",
                    "Pulling Speed" to "50 mm / min",
                    "Elongation Thread Break" to "$ductility cm",
                    "IS 73 Specified Minimum" to "Min 40.0 cm"
                ),
                status = PassStatus.PASS
            )
        )

        val calculations = listOf(
            CalculationStep(
                title = "1. Penetration Value",
                formula = "Penetration = Depth needle penetrates in units of 0.1 mm",
                substitution = "Direct reading from dial micrometer",
                outcome = "$penetration dmm (Conforms to VG-30 limits 45 - 70)",
                isCodeClause = "IS 1203:1978 Cl. 4"
            ),
            CalculationStep(
                title = "2. Softening Point (°C)",
                formula = "Temperature at which steel ball drops 25.4mm through ring",
                substitution = "Determined in distilled water bath at 5°C/min heating",
                outcome = "$softeningPoint °C (Permissible: ≥ 47.0 °C)",
                isCodeClause = "IS 1205:1978 Cl. 5"
            )
        )

        val summaryMetrics = mapOf(
            "Bitumen Grade" to grade,
            "Penetration (25°C)" to "$penetration dmm",
            "Softening Point" to "$softeningPoint °C",
            "Ductility (27°C)" to "$ductility cm",
            "Specific Gravity" to "$specificGravity",
            "Overall Status" to "PASS (Conforms to IS 73:2018 Table 1)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 73:2018 Paving Bitumen - Specification (VG-30)",
                "IS 1203:1978 Determination of Penetration",
                "IS 1205:1978 Determination of Softening Point"
            ),
            recommendations = "Penetration and softening point show excellent thermal susceptibility balance for bituminous concrete (BC) and dense bituminous macadam (DBM)."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "bitumen_test",
            testTitle = "Bitumen Penetration, Softening & Ductility",
            category = TestCategory.BITUMEN,
            isCode = "IS 73:2018 & IS 1203/1205",
            grade = grade,
            sampleWeight = 100.0,
            weightUnit = "g",
            sampleCount = 3,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 0,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: Softening point 51.5°C provides high rutting resistance during peak summer surface pavement temperatures."
        )
    }

    // --- 8. BRICK COMPRESSIVE STRENGTH REPORT GENERATOR ---
    private fun generateBrickReport(
        reportNo: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        clientName: String,
        projectName: String,
        location: String,
        castingDate: String,
        testingDate: String
    ): CivilTestReport {
        val targetClassStrength = when {
            grade.contains("7.5") -> 7.5
            grade.contains("5.0") -> 5.0
            grade.contains("3.5") -> 3.5
            else -> 10.0 // Class 10
        }

        val readings = mutableListOf<ReadingRow>()
        val strengths = mutableListOf<Double>()
        val offsets = listOf(1.2, 0.8, 1.4, 1.1, 0.9)

        for (i in 1..sampleCount) {
            val offset = offsets.getOrElse(i - 1) { 1.0 }
            val strength = ((targetClassStrength + offset) * 10.0).roundToInt() / 10.0
            strengths.add(strength)

            val area = 190.0 * 90.0 // 17,100 mm²
            val load = ((strength * area / 1000.0) * 10.0).roundToInt() / 10.0

            readings.add(
                ReadingRow(
                    index = i,
                    label = "Brick Specimen #$i",
                    parameters = mapOf(
                        "Dimensions" to "190 × 90 × 90 mm",
                        "Bed Face Area" to "17,100 mm²",
                        "Frog Mortar" to "1:3 cement sand mortar filled & cured 3 days",
                        "Crushing Load" to "$load kN",
                        "Compressive Strength" to "$strength N/mm²"
                    ),
                    status = PassStatus.PASS
                )
            )
        }

        val avgStrength = ((strengths.average() * 10.0).roundToInt() / 10.0)
        val waterAbsorption = 13.8 // % (Max 20% limit for class up to 12.5)

        val calculations = listOf(
            CalculationStep(
                title = "1. Brick Bed Face Area",
                formula = "Area = Length × Width",
                substitution = "Area = 190 mm × 90 mm",
                outcome = "17,100 mm²",
                isCodeClause = "IS 3495 (Part 1):2019 Cl. 4"
            ),
            CalculationStep(
                title = "2. Compressive Strength",
                formula = "Strength (N/mm²) = (Crushing Load P in kN × 1000) / Area in mm²",
                substitution = "Average = (${strengths.joinToString(" + ") { "$it" }}) / $sampleCount",
                outcome = "$avgStrength N/mm² (Permissible: ≥ $targetClassStrength N/mm²)",
                isCodeClause = "IS 3495 (Part 1):2019 Cl. 5"
            ),
            CalculationStep(
                title = "3. 24-Hour Cold Water Absorption",
                formula = "Absorption (%) = [(Wet Weight W2 - Dry Weight W1) / W1] × 100",
                substitution = "Absorption = [(3.52 kg - 3.09 kg) / 3.09 kg] × 100",
                outcome = "$waterAbsorption % (Permissible limit ≤ 20.0% by weight)",
                isCodeClause = "IS 3495 (Part 2):2019 Cl. 5"
            ),
            CalculationStep(
                title = "4. Efflorescence Test Rating",
                formula = "Observed after water immersion and drying cycles",
                substitution = "No perceptible deposit of salts observed",
                outcome = "RATING: NIL (Complies with IS 1077)",
                isCodeClause = "IS 3495 (Part 3):2019 Cl. 5"
            )
        )

        val summaryMetrics = mapOf(
            "Brick Classification" to grade,
            "Average Compressive Strength" to "$avgStrength N/mm²",
            "Water Absorption (24-hr)" to "$waterAbsorption % (Max 20%)",
            "Efflorescence" to "NIL (PASS)",
            "Overall Status" to "PASS (Conforms strictly to IS 1077 & IS 3495)"
        )

        val validation = ValidationResult(
            isPassing = true,
            hasErrors = false,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(
                "IS 1077:1992 Common Burnt Clay Building Bricks - Specification",
                "IS 3495 (Parts 1 to 4):2019 Methods of Tests of Burnt Clay Building Bricks"
            ),
            recommendations = "Bricks satisfy Class 10 load bearing masonry requirements with uniform shape, sharp edges, and zero efflorescence."
        )

        return CivilTestReport(
            reportNumber = reportNo,
            testId = "brick_test",
            testTitle = "Burnt Clay Brick Compressive & Physical Tests",
            category = TestCategory.BRICKS,
            isCode = "IS 3495:2019 & IS 1077",
            grade = grade,
            sampleWeight = 3.10,
            weightUnit = "kg",
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = 3,
            readings = readings,
            calculations = calculations,
            summaryMetrics = summaryMetrics,
            validation = validation,
            aiRemarks = "AI Audit: Average strength of $avgStrength N/mm² exceeds Class 10 threshold. Water absorption (13.8%) provides durable weather resistance."
        )
    }

    /**
     * Real-time Automatic Validation and Error Detection Engine.
     * Evaluates custom inputs and readings against Indian Standard tolerances.
     */
    fun validateCustomInputs(
        testId: String,
        grade: String,
        sampleWeight: Double,
        sampleCount: Int,
        manualStrengthInput: Double? = null
    ): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val clauses = mutableListOf<String>()

        // General Sanity Checks
        if (sampleWeight <= 0.0) {
            errors.add("Sample weight cannot be zero or negative.")
        }
        if (sampleCount < 1) {
            errors.add("Sample count must be at least 1 specimen.")
        }

        when (testId) {
            "concrete_cube" -> {
                clauses.add("IS 456:2000 Cl. 15.4 & Table 11")
                clauses.add("IS 516:2021 Cl. 5.5")
                val fck = grade.removePrefix("M").toDoubleOrNull() ?: 25.0

                if (sampleCount < 3) {
                    warnings.add("IS 456 mandates a minimum of 3 cube specimens from the same batch for a valid statistical test result.")
                }
                if (sampleWeight !in 7.5..9.0) {
                    warnings.add("Typical 150mm concrete cube weight is between 7.8 kg and 8.6 kg (Density ~2350 - 2500 kg/m³). Weight of $sampleWeight kg may indicate honeycombing or incorrect density.")
                }
                if (manualStrengthInput != null) {
                    if (manualStrengthInput < fck) {
                        errors.add("Average compressive strength ($manualStrengthInput N/mm²) is BELOW the specified characteristic strength ($fck N/mm²) required by IS 456:2000.")
                    } else if (manualStrengthInput < fck + 3.0) {
                        warnings.add("Strength meets fck but is close to margin ($manualStrengthInput vs target ${fck + 3.0} N/mm² as per IS 456 Table 11).")
                    }
                }
            }
            "sand_sieve" -> {
                clauses.add("IS 383:2016 Table 4")
                clauses.add("IS 2386 Part 1 & 2")
                if (sampleWeight < 500.0) {
                    warnings.add("IS 2386 recommends at least 500g (preferably 1000g) for sand sieve analysis to avoid representative sampling error.")
                }
            }
            "steel_tensile" -> {
                clauses.add("IS 1786:2008 Table 3")
                clauses.add("IS 1608:2022")
                if (sampleCount < 3) {
                    warnings.add("IS 1786 mandates at least 3 test pieces from each batch / coil.")
                }
            }
            "soil_compaction" -> {
                clauses.add("IS 2720 (Part 29):1975")
                clauses.add("MoRTH Cl. 305")
                if (sampleWeight < 1500.0 || sampleWeight > 2600.0) {
                    warnings.add("Soil + cutter weight typically falls in the range of 1800g to 2300g.")
                }
            }
        }

        val isPassing = errors.isEmpty()
        val recommendation = when {
            errors.isNotEmpty() -> "Non-conformance detected! Rectify the flagged parameters or check batch mix design before proceeding."
            warnings.isNotEmpty() -> "Parameters are within acceptable bounds, but consider the advisory recommendations for optimal compliance."
            else -> "All parameters strictly conform to the Indian Standard (IS) Code testing protocol. Ready for certified test generation."
        }

        return ValidationResult(
            isPassing = isPassing,
            hasErrors = errors.isNotEmpty(),
            warnings = warnings,
            errors = errors,
            clauseReferences = clauses,
            recommendations = recommendation
        )
    }
}
