package com.cache.proxy

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.net.ServerSocket
import kotlin.test.assertEquals

class ProxyControllerTest {

    lateinit var proxy:ProxyController

    val serverSocket: ServerSocket = mock()
    val client = HttpClient(mockEngine)
    @BeforeEach
    fun setUp () {

        proxy = ProxyController(client)
    }

    @Test
    fun canCallRequestFunction() {
        runBlocking{ proxy.request() }
    }

    @Test
    fun whenCallServiceThenReturnResponse() {
        runBlocking {
            val response = proxy.request("hello").httpResponse.bodyAsText()

            assertEquals("""{hello:world}""",  response )
        }
    }

    @Test
    fun `when controller has port and host then set port and host`() {
        val host = "testing.com"
        val port = "8080"

        val controller = ProxyController(httpClient = client, host = host, port = port)
        runBlocking {
            val response = controller.request(endpoint = "hello").httpResponse.bodyAsText()
            assertEquals("""{hello:world}""", response)
        }
    }



    @Test
    fun `when request is successful return cache hit header`() {
        runBlocking {
            val response = proxy.request()

            assertEquals(MISS, response.cacheValue)
        }
    }

    @Test
    fun `when request is called twice then cache value should be hit on second call`() {
        runBlocking {
            val firstCall = proxy.request()

            val secondCall = proxy.request()

            assertEquals(HIT, secondCall.cacheValue)
        }
    }

    @Test
    fun `when start called then start proxy server`() {
        proxy.start()
        
        assertTrue { proxy.isStart }
    }

    @Test
    fun `when start then initialise server`() {
        proxy.start(serverSocket)

        proxy.server
    }

}

val mockEngine = MockEngine { request ->
    when{
        request.url.encodedPath.equals("/hello") -> {

    respond(
        content = ByteReadChannel("""{hello:world}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
        }

        request.url.host.equals("testing") -> {
            respond(
                content = ByteReadChannel("""{test:isworking}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        else ->{
            respond(
                content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
    }
}