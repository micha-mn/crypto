package com.crypto.analysis.websocket.orderBook.client;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import com.crypto.analysis.service.OrderBookService;
import com.crypto.analysis.websocket.orderBook.client.core.PingAwareWebSocketClient;
import com.crypto.analysis.websocket.orderBook.config.DepthUpdateDTO;
import com.crypto.analysis.websocket.orderBook.dto.BinanceOrderBook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class BinanceWebSocketClient // extends PingAwareWebSocketClient 
{

    private final OrderBookService orderBookService;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private StringBuilder messageBuffer = new StringBuilder();

    public BinanceWebSocketClient(OrderBookService orderBookService) throws URISyntaxException {
      //  super(new URI("wss://stream.binance.com:9443/ws/btcusdt@depth"), new Draft_6455());
        this.orderBookService = orderBookService;
     //   this.setConnectionLostTimeout(60); // optional: detects dead connection
    }

  //  @Override
    public void onOpen(ServerHandshake handshake) {
    	System.out.println("✅ Connected to Binance Order Book WebSocket");
    }
    
  //  @Override
    public void onMessage(String message) {
        messageBuffer.append(message);
        processFullMessage(messageBuffer.toString());
        messageBuffer.setLength(0);
    }

    private void processFullMessage(String fullMessage) {
        try {
            System.out.println("📥 Received Order Book Update: " + fullMessage);

            JsonNode jsonNode = objectMapper.readTree(fullMessage);

            // Ensure JSON structure is valid
            if (jsonNode == null || jsonNode.isEmpty()) {
                System.out.println("⚠️ Received empty JSON message");
                return;
            }

            // Extract event fields with null checks
            String eventType = jsonNode.has("e") ? jsonNode.get("e").asText() : "unknown";
            long eventTime = jsonNode.has("E") ? jsonNode.get("E").asLong() : System.currentTimeMillis();
            String symbol = jsonNode.has("s") ? jsonNode.get("s").asText() : "BTCUSDT";
            long firstUpdateId = jsonNode.has("U") ? jsonNode.get("U").asLong() : -1;
            long finalUpdateId = jsonNode.has("u") ? jsonNode.get("u").asLong() : -1;

            // Convert JSON "b" (bids) and "a" (asks) into Java lists (limit to top 10)
            List<BinanceOrderBook> bids = parseOrders(jsonNode.get("b"), 1000); // Buy
            List<BinanceOrderBook> asks = parseOrders(jsonNode.get("a"), 1000); // Sell

            // Create DTO
            DepthUpdateDTO depthUpdateDTO = new DepthUpdateDTO(eventType, eventTime, symbol, firstUpdateId, finalUpdateId, bids, asks);
            System.out.println("✅ Converted Order Book DTO: " + depthUpdateDTO);

            // Save the order book data in the database
            orderBookService.saveOrderBookLst(BinanceOrderBook.convertToEntity(asks, "sell", eventTime));
            orderBookService.saveOrderBookLst(BinanceOrderBook.convertToEntity(bids, "buy", eventTime));

        } catch (Exception e) {
            System.err.println("❌ Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }
/*
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket Closed: " + reason + " (code: " + code + ")");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("⚠️ WebSocket Error:");
        ex.printStackTrace();
    }
*/
    private List<BinanceOrderBook> parseOrders(JsonNode ordersNode, int limit) {
        List<BinanceOrderBook> orders = new ArrayList<>();

        if (ordersNode == null || ordersNode.isEmpty()) {
            System.out.println("⚠️ Warning: Received empty orders.");
            return orders; // Return empty list to prevent errors
        }

        for (int i = 0; i < Math.min(limit, ordersNode.size()); i++) {
            BigDecimal price = new BigDecimal(ordersNode.get(i).get(0).asText());
            BigDecimal quantity = new BigDecimal(ordersNode.get(i).get(1).asText());
            orders.add(new BinanceOrderBook(price, quantity));
        }
        return orders;
    }
}
