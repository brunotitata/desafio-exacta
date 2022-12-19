package com.exacta.desafio.domain

import org.valiktor.Constraint

object DomainExceptions {

    data class CustomerNotFoundException(val msg: String, val value: Any?, val constraint: Constraint) : RuntimeException(msg)
    data class GastoNotFoundException(val msg: String, val constraint: Constraint) : RuntimeException(msg)

}

object DomainConstraints {

    class CustomerNotFound : Constraint
    class GastoNotFound : Constraint

}