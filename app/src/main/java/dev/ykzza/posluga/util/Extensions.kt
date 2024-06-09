package dev.ykzza.posluga.util

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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

fun convertTimestampToFormattedDateTime(timestamp: Long): String {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return dateTime.format(formatter)
}

fun getStringArrayEnglish(context: Context, arrayResId: Int): Array<String> {
    val locale = Locale.ENGLISH
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources.getStringArray(arrayResId)
}

fun isEnglish(text: String): Boolean {
    val englishRegex = Regex("[a-zA-Z]")

    return when {
        englishRegex.containsMatchIn(text) -> true
        else -> false
    }
}