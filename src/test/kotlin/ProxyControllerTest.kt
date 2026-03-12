package com.cache.proxy

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.net.ServerSocket


class ProxyControllerTest {

    lateinit var proxy:ProxyController

    val serverSocket: ServerSocket = mock()

    @BeforeEach
    fun setUp () {

        proxy = ProxyController()
    }

    @Test
    fun canCallRequestFunction() {
        ProxyController().request()
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
