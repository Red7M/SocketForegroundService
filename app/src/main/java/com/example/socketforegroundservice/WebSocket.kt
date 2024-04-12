package com.example.socketforegroundservice

import android.util.Log
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import okio.ByteString.Companion.decodeHex

class WebSocket : WebSocketListener() {

    private var mWebSocket : WebSocket? = null
    private var count = 10

    fun run() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
        val request: Request = Request.Builder()
            .url("ws://echo.websocket.org")
            .build()
        client.newWebSocket(request, this)

        // Trigger shutdown of the dispatcher's executor so this process exits immediately.
        client.dispatcher.executorService.shutdown()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        mWebSocket = webSocket
        while (count >= 0) {
            webSocket.send("Count is $count")
            Log.i("WebSocket: onOpen: ", "Count is $count")
            count--
            Thread.sleep(1000)
        }
    }

    fun closeWebSocket() {
        // websocket is already closed
        if (mWebSocket == null) return
        mWebSocket!!.close(1000, "user stopped service")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i("WebSocket: onMessage: ", "Count is $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        System.out.println("MESSAGE: " + bytes.hex())
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.i("onClosed: ", "Count stopped")
        Log.i("onClosed: ", reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
    }
}