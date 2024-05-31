package dev.ykzza.posluga.util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.hideView() {
    visibility = View.INVISIBLE
}

fun View.showView() {
    visibility = View.VISIBLE
}

fun View.makeViewGone() {
    visibility = View.GONE
}

fun Fragment.showToast(toastMessage: String) {
    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
}