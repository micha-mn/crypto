package com.crypto.analysis.websocket.BinanceKline;

import com.crypto.analysis.service.CoinGeckoMarketDataService;
import com.crypto.analysis.service.CryptoAnalyseHighLowService;
import com.crypto.analysis.websocket.BinanceKline.dto.BinanceKlineDTO;
import com.crypto.analysis.websocket.BinanceKline.util.ContextUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class CombinedKlineWebSocketClient extends WebSocketClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CryptoAnalyseHighLowService cryptoService;
    private final CoinGeckoMarketDataService coinGeckoMarketDataService;
    private final List<String> subscribedSymbols;
    private static CombinedKlineWebSocketClient activeClient = null;
    private static boolean isReconnecting = false;
    
    public CombinedKlineWebSocketClient(
            List<String> symbols,
            CryptoAnalyseHighLowService cryptoService,
            CoinGeckoMarketDataService coinGeckoMarketDataService) throws Exception {

        super(new URI("wss://stream.binance.com:9443/stream?streams=" + String.join("/", buildStreamList(symbols))));
        this.cryptoService = cryptoService;
        this.coinGeckoMarketDataService = coinGeckoMarketDataService;
        this.subscribedSymbols = symbols;
    }

    private static List<String> buildStreamList(List<String> symbols) {
        List<String> streams = new ArrayList<>();
        for (String symbol : symbols) {
            streams.add(symbol.toLowerCase() + "@kline_1m");
        }
        return streams;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    	 System.out.println("✅ Connected to Binance combined WebSocket stream for: " + subscribedSymbols);
    	    activeClient = this;
    	    isReconnecting = false;
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            JsonNode data = json.path("data");
            String stream = json.path("stream").asText();
            String symbol = data.path("s").asText(); // e.g., BTCUSDT
            JsonNode klineData = data.path("k");

            boolean isFinal = klineData.path("x").asBoolean(false);
            if (!isFinal) return;

            BinanceKlineDTO dto = BinanceKlineDTO.builder()
                    .symbol(symbol)
                    .openTime(klineData.path("t").asLong())
                    .closeTime(klineData.path("T").asLong())
                    .open(new BigDecimal(klineData.path("o").asText()))
                    .high(new BigDecimal(klineData.path("h").asText()))
                    .low(new BigDecimal(klineData.path("l").asText()))
                    .close(new BigDecimal(klineData.path("c").asText()))
                    .volume(new BigDecimal(klineData.path("q").asText()))
                    .isFinal(true)
                    .build();

            saveEnrichedKline(dto);

        } catch (Exception e) {
            System.err.println("❌ Error parsing combined stream message: " + e.getMessage());
        }
    }

    private void saveEnrichedKline(BinanceKlineDTO dto) {
        try {
            LocalDateTime startTime = Instant.ofEpochMilli(dto.getOpenTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
            LocalDateTime endTime = Instant.ofEpochMilli(dto.getCloseTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();

            Map<String, Object> klineMap = new HashMap<>();
            klineMap.put("startTime", dto.getOpenTime());
            klineMap.put("endTime", dto.getCloseTime());
            klineMap.put("open", dto.getOpen());
            klineMap.put("high", dto.getHigh());
            klineMap.put("low", dto.getLow());
            klineMap.put("close", dto.getClose());
            klineMap.put("volume", dto.getVolume());

            String cgSymbol = mapToCoinGeckoSymbol(dto.getSymbol());
            Map<String, Double> marketData = Optional.ofNullable(
                    coinGeckoMarketDataService.getCachedMarketData().get(cgSymbol)
            ).orElse(new HashMap<>());

            BigDecimal marketCap = BigDecimal.valueOf(marketData.getOrDefault("market_cap", 0.0));
            BigDecimal totalVolume = BigDecimal.valueOf(marketData.getOrDefault("total_volume", 0.0));

            BigDecimal fundingRate = null;
            if (cryptoService.isValidFundingInterval(dto.getOpenTime())) {
                fundingRate = cryptoService.fetchFundingRate(dto.getSymbol());
            }

            cryptoService.saveCryptoSnapshot(dto.getSymbol(), startTime, endTime, klineMap, marketCap, totalVolume, fundingRate);

        } catch (Exception e) {
            System.err.println("❌ Failed to enrich/save combined stream kline: " + e.getMessage());
        }
    }

    private String mapToCoinGeckoSymbol(String binanceSymbol) {
        switch (binanceSymbol) {
            case "BTCUSDT": return "bitcoin";
            case "ETHUSDT": return "ethereum";
            case "SOLUSDT": return "solana";
            case "SHIBUSDT": return "shiba-inu";
            case "BNBUSDT": return "binancecoin";
            case "XRPUSDT": return "ripple";
            default: return "";
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("❌ Combined WebSocket closed: " + reason + " (code " + code + ")");
        reconnectWithDelay();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("❌ Combined WebSocket error: " + ex.getMessage());
        reconnectWithDelay();
    }

    private void reconnectWithDelay() {
        if (isReconnecting) return;
        isReconnecting = true;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // ✅ Add context check here
                    if (!ContextUtil.isContextRunning()) {
                        System.err.println("⚠️ Spring context is closed. Aborting WebSocket reconnect for " + subscribedSymbols);
                        return;
                    }

                    System.out.println("🔄 Reconnecting to combined stream...");

                    if (activeClient != null && activeClient.isOpen()) {
                        System.out.println("⚠️ Closing existing WebSocket before reconnect...");
                        activeClient.closeBlocking();
                    }

                    CombinedKlineWebSocketClient newClient = new CombinedKlineWebSocketClient(
                            subscribedSymbols, cryptoService, coinGeckoMarketDataService
                    );
                    newClient.connect();
                    activeClient = newClient;
                    isReconnecting = false;

                } catch (Exception e) {
                    isReconnecting = false;
                    System.err.println("❌ Reconnect failed: " + e.getMessage());
                }
            }
        }, 5000);
    }
}
