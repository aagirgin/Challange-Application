package com.challengerdaily.challenge.adapters

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@BindingAdapter("stateFlowValue", "lifecycleOwner", requireAll = false)
fun <T> bindStateFlowToEditText(
    editText: EditText,
    stateFlow: StateFlow<T>?,
    lifecycleOwner: LifecycleOwner?
) {
    lifecycleOwner ?: return
    stateFlow?.let { flow ->
        flow.onEach { value ->
            editText.setText(value.toString())
        }.launchIn(lifecycleOwner.lifecycleScope)
    }
}
