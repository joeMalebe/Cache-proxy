package com.cache.proxy


import java.net.ServerSocket

class ProxyController {
    lateinit var server: ServerSocket
    var isStart= false
    fun request() {

    }

    fun start(serverSocket: ServerSocket= ServerSocket(3000)) {
        isStart = true
        server = serverSocket
    }

}
