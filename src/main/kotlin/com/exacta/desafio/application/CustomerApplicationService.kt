package com.exacta.desafio.application

import com.exacta.desafio.domain.customer.Customer
import com.exacta.desafio.domain.customer.CustomerId
import com.exacta.desafio.domain.customer.CustomerRepository
import org.springframework.stereotype.Service

data class NewCustomerCommand(
        val name: String
)

@Service
class CustomerApplicationService(
        private val customerRepository: CustomerRepository
) {

    suspend fun newCustomer(command: NewCustomerCommand): CustomerId {
        val customer = Customer(
                customerId = customerRepository.nextIdentity(),
                name = command.name
        )
        customerRepository.save(customer)

        //TODO aqui podemos adicionar um evento de dominio por ex: NewCustomerCreatedEvent

        return customer.customerId
    }

}