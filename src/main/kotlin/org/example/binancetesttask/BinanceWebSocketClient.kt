package org.example

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage

data class SubscriptionRequest(
    val method: String,
    val params: List<String>,
    val id: Int
)

@Component
class BinanceWebSocketClient : CommandLineRunner, WebSocket.Listener {

    private val mapper = jacksonObjectMapper()

    override fun run(vararg args: String?) {
        val client = HttpClient.newHttpClient()
        val uri = URI.create("wss://fstream.binance.com/ws")
        client.newWebSocketBuilder()
            .buildAsync(uri, this)
            .thenAccept { webSocket ->
                println("Connected to Binance Futures WebSocket")
                subscribeToKline(webSocket, "btcusdt", "1m")
            }
            .join()
    }

    private fun subscribeToKline(webSocket: WebSocket, symbol: String, interval: String) {
        val stream = "${symbol.lowercase()}@kline_$interval"
        val request = SubscriptionRequest("SUBSCRIBE", listOf(stream), 1)
        val jsonRequest = mapper.writeValueAsString(request)
        println("Sending subscription: $jsonRequest")
        webSocket.sendText(jsonRequest, true)
    }

    override fun onOpen(webSocket: WebSocket) {
        println("WebSocket opened")
        super.onOpen(webSocket)
    }

    override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
        println("Received data: $data")
        return super.onText(webSocket, data, last)
    }

    override fun onError(webSocket: WebSocket, error: Throwable) {
        println("Error: ${error.message}")
        super.onError(webSocket, error)
    }

    override fun onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletionStage<*>? {
        println("WebSocket closed: $statusCode - $reason")
        return super.onClose(webSocket, statusCode, reason)
    }
}
