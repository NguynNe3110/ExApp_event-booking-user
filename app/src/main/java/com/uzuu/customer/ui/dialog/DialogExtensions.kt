package com.uzuu.customer.ui.dialog

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Show a confirmation dialog
 *
 * @param title Dialog title
 * @param message Dialog message
 * @param positiveText Positive button text (default "Xác nhận")
 * @param negativeText Negative button text (default "Hủy")
 * @param onPositive Callback when positive button is clicked
 * @param onNegative Callback when negative button is clicked (optional)
 */
fun Fragment.showConfirmDialog(
    title: String,
    message: String,
    positiveText: String = "Xác nhận",
    negativeText: String = "Hủy",
    onPositive: () -> Unit,
    onNegative: (() -> Unit)? = null
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ ->
            onPositive()
        }
        .setNegativeButton(negativeText) { _, _ ->
            onNegative?.invoke()
        }
        .setCancelable(false)
        .show()
}

/**
 * Show a confirmation dialog (alternative with different button colors)
 */
fun Fragment.showWarningDialog(
    title: String,
    message: String,
    positiveText: String = "Xóa",
    negativeText: String = "Hủy",
    onPositive: () -> Unit
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ ->
            onPositive()
        }
        .setNegativeButton(negativeText) { _, _ ->
            // Do nothing
        }
        .setCancelable(false)
        .show()
}
