package com.exacta.desafio.domain.customer

import com.exacta.desafio.domain.gasto.Gasto
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.util.*
import javax.persistence.*

typealias CustomerId = UUID

@Entity
@Table(name = "customer")
data class Customer(
        @Id
        @Column(name = "customer_id", columnDefinition = "uuid")
        val customerId: CustomerId,
        @Column(name = "name", length = 255)
        val name: String,
        @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
        val gastos: MutableSet<Gasto>
) {

    init {
        validate(this) {
            validate(Customer::name).isNotBlank()
            validate(Customer::customerId).isNotNull()
        }
    }

    constructor(
            customerId: CustomerId,
            name: String
    ) : this(
            customerId = customerId,
            name = name,
            gastos = mutableSetOf()
    )

    fun newGasto(gasto: Gasto) {
        this.gastos.add(gasto)
        //TODO aqui podemos adicionar um evento de dominio por ex: NewGastoCreatedEvent
    }
}
