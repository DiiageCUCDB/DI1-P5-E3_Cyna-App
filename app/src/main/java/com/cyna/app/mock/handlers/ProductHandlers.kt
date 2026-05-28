package com.cyna.app.mock.handlers

import com.cyna.app.mock.factories.MockFactories
import com.cyna.app.mock.factories.MockProduct
import com.cyna.app.mock.registry.MockHandler
import io.ktor.http.*

// ---------------------------------------------------------------------------
// In-memory store — persists for the lifetime of the app session
// ---------------------------------------------------------------------------

private val _products: MutableList<MockProduct> =
    MockFactories.makeMany(12) { MockFactories.makeProduct() }.toMutableList()

// ---------------------------------------------------------------------------
// Product handlers — mirrors handlers/products.js
// ---------------------------------------------------------------------------

val productHandlers: List<MockHandler> = listOf(

    // GET /products
    MockHandler(
        method = HttpMethod.Get,
        path = "/products",
        resolver = { _, _ -> _products.toList() }
    ),

    // GET /products/:id
    MockHandler(
        method = HttpMethod.Get,
        path = "/products/:id",
        resolver = { params, _ ->
            _products.find { it.id == params["id"] }
                ?: error("Product not found")
        }
    ),

    // GET /products/similar/:id — 6 random products
    MockHandler(
        method = HttpMethod.Get,
        path = "/products/similar/:id",
        resolver = { params, _ ->
            _products
                .filter { it.id != params["id"] }
                .shuffled()
                .take(6)
        }
    ),

    // POST /products (admin)
    MockHandler(
        method = HttpMethod.Post,
        path = "/products",
        status = HttpStatusCode.Created,
        resolver = { _, _ ->
            val newProduct = MockFactories.makeProduct()
            _products.add(newProduct)
            newProduct
        }
    ),

    // DELETE /products/:id (admin)
    MockHandler(
        method = HttpMethod.Delete,
        path = "/products/:id",
        status = HttpStatusCode.NoContent,
        resolver = { params, _ ->
            _products.removeAll { it.id == params["id"] }
            null
        }
    ),
)