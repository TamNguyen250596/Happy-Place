package com.example.happyplaces.module.common.views

import android.app.Dialog
import android.content.Context
import com.example.happyplaces.R

object CustomDialog {

    // MARK: - Properties
    private var dialog: Dialog? = null

    fun showSpinner(context: Context) {
        dialog?.dismiss()
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_spinner)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun hideSpinner() {
        dialog?.dismiss()
    }
}