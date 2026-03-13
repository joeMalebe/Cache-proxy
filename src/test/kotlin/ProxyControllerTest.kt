package com.cache.proxy

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProxyControllerTest {

    lateinit var proxy:ProxyController

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
            val response = proxy.request("/hello").httpResponse.bodyAsText()

            assertEquals("""{hello:world}""",  response )
        }
    }

    @Test
    fun `when controller has port and host then set port and host`() {
        val host = "https://testing.com"

        val controller = ProxyController(httpClient = client, host = host)
        runBlocking {
            val response = controller.request(endpoint = "/hello").httpResponse.bodyAsText()
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
            proxy.request()
            val secondCallResponse = proxy.request()

            assertEquals(HIT, secondCallResponse.cacheValue)
        }
    }

    @Test
    fun `when service call has server error then return error message`() {
        val controller = failureProxyController()

        runBlocking {
            val response = controller.request()
            assertEquals(HttpStatusCode.InternalServerError,response.httpResponse.status)
        }
    }

    @Test
    fun `when service call has error, subsequent call should not come from cache`() {
        val controller = failureProxyController()

        runBlocking {
            controller.request()
            val secondCallResponse = controller.request()
            assertEquals(HttpStatusCode.InternalServerError,secondCallResponse.httpResponse.status)
            assertEquals(MISS,secondCallResponse.cacheValue)
        }
    }

    private fun failureProxyController(): ProxyController {
        val host = "http://failure.com"

        val controller = ProxyController(httpClient = client, host = host)
        return controller
    }


}

private val mockEngine = MockEngine { request ->
    when{
        request.url.encodedPath == "/hello" -> {

    respond(
        content = ByteReadChannel("""{hello:world}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
        }

        request.url.host == "testing.com" -> {
            respond(
                content = ByteReadChannel("""{test:working}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        request.url.host == "failure.com" -> {
            respondError(HttpStatusCode.InternalServerError)
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