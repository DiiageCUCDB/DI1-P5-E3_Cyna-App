package com.cyna.app.mock.factories

import kotlinx.serialization.Serializable
import kotlin.random.Random

// ---------------------------------------------------------------------------
// Tiny faker helpers
// ---------------------------------------------------------------------------

private val firstNames  = listOf("Alice", "Bob", "Carlos", "Diana", "Emma", "François", "Grace", "Hugo")
private val lastNames   = listOf("Martin", "Dupont", "Smith", "Garcia", "Müller", "Rossi", "Tanaka")
private val companies   = listOf("Cyna", "Shield", "Guard", "Sentinel", "Apex", "Nexus", "Vortex")
private val productSfx  = listOf("EDR Pro", "XDR Suite", "SOC Manager", "Zero Trust Gateway", "SIEM Core", "MDM Shield")
private val catNames    = listOf("SOC", "EDR", "XDR", "SIEM", "Zero Trust", "MDM")
private val planNames   = listOf("Mensuel", "Annuel", "Starter", "Pro", "Enterprise")
private val loremWords  = listOf("security", "cloud", "advanced", "platform", "enterprise", "solution",
    "protection", "monitoring", "detection", "response", "threat", "intelligence")

private fun uuid()                  = java.util.UUID.randomUUID().toString()
private fun randomInt()             = Random.nextInt(1, 9999)
private fun randomOf(list: List<String>) = list[Random.nextInt(list.size)]
private fun lorem(words: Int = 8)   = (1..words).map { randomOf(loremWords) }.joinToString(" ")
private fun randomPrice(min: Double = 49.0, max: Double = 999.0) =
    (min + Random.nextDouble() * (max - min)).roundTo(2)
private fun Double.roundTo(decimals: Int): Double {
    val factor = Math.pow(10.0, decimals.toDouble())
    return Math.round(this * factor) / factor
}
private fun isoDate(daysAgo: Int = Random.nextInt(365)): String {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(cal.time)
}
private fun futureDateIso(daysAhead: Int = Random.nextInt(30) + 30): String {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, daysAhead)
    return java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(cal.time)
}

// ---------------------------------------------------------------------------
// Mock DTOs — shape identique aux DTOs v1
// ---------------------------------------------------------------------------

// UserProfileDto
@Serializable
data class MockUser(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isEmailVerified: Boolean,
    val createdAt: String,
)

// CategoryDto
@Serializable
data class MockCategory(
    val id: Int,
    val slug: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val displayOrder: Int,
)

// ProductDto (catalog)
@Serializable
data class MockProduct(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,       // "Active" | "Inactive" | "Archived"
    val imageUrl: String?,
    val price: Double,
)

// OrderItemDto
@Serializable
data class MockOrderItem(
    val id: Int,
    val productNameSnapshot: String,
    val planNameSnapshot: String,
    val quantityUsers: Int,
    val quantityDevices: Int,
)

// OrderSummaryDto
@Serializable
data class MockOrder(
    val id: Int,
    val status: String,        // "Pending" | "Paid" | "Failed" | "Refunded"
    val totalAmount: Double,
    val createdAt: String,
    val invoiceUrl: String?,
    val items: List<MockOrderItem>,
)

// SubscriptionDto
@Serializable
data class MockSubscription(
    val id: Int,
    val status: String,             // "Active" | "Cancelled" | "Expired"
    val productName: String,
    val planName: String,
    val currentPeriodStart: String,
    val currentPeriodEnd: String,
    val autoRenew: Boolean,
)

// CatalogPageDto
@Serializable
data class PaginatedProducts(
    val items: List<MockProduct>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
)

// Auth response (interne)
@Serializable
data class MockAuthResponse(
    val token: String,
    val user: MockUser,
)

// ---------------------------------------------------------------------------
// Factory functions — miroir de factories.js
// ---------------------------------------------------------------------------

object MockFactories {

    // UserProfileDto
    fun makeUser(
        email: String    = "${randomOf(firstNames).lowercase()}.${randomOf(lastNames).lowercase()}@example.com",
        firstName: String = randomOf(firstNames),
        lastName: String  = randomOf(lastNames),
        role: String     = "user",
        isEmailVerified: Boolean = true,
    ) = MockUser(
        id              = randomInt(),
        email           = email,
        firstName       = firstName,
        lastName        = lastName,
        role            = role,
        isEmailVerified = isEmailVerified,
        createdAt       = isoDate(),
    )

    /** Utilisateur de démo fixe — miroir de auth.js GET /user/profile */
    fun makeDemoUser() = makeUser(
        email     = "jean.dupont@entreprise.com",
        firstName = "Jean",
        lastName  = "Dupont",
        role      = "user",
    )

    // CategoryDto
    fun makeCategory(): MockCategory {
        val name = randomOf(catNames)
        return MockCategory(
            id           = randomInt(),
            slug         = name.lowercase().replace(" ", "-"),
            name         = name,
            description  = lorem(10),
            imageUrl     = "https://picsum.photos/seed/${(1..9999).random()}/800/400",
            displayOrder = Random.nextInt(10),
        )
    }

    // ProductDto — status PascalCase comme l'enum .NET
    fun makeProduct(): MockProduct = MockProduct(
        id          = randomInt(),
        name        = "${randomOf(companies)} ${randomOf(productSfx)}",
        description = lorem(20),
        status      = listOf("Active", "Active", "Active", "Inactive", "Archived").random(),
        imageUrl    = "https://picsum.photos/seed/${(1..9999).random()}/800/600",
        price       = randomPrice(49.0, 999.0),
    )

    // OrderItemDto
    fun makeOrderItem() = MockOrderItem(
        id                  = randomInt(),
        productNameSnapshot = "${randomOf(companies)} ${randomOf(productSfx)}",
        planNameSnapshot    = randomOf(planNames),
        quantityUsers       = Random.nextInt(1, 50),
        quantityDevices     = Random.nextInt(0, 200),
    )

    // OrderSummaryDto — status PascalCase
    private val orderStatuses = listOf("Pending", "Paid", "Failed", "Refunded")

    fun makeOrder(status: String = randomOf(orderStatuses)) = MockOrder(
        id          = randomInt(),
        status      = status,
        totalAmount = randomPrice(49.0, 2400.0),
        createdAt   = isoDate(Random.nextInt(730)),
        invoiceUrl  = if (Random.nextDouble() < 0.7) "#" else null,
        items       = makeMany(Random.nextInt(1, 4)) { makeOrderItem() },
    )

    // SubscriptionDto — status PascalCase
    fun makeSubscription(
        status: String = "Active",
    ) = MockSubscription(
        id                 = randomInt(),
        status             = status,
        productName        = "${randomOf(companies)} ${randomOf(productSfx)}",
        planName           = randomOf(planNames),
        currentPeriodStart = isoDate(30),
        currentPeriodEnd   = futureDateIso(),
        autoRenew          = Random.nextBoolean(),
    )

    fun makeAuthResponse(user: MockUser = makeUser()) = MockAuthResponse(
        token = "eyJ.${List(64) { ('a'..'z').random() }.joinToString("")}.mock",
        user  = user,
    )

    fun <T> makeMany(n: Int, factory: () -> T): List<T> = (1..n).map { factory() }
}