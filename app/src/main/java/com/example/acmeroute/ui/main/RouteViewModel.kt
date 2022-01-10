package com.example.acmeroute.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.acmeroute.data.Destinations
import com.example.acmeroute.data.DriverDestinationWrapper
import com.example.acmeroute.data.ShipmentData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException

@ExperimentalSerializationApi
class RouteViewModel(application: Application) : AndroidViewModel(application) {
  private val assetManager = application.assets
  private val json = Json { ignoreUnknownKeys = true }

  private val mutableDriverList = MutableStateFlow<List<DriverDestinationWrapper>>(emptyList())
  val driverList: StateFlow<List<DriverDestinationWrapper>> = mutableDriverList

  fun fetchDriverData(sortOptions: SortOptions? = null): Job = viewModelScope.launch {
    try {
      // Load sample data from file (or replace with network call to load data)
      assetManager.open("DataFile.json").use { inputStream ->
        val shipmentData = json.decodeFromStream<ShipmentData>(inputStream)

        // Build the driver list
        val driverInfoList = ArrayList<DriverDestinationWrapper>(shipmentData.drivers?.size ?: 0)
        shipmentData.drivers?.forEach { driverName ->
          driverInfoList.add(DriverDestinationWrapper(driverName))
        }

        // Build the destinations list
        val destinations = ArrayList<Destinations>(shipmentData.shipments?.size ?: 0)
        shipmentData.shipments?.forEach {
          val dest = buildDestination(it)
          Log.d(this::class.simpleName, "Destination=${dest.fullAddress} StreetName=${dest.streetName} isEven=${dest.isEven} factors=${dest.streetNameFactors}")
          destinations.add(dest)
        }

        // Build an optimized list, with the initial List sorted by the specified option
        val finalList: List<DriverDestinationWrapper> = when(sortOptions) {
          // This gives us the ability to A/B test different optimization strategies. Currently only simple single-pass
          // options are being used.
          // TODO: Try multi-pass approach based on opportunity cost of assigning drivers

          // This strategy priorities drivers with the highest delta between assigning an even address vs. odd address
          SortOptions.EVEN_ODD_DELTA_DESC -> optimizeShipments(driverInfoList.sortedByDescending { it.evenOddDelta }, destinations)
          // This strategy priorities drivers with the lowest number of factors in their names (tries to optimize the number
          // of drivers that will get the common factor bonus)
          SortOptions.LEAST_COMMON_FACTORS -> optimizeShipments(driverInfoList.sortedBy { it.nameFactors.size }, destinations)
          else -> optimizeShipments(driverInfoList.sortedByDescending { it.evenOddDelta }, destinations)
        }
        mutableDriverList.value = finalList
      }
    } catch (e: IOException) {
      Log.e(this::class.simpleName, "Error reading input file: ${e.localizedMessage}")
    }
  }

  private fun optimizeShipments(driverInfoList: List<DriverDestinationWrapper>, destinations: MutableList<Destinations>): List<DriverDestinationWrapper> {
    driverInfoList.forEach { driver ->
      // Calculate the highest possible even SS and save the matching destination
      var evenSS = 0.0
      var evenDest = getDestWithCommonFactor(driver, destinations, true)
      if (evenDest != null) {
        evenSS = driver.evenSS * COMMON_FACTOR_BONUS
      } else {
        evenDest = destinations.firstOrNull { it.isEven }
        if (evenDest != null) evenSS = driver.evenSS
      }

      // Calculate the highest possible odd SS and save the matching destination
      var oddSS = 0.0
      var oddDest = getDestWithCommonFactor(driver, destinations, false)
      if (oddDest != null) {
        oddSS = driver.oddSS * COMMON_FACTOR_BONUS
      } else {
        oddDest = destinations.firstOrNull { !it.isEven }
        if (oddDest != null) oddSS = driver.oddSS
      }

      // Assign the destination that results in the higher score and remove that destination from the list of available destinations
      if (evenSS >= oddSS) {
        destinations.remove(evenDest)
        driver.dest = evenDest?.fullAddress
        driver.totalSS = evenSS
      } else {
        destinations.remove(oddDest)
        driver.dest = oddDest?.fullAddress
        driver.totalSS = oddSS
      }
    }
    return driverInfoList.sortedByDescending { it.totalSS }
  }

  private fun getDestWithCommonFactor(driver: DriverDestinationWrapper, destinations: MutableList<Destinations>, isEven: Boolean): Destinations? {
    return if (isEven) {
      destinations.firstOrNull { dest -> dest.isEven && dest.streetNameFactors.intersect(driver.nameFactors).isNotEmpty() }
    } else {
      destinations.firstOrNull { dest -> !dest.isEven && dest.streetNameFactors.intersect(driver.nameFactors).isNotEmpty() }
    }
  }

  private fun buildDestination(fullAddress: String): Destinations {
    return Destinations(parseStreetName(fullAddress), fullAddress)
  }

  fun parseStreetName(fullAddress: String): String {
    // First try removing the street address
    var streetName = fullAddress.trim().replace(Regex("^\\d+ "), "")

    // If the address end in a digit, assume apartment number, unit number etc.
    if (streetName.contains(Regex(" \\d+$"))) {
      // Strip the apartment number, plus Apt., Suite, Unit prefix
      // TODO: Doesn't work for units like B303
      streetName = streetName.replace(Regex(" [\\w\\.]+ \\d+$"), "")
    }
    return streetName
  }

  enum class SortOptions { EVEN_ODD_DELTA_DESC, LEAST_COMMON_FACTORS }

  companion object {
    private const val COMMON_FACTOR_BONUS = 1.5
  }
}