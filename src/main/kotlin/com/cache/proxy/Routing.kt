package com.cache.proxy

import io.ktor.client.statement.bodyAsBytes
import io.ktor.server.application.Application
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.utils.io.ByteReadChannel

fun Application.configureRouting(proxyController: ProxyController) {
    routing {

        get("/") {
            call.respondText("Hello World!")
        }
        get("{...}") {
            val response = proxyController.request(call.pipelineCall.engineCall.request.path())
            call.response.headers.append(X_CACHE, response.cacheValue)
            call.respond(
                status = response.httpResponse.status,
                ByteReadChannel(response.httpResponse.bodyAsBytes())
            )
        }
    }
}
