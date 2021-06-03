package com.example.tracking_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tracking_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar



fun showMaterialDialogWithSingleButton(
    context: Context,
    title: String,
    message: String,
    positiveButton: String,
    positiveFunction: () -> Unit
) {

    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(positiveButton) { dialog, _ ->
            positiveFunction()
        }
        .show()
}

fun showMaterialDialogWithScrollingItems(
    context: Context,
    title: String,
    confirmingButton: String,
    dismissiveButton: String,
    singleItems: Array<String>,
    confirmingFunction: (item: String) -> Unit
) {

    var chosenItem = singleItems.first()

    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setSingleChoiceItems(singleItems, 0) { _, item ->
            chosenItem = singleItems[item]
        }
        .setPositiveButton(confirmingButton) { _, _ ->
            confirmingFunction(chosenItem)
        }
        .setNeutralButton(dismissiveButton) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

// todo: optimise this code
fun showErrorSnackbar(view: View, context: Context, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.red))
    snackbar.show()
}

fun showWarningSnackbar(view: View, context: Context, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.orange))
    snackbar.show()
}

fun showPrimarySnackbar(view: View, context: Context, message: String) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.green))
    snackbar.show()
}

fun isNetworkAvailable(context: Context): Boolean {

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            val cap = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            val networks = connectivityManager.allNetworks
            for (n in networks) {
                val nInfo = connectivityManager.getNetworkInfo(n)
                if (nInfo != null && nInfo.isConnected) return true
            }
        }
        else -> {
            val networks = connectivityManager.allNetworkInfo
            for (nInfo in networks) {
                if (nInfo != null && nInfo.isConnected) return true
            }
        }
    }
    return false
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

