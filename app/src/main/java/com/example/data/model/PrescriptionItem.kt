package com.example.data.model

data class PrescriptionItem(
    val medicineName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: String = "",
    val instructions: String = ""
)

object PrescriptionParser {
    fun serialize(items: List<PrescriptionItem>): String {
        return items.filter { it.medicineName.isNotBlank() }.joinToString(";;;") { item ->
            "${escape(item.medicineName)}:::${escape(item.dosage)}:::${escape(item.frequency)}:::${escape(item.duration)}:::${escape(item.instructions)}"
        }
    }

    fun deserialize(raw: String): List<PrescriptionItem> {
        if (raw.isBlank()) return emptyList()
        return raw.split(";;;").filter { it.isNotBlank() }.map { part ->
            val fields = part.split(":::")
            PrescriptionItem(
                medicineName = fields.getOrNull(0) ?: "",
                dosage = fields.getOrNull(1) ?: "",
                frequency = fields.getOrNull(2) ?: "",
                duration = fields.getOrNull(3) ?: "",
                instructions = fields.getOrNull(4) ?: ""
            )
        }
    }

    private fun escape(s: String) = s.replace(":::", " ").replace(";;;", " ")
}

