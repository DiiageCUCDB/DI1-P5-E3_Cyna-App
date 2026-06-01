package com.cyna.app.mock.factories

import kotlinx.serialization.Serializable
import kotlin.random.Random

// ---------------------------------------------------------------------------
// Tiny faker helpers — no external dependency needed
// ---------------------------------------------------------------------------

private val firstNames  = listOf("Alice", "Bob", "Carlos", "Diana", "Emma", "François", "Grace", "Hugo")
private val lastNames   = listOf("Martin", "Dupont", "Smith", "Garcia", "Müller", "Rossi", "Tanaka")
private val companies   = listOf("Cyna", "Shield", "Guard", "Sentinel", "Apex", "Nexus", "Vortex")
private val productSfx  = listOf("EDR Pro", "XDR Suite", "SOC Manager", "Zero Trust Gateway", "SIEM Core", "MDM Shield")
private val catNames    = listOf("SOC", "EDR", "XDR", "SIEM", "Zero Trust", "MDM")
private val loremWords  = listOf("security", "cloud", "advanced", "platform", "enterprise", "solution",
    "protection", "monitoring", "detection", "response", "threat", "intelligence")

private fun uuid() = java.util.UUID.randomUUID().toString()
private fun randomOf(list: List<String>) = list[Random.nextInt(list.size)]
private fun lorem(words: Int = 8) = (1..words).map { randomOf(loremWords) }.joinToString(" ")
private fun randomPrice(min: Double = 49.0, max: Double = 999.0) =
    (min + Random.nextDouble() * (max - min)).roundTo(2)
private fun Double.roundTo(decimals: Int): Double {
    val factor = Math.pow(10.0, decimals.toDouble())
    return Math.round(this * factor) / factor
}
private fun isoDate(daysAgo: Int = Random.nextInt(365)): String {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
    return cal.time.let {
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(it)
    }
}
private fun futureDateIso(daysAhead: Int = Random.nextInt(30) + 30): String {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, daysAhead)
    return cal.time.let {
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(it)
    }
}

// ---------------------------------------------------------------------------
// DTOs used by the mock layer (serialisable)
// ---------------------------------------------------------------------------

@Serializable
data class MockUser(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val isConfirmed: Boolean,
    val is2faEnabled: Boolean,
    val createdAt: String,
)

@Serializable
data class MockCategory(
    val id: String,
    val name: String,
    val description: String,
    val image: String,
    val displayOrder: Int,
    val createdAt: String,
)

@Serializable
data class MockProduct(
    val id: String,
    val categoryId: String,
    val imageUrl: String,
    val name: String,
    val description: String,
    val priceMonthly: Double,
    val priceYearly: Double,
    val status: String,
    val priority: Int,
    val createdAt: String,
)

@Serializable
data class MockSubscription(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val status: String,
    val duration: String,
    val quantity: Int,
    val unitPrice: Double,
    val startsAt: String,
    val endsAt: String,
    val createdAt: String,
)

@Serializable
data class MockOrder(
    val id: String,
    val userId: String,
    val status: String,
    val statusLabel: String,
    val productName: String,
    val total: Double,
    val type: String,
    val paymentLast4: String,
    val paymentMethod: String,
    val invoiceUrl: String?,
    val createdAt: String,
)

@Serializable
data class MockAuthResponse(
    val token: String,
    val user: MockUser,
)

@Serializable
data class PaginatedProducts(
    val items: List<MockProduct>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
)

// ---------------------------------------------------------------------------
// Factory functions — mirrors factories.js
// ---------------------------------------------------------------------------

object MockFactories {

    fun makeUser(
        email: String = "${randomOf(firstNames).lowercase()}.${randomOf(lastNames).lowercase()}@example.com",
        name: String = "${randomOf(firstNames)} ${randomOf(lastNames)}",
        role: String = "user",
        isConfirmed: Boolean = true,
    ) = MockUser(
        id = uuid(),
        email = email,
        name = name,
        role = role,
        isConfirmed = isConfirmed,
        is2faEnabled = false,
        createdAt = isoDate(),
    )

    /** Fixed demo user — mirrors auth.js GET /auth/me */
    fun makeDemoUser() = makeUser(
        email = "jean.dupont@entreprise.com",
        name = "Jean Dupont",
        role = "user",
        isConfirmed = true,
    )

    fun makeCategory() = MockCategory(
        id = uuid(),
        name = randomOf(catNames),
        description = lorem(10),
        image = "https://picsum.photos/seed/${(1..9999).random()}/800/400",
        displayOrder = Random.nextInt(10),
        createdAt = isoDate(),
    )

    fun makeProduct(categoryId: String = uuid()) = MockProduct(
        id          = uuid(),
        categoryId  = categoryId,
        imageUrl    = "https://picsum.photos/seed/${(1..9999).random()}/800/600",
        name        = "${randomOf(companies)} ${randomOf(productSfx)}",
        description = lorem(20),
        priceMonthly = randomPrice(49.0, 999.0),
        priceYearly  = randomPrice(490.0, 9990.0),
        status      = listOf("available", "available", "available",   // ✅ ~75% available
            "unavailable", "out_of_stock").random(),
        priority    = Random.nextInt(6),
        createdAt   = isoDate(),
    )

    fun makeSubscription(
        userId: String = uuid(),
        status: String = "active",
    ) = MockSubscription(
        id = uuid(),
        userId = userId,
        productId = uuid(),
        productName = "${randomOf(companies)} ${randomOf(productSfx)}",
        status = status,
        duration = if (Random.nextBoolean()) "monthly" else "yearly",
        quantity = Random.nextInt(1, 10),
        unitPrice = randomPrice(49.0, 999.0),
        startsAt = isoDate(30),
        endsAt = futureDateIso(),
        createdAt = isoDate(30),
    )

    private val orderStatuses = listOf("pending", "paid", "failed", "refunded", "active", "terminated")
    private val orderStatusLabels = mapOf(
        "active" to "Actif", "terminated" to "Terminée", "refunded" to "Remboursé",
        "paid" to "Payé", "pending" to "En attente", "failed" to "Échoué",
    )
    private val orderTypes = listOf("Abonnement Mensuel", "Abonnement Annuel", "Prestation Unique")
    private val paymentMethods = listOf("Carte", "Virement Bancaire", "Prélèvement SEPA")

    fun makeOrder(status: String = randomOf(orderStatuses)) = MockOrder(
        id = uuid(),
        userId = "user-1",
        status = status,
        statusLabel = orderStatusLabels[status] ?: status,
        productName = "${randomOf(companies)} ${randomOf(productSfx)}",
        total = randomPrice(49.0, 2400.0),
        type = randomOf(orderTypes),
        paymentLast4 = if (Random.nextBoolean()) Random.nextInt(1000, 9999).toString() else "",
        paymentMethod = randomOf(paymentMethods),
        invoiceUrl = if (Random.nextDouble() < 0.7) "#" else null,
        createdAt = isoDate(Random.nextInt(730)),
    )

    fun makeAuthResponse(user: MockUser = makeUser()) = MockAuthResponse(
        token = "eyJ.${List(64) { ('a'..'z').random() }.joinToString("")}.mock",
        user = user,
    )

    fun <T> makeMany(n: Int, factory: () -> T): List<T> = (1..n).map { factory() }
}