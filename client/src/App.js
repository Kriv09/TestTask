import React, { useEffect, useState } from 'react';

function BinanceWebSocketTest() {
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    const ws = new WebSocket("wss://fstream.binance.com/ws");
    ws.onopen = () => {
      console.log("Connected to Binance WebSocket");
      // Send subscription message
      ws.send(JSON.stringify({
        method: "SUBSCRIBE",
        params: ["btcusdt@kline_1m"],
        id: 1,
      }));
    };

    ws.onmessage = (event) => {
      console.log("Message received:", event.data);
      setMessages(prev => [...prev, event.data]);
    };

    ws.onerror = (error) => {
      console.error("WebSocket error:", error);
    };

    ws.onclose = () => {
      console.log("WebSocket closed");
    };

    // Clean up the connection when the component unmounts
    return () => {
      ws.close();
    };
  }, []);

  return (
    <div>
      <h1>Binance WebSocket Messages</h1>
      <ul>
        {messages.map((msg, index) => (
          <li key={index}>{msg}</li>
        ))}
      </ul>
    </div>
  );
}

export default BinanceWebSocketTest;
