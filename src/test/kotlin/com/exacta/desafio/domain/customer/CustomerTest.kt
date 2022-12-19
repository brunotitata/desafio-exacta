package com.exacta.desafio.domain.customer

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation
import java.util.*

class CustomerTest : AnnotationSpec() {

    private val customerId: UUID = UUID.fromString("87a1b85b-5135-4d27-8015-9950142138d2")

    @Test
    fun newCustomer() {

        val customer = Customer(customerId = customerId, name = "Bruno Costa")

        customer.customerId.shouldBe(customerId)
        customer.name.shouldBe("Bruno Costa")

    }

    @Test
    fun newCustomerWithNameEmptyShouldFail() {

        shouldFailValidation<Customer> {
            Customer(customerId = customerId, name = "")
        }.verify {
            expect(Customer::name, "", NotBlank)
        }

    }

}