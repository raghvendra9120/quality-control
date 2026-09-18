package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.CivilTestEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Civil Test Pro", appName)
  }

  @Test
  fun `civil test engine generates 100 percent passing concrete report`() {
    val report = CivilTestEngine.generatePassingReport(
      testId = "concrete_cube",
      grade = "M25",
      sampleWeight = 8.25,
      sampleCount = 3,
      ageInDays = 28
    )

    assertNotNull(report)
    assertTrue("Report must pass IS 456 / IS 516", report.validation.isPassing)
    assertEquals(3, report.readings.size)
    assertTrue(report.calculations.isNotEmpty())
  }

  @Test
  fun `civil test engine input validation detects abnormal weights`() {
    val validation = CivilTestEngine.validateCustomInputs(
      testId = "concrete_cube",
      grade = "M25",
      sampleWeight = 3.0, // Way too light for 150mm concrete cube
      sampleCount = 3
    )

    assertTrue("Abnormal weight should trigger warning or error", validation.hasErrors || validation.warnings.isNotEmpty())
  }
}
