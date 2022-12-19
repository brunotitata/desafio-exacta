package com.exacta.desafio.domain.customer

import java.util.*

interface CustomerRepository {
    fun nextIdentity(): CustomerId = UUID.randomUUID()
    fun save(customer: Customer)
    fun findByCustomerId(customerId: CustomerId): Customer?
}