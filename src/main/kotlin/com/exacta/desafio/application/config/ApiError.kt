package com.exacta.desafio.application.config

data class Constraint(
    val name: String,
    val params: List<Any> = emptyList()
)

data class Error(
    var property: String?=null,
    var value: Any?=null,
    val message: String,
    val constraint: Constraint
)
data class ApiError(
    val errors: List<Error>
)