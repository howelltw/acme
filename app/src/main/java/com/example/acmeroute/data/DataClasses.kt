package com.example.acmeroute.data

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class ShipmentData(val shipments: List<String>?, val drivers: List<String>?)

data class DriverDestinationWrapper(val fullName: String, var dest: String? = null, var totalSS: Double? = null) {
  val evenSS: Double = fullName.lowercase().count { it.isLetter() && "'a', 'e', 'i', 'o', 'u'".contains(it) } * 1.5
  val oddSS: Double = fullName.lowercase().count { it.isLetter() && !"'a', 'e', 'i', 'o', 'u'".contains(it) }.toDouble()
  val evenOddDelta: Double = abs(evenSS - oddSS)
  // Exclude integer 1 as a common factor for this algorithm
  val nameFactors: List<Int> = (2..this.fullName.length).filter { this.fullName.length % it == 0 }
}

data class Destinations(val streetName: String, var fullAddress: String? = null) {
  val isEven: Boolean = (streetName.count() % 2) == 0
  // Exclude integer 1 as a common factor for this algorithm
  val streetNameFactors: List<Int> = (2..this.streetName.length).filter { this.streetName.length % it == 0 }
}
