package com.example.acmeroute.ui.main

import android.app.Application
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalSerializationApi
internal class RouteViewModelTest {

  val mockApplication: Application = mockk(relaxed = true)
  lateinit var sut: RouteViewModel

  @BeforeEach
  fun setUp() {
    sut = RouteViewModel(mockApplication)
  }

  @AfterEach
  fun tearDown() {
    clearMocks(mockApplication)
  }

  @Test
  fun verifyStreetAddressParser() {
    listOf<String>(
      "63187 Volkman Garden Suite 447",
      "75855 Dessie Lights",
      "1797 Adolf Island Apt. 744",
    ).forEachIndexed { i, address ->
      val result = sut.parseStreetName(address)
      when(i) {
        0 -> assert(result == "Volkman Garden")
        1 -> assert(result == "Dessie Lights")
        2 -> assert(result == "Adolf Island")
      }
    }
  }
}