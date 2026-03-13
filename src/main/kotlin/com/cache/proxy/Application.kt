package com.cache.proxy

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

var CLIENT_HOST = "0.0.0.0"

fun main(args: Array<String>) {
    var port = 8080

    try {
        port = args[1].toInt()
        CLIENT_HOST = args[3]
    } catch (ex: Exception) {
        println("See exception message $ex")
        println("Invalid args. Defaulting to --port $port")

    } finally {
        embeddedServer(
            Netty,
            port = port,
            host = "0.0.0.0",
            ){
            module(ProxyController(HttpClient(CIO),CLIENT_HOST))
        }.start(wait = true)
    }
}

fun Application.module(proxyController: ProxyController) {
    configureRouting(proxyController = proxyController)
}
