package com.example.acmeroute.ui.main

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.acmeroute.R
import com.example.acmeroute.databinding.DriverItemBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class DriverItem(val name: String, val totalSS: Double?, val destination: String? = null) : BindableItem<DriverItemBinding>() {
  override fun bind(viewBinding: DriverItemBinding, position: Int) {
    viewBinding.driverName.text = name
    if (destination.isNullOrEmpty()) {
      viewBinding.destinationLabel.isVisible = false
      viewBinding.destinationAddress.fillOrHide(null)
    } else {
      viewBinding.destinationLabel.isVisible = true
      viewBinding.destinationAddress.fillOrHide(destination)
    }
    viewBinding.ssScore.text = totalSS?.toString()
  }

  override fun isSameAs(other: Item<*>): Boolean {
    return other is DriverItem
        && this.name == other.name
        && this.destination == other.destination
  }

  override fun hasSameContentAs(other: Item<*>): Boolean {
    return other is DriverItem
        && this.totalSS == other.totalSS
  }

  override fun getLayout() = R.layout.driver_item
  override fun initializeViewBinding(view: View) = DriverItemBinding.bind(view)

  fun TextView.fillOrHide(value: CharSequence?) {
    isVisible = !value.isNullOrBlank()
    if (isVisible) text = value
  }
}