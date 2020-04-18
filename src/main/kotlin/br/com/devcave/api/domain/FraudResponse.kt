package br.com.devcave.api.domain

data class FraudResponse(
    val document: String,
    val fraud: Boolean,
    val reason: String?
)
