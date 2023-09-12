package com.challengerdaily.challenge.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object CopyClipboardUtils {
    fun copyTextToClipboard(copyText: CharSequence, context: Context) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Copied Text", copyText)
            clipboardManager.setPrimaryClip(clipData)
    }
}