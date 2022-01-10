package com.example.acmeroute.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acmeroute.R
import com.example.acmeroute.databinding.RouteFragmentBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class RouteFragment : Fragment() {

  private var _binding: RouteFragmentBinding? = null
  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  private val viewModel by activityViewModels<RouteViewModel>()
  private val groupAdapter = GroupAdapter<GroupieViewHolder>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = RouteFragmentBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.destinationList.apply {
      layoutManager = LinearLayoutManager(context)
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      adapter = groupAdapter
    }

    viewModel.fetchDriverData(RouteViewModel.SortOptions.LEAST_COMMON_FACTORS)

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.driverList.collect {
          val drivers = ArrayList<DriverItem>(it.size)
          var totalSSScore = 0.0
          it.map { driver -> totalSSScore += (driver.totalSS ?: 0.0) }
          drivers.add(DriverItem("Total Sustainability Score:", totalSSScore))
          it.forEach { driver ->
            Log.d(
              this::class.simpleName,
              "Driver=${driver.fullName} Dest=${driver.dest} MaxEvenSS=${driver.evenSS} MaxOddSS=${driver.oddSS}" +
                  " delta=${driver.evenOddDelta} NameFactors=${driver.nameFactors} total=${driver.totalSS}"
            )
            drivers.add(DriverItem(driver.fullName, driver.totalSS, driver.dest))
          }
          groupAdapter.updateAsync(drivers)
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_options, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here.
    return when (item.itemId) {
      R.id.action_by_delta -> {
        viewModel.fetchDriverData(RouteViewModel.SortOptions.EVEN_ODD_DELTA_DESC)
        true
      }
      R.id.action_by_least_common_factors -> {
        viewModel.fetchDriverData(RouteViewModel.SortOptions.LEAST_COMMON_FACTORS)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    fun newInstance() = RouteFragment()
  }
}