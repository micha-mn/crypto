package com.crypto.analysis.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CoinGeckoMarketDataService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Map<String, Double>> cache = new ConcurrentHashMap<>();
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets";

    private static final String[] COINGECKO_SYMBOLS = {
        "bitcoin", "ethereum", "solana", "shiba-inu", "binancecoin", "ripple"
    };

    public Map<String, Map<String, Double>> getCachedMarketData() {
        return cache;
    }

    @Scheduled(fixedRate = 60000)
    public void refreshCache() {
        try {
            String symbols = String.join(",", COINGECKO_SYMBOLS);
            String url = UriComponentsBuilder.fromHttpUrl(COINGECKO_API_URL)
                    .queryParam("vs_currency", "usd")
                    .queryParam("ids", symbols)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JSONArray jsonArray = new JSONArray(response);
            Map<String, Map<String, Double>> updated = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                double marketCap = jsonObject.getDouble("market_cap");
                double totalVolume = jsonObject.getDouble("total_volume");

                Map<String, Double> data = new HashMap<>();
                data.put("market_cap", marketCap);
                data.put("total_volume", totalVolume);
                updated.put(id, data);
            }

            cache.clear();
            cache.putAll(updated);

            System.out.println("✅ Updated market cap/volume cache at: " + System.currentTimeMillis());

        } catch (Exception e) {
            System.err.println("❌ Error refreshing market cap and volume data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
