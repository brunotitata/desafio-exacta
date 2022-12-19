package com.exacta.desafio.domain.gasto

import com.exacta.desafio.domain.customer.Customer
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.javamoney.moneta.CurrencyUnitBuilder
import org.valiktor.constraints.NotBlank
import org.valiktor.test.shouldFailValidation
import java.util.*
import javax.money.Monetary

class GastoTest : AnnotationSpec() {

    private val customerId: UUID = UUID.fromString("87a1b85b-5135-4d27-8015-9950142138d2")
    private val customer = mockk<Customer>()

    @Test
    fun newGasto() {

        val gasto = Gasto(
                description = "Bar com os amigos",
                amount = toMoney(135.00),
                tags = "[Varejo]",
                customer = customer
        )

        gasto.description.shouldBe("Bar com os amigos")
        gasto.amount.shouldBe(toMoney(135.00))
        gasto.tags.shouldBe("[Varejo]")
        gasto.customer.shouldNotBeNull()

    }

    @Test
    fun newGastoWithoutDescriptionShouldFail() {

        shouldFailValidation<Gasto> {
            Gasto(
                    description = "",
                    amount = toMoney(135.00),
                    tags = "[Varejo]",
                    customer = customer
            )
        }.verify {
            expect(Gasto::description, "", NotBlank)
        }

    }

    fun toMoney(value: Double) =
            Monetary.getDefaultAmountFactory()
                    .setNumber(value)
                    .setCurrency(CurrencyUnitBuilder.of("BRL", "real").setDefaultFractionDigits(2).build())
                    .create()

}