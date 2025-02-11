package com.android.mdl.appreader.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.android.mdl.appreader.R

@Stable
@Immutable
data class RequestingDocumentState(
    val olderThan18: DocumentElementsRequest = DocumentElementsRequest(R.string.mdl_over_18),
    val utrechtInteropEventMdl: DocumentElementsRequest = DocumentElementsRequest(R.string.utrecht_interop_event_mdl, true),
    val utrechtInteropEventPid: DocumentElementsRequest = DocumentElementsRequest(R.string.utrecht_interop_event_pid),
) {
    val currentRequestSelection: String
        get() = buildString {
            if (olderThan18.isSelected) {
                append("Over 18")
                append("; ")
            }
            if (utrechtInteropEventMdl.isSelected) {
                append("UIE MDL")
                append("; ")
            }
            if (utrechtInteropEventPid.isSelected) {
                append("UIE PID")
                append("; ")
            }
        }
}