package com.example.acmeroute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.acmeroute.databinding.MainActivityBinding
import com.example.acmeroute.ui.main.RouteFragment
import com.example.acmeroute.ui.main.RouteViewModel

class MainActivity : AppCompatActivity() {

  private lateinit var binding: MainActivityBinding
  private val viewModel by viewModels<RouteViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = MainActivityBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(binding.container.id, RouteFragment.newInstance())
        .commitNow()
    }
  }
}