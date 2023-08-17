package com.example.challapp.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object CopyClipboardUtils {
    fun copyTextToClipboard(copyText:String,context: Context) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Copied Text", copyText)
            clipboardManager.setPrimaryClip(clipData)
    }
}