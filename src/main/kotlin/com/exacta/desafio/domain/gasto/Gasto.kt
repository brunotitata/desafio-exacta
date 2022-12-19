package com.exacta.desafio.domain.gasto

import com.exacta.desafio.domain.customer.Customer
import org.hibernate.annotations.Columns
import org.hibernate.annotations.Type
import org.hibernate.validator.constraints.Currency
import org.valiktor.functions.hasDecimalDigits
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isPositive
import org.valiktor.validate
import java.time.OffsetDateTime
import java.util.*
import javax.money.MonetaryAmount
import javax.persistence.*

typealias GastoId = UUID

@Entity
@Table(name = "gasto")
data class Gasto(
        @Id
        @Column(name = "gasto_id", columnDefinition = "uuid")
        val gastoId: GastoId,
        @Column(name = "description", length = 255)
        val description: String,
        @Column(name = "date_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
        val dateTime: OffsetDateTime,
        @Columns(columns = [
            Column(name = "currency"),
            Column(name = "amount")
        ])
        @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
        @Currency(value = ["BRL"])
        val amount: MonetaryAmount,
        @Column(name = "tags")
        val tags: String,
        @ManyToOne
        @JoinColumn(name = "customer_id", nullable = false)
        val customer: Customer
) {

    init {
        validate(this) {
            validate(Gasto::gastoId).isNotNull()
            validate(Gasto::dateTime).isNotNull()
            validate(Gasto::amount).isPositive().hasDecimalDigits(max = 2)
            validate(Gasto::customer).isNotNull()
            validate(Gasto::description).isNotBlank()
        }
    }

    constructor(
            description: String,
            amount: MonetaryAmount,
            tags: String,
            customer: Customer
    ) : this(
            gastoId = UUID.randomUUID(),
            description = description,
            amount = amount,
            tags = tags,
            customer = customer,
            dateTime = OffsetDateTime.now()
    )

    override fun hashCode(): Int {
        return gastoId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gasto

        if (gastoId != other.gastoId) return false

        return true
    }
}
