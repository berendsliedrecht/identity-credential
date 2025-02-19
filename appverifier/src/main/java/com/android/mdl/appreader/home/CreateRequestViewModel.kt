package com.android.mdl.appreader.home

import androidx.lifecycle.ViewModel
import com.android.identity.documenttype.MdocDataElement
import com.android.mdl.appreader.VerifierApp
import com.android.mdl.appreader.document.RequestDocument
import com.android.mdl.appreader.document.RequestDocumentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CreateRequestViewModel : ViewModel() {


    private val mutableState = MutableStateFlow(RequestingDocumentState())
    val state: StateFlow<RequestingDocumentState> = mutableState

    fun onRequestUpdate(fieldsRequest: DocumentElementsRequest) {
        val updated = fieldsRequest.copy(isSelected = !fieldsRequest.isSelected)
        when (updated.title) {
            state.value.olderThan18.title -> mutableState.update { it.copy(olderThan18 = updated) }

            state.value.utrechtInteropEventPid.title -> mutableState.update {
                it.copy(
                    utrechtInteropEventPid = updated
                )
            }

            state.value.utrechtInteropEventMdl.title -> mutableState.update {
                it.copy(
                    utrechtInteropEventMdl = updated
                )
            }
        }
    }

    fun calculateRequestDocumentList(intentToRetain: Boolean): RequestDocumentList {
        val requestDocumentList = RequestDocumentList()
        val uiState = state.value

        if (uiState.olderThan18.isSelected) {
            requestDocumentList.addRequestDocument(
                getRequestDocument(
                    RequestDocument.MDL_DOCTYPE,
                    intentToRetain,
                    filterNamespace = { ns -> ns == RequestDocument.MDL_NAMESPACE },
                    filterElement = { el ->
                        listOf(
                            "portrait",
                            "age_over_18"
                        ).contains(el.attribute.identifier)
                    }
                )
            )
        }

        if (uiState.utrechtInteropEventMdl.isSelected) {
            requestDocumentList.addRequestDocument(
                getRequestDocument(
                    RequestDocument.MDL_DOCTYPE,
                    intentToRetain,
                    filterElement = { el ->
                        listOf(
                            "family_name",
                            "given_name",
                            "birth_date",
                            "issue_date",
                            "expiry_date",
                            "issuing_country",
                            "issuing_authority",
                            "document_number",
                            "portrait",
                            "un_distinguishing_sign",

                            "driving_privileges",

                            "signature_usual_mark",
                            "age_over_18"
                        ).contains(el.attribute.identifier)
                    }
                )
            )
        }

        if (uiState.utrechtInteropEventPid.isSelected) {
            requestDocumentList.addRequestDocument(
                getRequestDocument(
                    RequestDocument.EU_PID_DOCTYPE,
                    intentToRetain,
                    filterElement = { el ->
                        listOf(
                            "family_name",
                            "given_name",
                            "birth_date",
                            "birth_place",
                            "nationality",
                            "portrait",
                            "expiry_date",
                            "issuing_authority",
                            "issuing_country",
                            "issuance_date",
                            "age_over_18",
                            "age_in_years",
                            "age_birth_year",

                            // Included in sample data but not in bdr issuer
                            // "family_name_birth",
                            // "given_name_birth",
                            // "mobile_phone_number",
                            // "sex",
                            // "document_number"
                            // "email_address"
                            // "personal_administrative_number",
                            // "resident_address",
                            // "resident_country",
                            // "resident_state",
                            // "resident_city",
                            // "resident_postal_code",
                            // "resident_street",
                            // "resident_house_number",
                        ).contains(el.attribute.identifier)
                    }
                )
            )
        }

        return requestDocumentList
    }


    private fun getRequestDocument(
        docType: String,
        intentToRetain: Boolean,
        filterNamespace: (String) -> Boolean = { _ -> true },
        filterElement: (MdocDataElement) -> Boolean = { _ -> true }
    ): RequestDocument {
        val mdocDocumentType = VerifierApp.documentTypeRepositoryInstance
            .getDocumentTypeForMdoc(docType)!!.mdocDocumentType!!
        return RequestDocument(
            docType,
            mdocDocumentType.namespaces.values.filter { filterNamespace(it.namespace) }.associate {
                Pair(
                    it.namespace,
                    it.dataElements.values.filter { el -> filterElement(el) }
                        .associate { el -> Pair(el.attribute.identifier, intentToRetain) }
                )
            }
        )
    }
}