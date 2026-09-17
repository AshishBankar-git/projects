package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PrescriptionItem
import com.example.data.model.PrescriptionParser
import org.junit.Assert.assertEquals
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
    assertEquals("Clinic Reception Assistant", appName)
  }

  @Test
  fun `prescription parser serializes and deserializes correctly`() {
    val items = listOf(
      PrescriptionItem("Amoxicillin", "500mg", "TDS", "5 days", "Take with meals"),
      PrescriptionItem("Paracetamol", "650mg", "SOS", "3 days", "When temperature > 100")
    )
    val serialized = PrescriptionParser.serialize(items)
    val deserialized = PrescriptionParser.deserialize(serialized)

    assertEquals(2, deserialized.size)
    assertEquals("Amoxicillin", deserialized[0].medicineName)
    assertEquals("500mg", deserialized[0].dosage)
    assertEquals("Paracetamol", deserialized[1].medicineName)
  }
}
