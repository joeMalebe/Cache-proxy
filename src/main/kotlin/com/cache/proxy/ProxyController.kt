package com.cache.proxy


import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.buildHeaders
import io.ktor.http.headers
import java.net.ServerSocket
const val X_CACHE: String = "X-Cache"
const val MISS:String = "MISS"
const val HIT:String = "HIT"
class ProxyController(val httpClient: HttpClient,
                      val host: String = "localhost",
                      val port: String = "80"
) {

    lateinit var server: ServerSocket
    var isStart = false

    private val cacheMap = mutableMapOf<String, ProxyResponse>()

    suspend fun request(endpoint: String = "yessir"): ProxyResponse {
        if (cacheMap.containsKey(endpoint)) {
            return cacheMap[endpoint]!!.copy(cacheValue = HIT)
        }
        val client = httpClient.get(urlString = "https://$host:$port/$endpoint")

        return ProxyResponse(client.call.response.also {
            headers { append(X_CACHE, MISS) }
        }).also {
            cacheMap[endpoint] = it
        }
    }

    fun start(serverSocket: ServerSocket = ServerSocket(3000)) {
        isStart = true
        server = serverSocket
    }

}

data class ProxyResponse(val httpResponse: HttpResponse,val cacheValue: String = MISS)

