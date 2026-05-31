package com.cyna.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ErrorResponse(
    val message: String? = null,
    val error: String? = null
) {
    val text: String get() = message ?: error ?: "No details provided"
}

@Serializable
internal data class MessageResponse(val message: String)