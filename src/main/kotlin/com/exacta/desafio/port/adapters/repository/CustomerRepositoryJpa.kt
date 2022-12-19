package com.exacta.desafio.port.adapters.repository

import com.exacta.desafio.domain.customer.Customer
import com.exacta.desafio.domain.customer.CustomerId
import com.exacta.desafio.domain.customer.CustomerRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface CustomerRepositorySpringData : JpaRepository<Customer, CustomerId> {
    fun findByCustomerId(customerId: CustomerId): Customer?
}

@Transactional
@Repository
class CustomerRepositoryJpa(
        private val repository: CustomerRepositorySpringData
) : CustomerRepository {

    override fun save(customer: Customer) {
        repository.save(customer)
    }

    override fun findByCustomerId(customerId: CustomerId): Customer? = repository.findByCustomerId(customerId)

}