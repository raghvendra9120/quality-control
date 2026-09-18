package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CalculationStep
import com.example.data.model.CivilTestReport
import com.example.data.model.PassStatus
import com.example.data.model.ReadingRow
import com.example.data.model.TestCategory
import com.example.data.model.ValidationResult
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "civil_reports")
data class CivilReportEntity(
    @PrimaryKey
    val reportNumber: String,
    val testId: String,
    val testTitle: String,
    val categoryName: String,
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
    val readingsJson: String,
    val calculationsJson: String,
    val summaryMetricsJson: String,
    val isPassing: Boolean,
    val recommendations: String,
    val aiRemarks: String?,
    val createdTimestamp: Long
) {
    fun toDomain(): CivilTestReport {
        val category = try {
            TestCategory.valueOf(categoryName)
        } catch (e: Exception) {
            TestCategory.CONCRETE
        }

        val readingsList = mutableListOf<ReadingRow>()
        try {
            val array = JSONArray(readingsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val paramsObj = obj.getJSONObject("params")
                val paramsMap = mutableMapOf<String, String>()
                val keys = paramsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    paramsMap[k] = paramsObj.getString(k)
                }
                readingsList.add(
                    ReadingRow(
                        index = obj.getInt("index"),
                        label = obj.getString("label"),
                        parameters = paramsMap,
                        status = PassStatus.valueOf(obj.optString("status", "PASS")),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback empty
        }

        val calcList = mutableListOf<CalculationStep>()
        try {
            val array = JSONArray(calculationsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                calcList.add(
                    CalculationStep(
                        title = obj.getString("title"),
                        formula = obj.getString("formula"),
                        substitution = obj.getString("substitution"),
                        outcome = obj.getString("outcome"),
                        isCodeClause = obj.getString("isCodeClause")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback empty
        }

        val metricsMap = mutableMapOf<String, String>()
        try {
            val obj = JSONObject(summaryMetricsJson)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                metricsMap[k] = obj.getString(k)
            }
        } catch (e: Exception) {
            // fallback empty
        }

        val validation = ValidationResult(
            isPassing = isPassing,
            hasErrors = !isPassing,
            warnings = emptyList(),
            errors = emptyList(),
            clauseReferences = listOf(isCode),
            recommendations = recommendations
        )

        return CivilTestReport(
            reportNumber = reportNumber,
            testId = testId,
            testTitle = testTitle,
            category = category,
            isCode = isCode,
            grade = grade,
            sampleWeight = sampleWeight,
            weightUnit = weightUnit,
            sampleCount = sampleCount,
            clientName = clientName,
            projectName = projectName,
            location = location,
            castingDate = castingDate,
            testingDate = testingDate,
            ageInDays = ageInDays,
            readings = readingsList,
            calculations = calcList,
            summaryMetrics = metricsMap,
            validation = validation,
            aiRemarks = aiRemarks,
            createdTimestamp = createdTimestamp
        )
    }

    companion object {
        fun fromDomain(report: CivilTestReport): CivilReportEntity {
            val readingsArray = JSONArray()
            for (r in report.readings) {
                val obj = JSONObject().apply {
                    put("index", r.index)
                    put("label", r.label)
                    put("status", r.status.name)
                    put("note", r.note)
                    val pObj = JSONObject()
                    for ((k, v) in r.parameters) {
                        pObj.put(k, v)
                    }
                    put("params", pObj)
                }
                readingsArray.put(obj)
            }

            val calcArray = JSONArray()
            for (c in report.calculations) {
                val obj = JSONObject().apply {
                    put("title", c.title)
                    put("formula", c.formula)
                    put("substitution", c.substitution)
                    put("outcome", c.outcome)
                    put("isCodeClause", c.isCodeClause)
                }
                calcArray.put(obj)
            }

            val metricsObj = JSONObject()
            for ((k, v) in report.summaryMetrics) {
                metricsObj.put(k, v)
            }

            return CivilReportEntity(
                reportNumber = report.reportNumber,
                testId = report.testId,
                testTitle = report.testTitle,
                categoryName = report.category.name,
                isCode = report.isCode,
                grade = report.grade,
                sampleWeight = report.sampleWeight,
                weightUnit = report.weightUnit,
                sampleCount = report.sampleCount,
                clientName = report.clientName,
                projectName = report.projectName,
                location = report.location,
                castingDate = report.castingDate,
                testingDate = report.testingDate,
                ageInDays = report.ageInDays,
                readingsJson = readingsArray.toString(),
                calculationsJson = calcArray.toString(),
                summaryMetricsJson = metricsObj.toString(),
                isPassing = report.validation.isPassing,
                recommendations = report.validation.recommendations,
                aiRemarks = report.aiRemarks,
                createdTimestamp = report.createdTimestamp
            )
        }
    }
}
