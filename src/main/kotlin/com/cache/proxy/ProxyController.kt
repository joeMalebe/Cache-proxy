package com.cache.proxy

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

const val X_CACHE: String = "X-Cache"
const val MISS: String = "MISS"
const val HIT: String = "HIT"

class ProxyController(
    val httpClient: HttpClient,
    val host: String = "http://localhost",
) {
    private val cacheMap = mutableMapOf<String, ProxyResponse>()

    suspend fun request(endpoint: String = "/yessir"): ProxyResponse {
        if (cacheMap.containsKey(endpoint)) {
            return cacheMap[endpoint]!!.copy(cacheValue = HIT)
        }
        val response = httpClient.get(urlString = "${host}$endpoint").call.response
        return ProxyResponse(
            response
        ).also {
            if (it.httpResponse.status.isSuccess()) cacheMap[endpoint] = it
        }
    }
}

data class ProxyResponse(val httpResponse: HttpResponse, val cacheValue: String = MISS)

