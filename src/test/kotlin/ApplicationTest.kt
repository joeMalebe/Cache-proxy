package com.cache.proxy

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val controller = ProxyController(HttpClient(mockEngine))

    @Test
    fun `when calling home endpoint then return Hello World!`() = testApplication {
        application {
            module(controller)
        }
        val response = client.get("/").call.response
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun `when calling random endpoint then return Hello`() = testApplication {
        application {
            module(controller)
        }
        val response = client.get("/random").call.response
        assertEquals("Hello", response.bodyAsText())
    }

    @Test
    fun `when any endpoint except home then return World`() = testApplication {
        application {
            module(controller)
        }
        val response = client.get("random/hello/world").call.response

        assertEquals("World", response.bodyAsText())
    }

    @Test
    fun `when service does not hit cache then return X-Cache header as MISS`() = testApplication {
        application {
            module(controller)
        }
        val response = client.get("random/hello/world").call.response

        assertEquals("MISS", response.headers.get(X_CACHE))
    }

    @Test
    fun `when service does hit the cache then return X-Cache header as HIT`() = testApplication {
        application {
            module(controller)
        }
        client.get("random/hello/world").call.response
        val response = client.get("random/hello/world").call.response

        assertEquals("HIT", response.headers.get(X_CACHE))
    }

    @Test
    fun `get args and change key`() {
        val args = arrayOf("--port", "800", "--host", "hello world")

        args[0] = "-port"
        args[2] = "-host"

        assertEquals("-host",args[2])
        assertEquals("-port",args[0])
    }
}

private val mockEngine = MockEngine { request ->
    when {
        request.url.encodedPath == "/random" -> {
            respond(content = ByteReadChannel("""Hello"""))
        }

        else -> {
            respond(

                content = ByteReadChannel("""World"""),
            )
        }
    }
}
