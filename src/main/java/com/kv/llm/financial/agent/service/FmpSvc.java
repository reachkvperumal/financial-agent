package com.kv.llm.financial.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kv.llm.financial.agent.dto.FinancialData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Log4j2
@Service
public class FmpSvc {

    @Value("${fmp.financial-estimate}")
    private String financial_estimate_path;

    @Value("${fmp.financial-ratings}")
    private String financial_ratings_path;

    @Value("${fmp.financial-target}")
    private String financial_target_path;

    @Value("${fmp.api-key}")
    private String apiKey;

    private final RestClient restClient;

    public FmpSvc(RestClient fmpClient) {
        this.restClient = fmpClient;
    }

    public FinancialData getFinancialData(String symbol) {
        return new FinancialData(symbol, fetch(financial_estimate_path), fetch(financial_ratings_path), fetch(financial_target_path));
    }

    public FinancialData getAnalystEstimates(String symbol) {
        return new FinancialData(symbol,fetch(financial_estimate_path),null,null);
    }

    public FinancialData getRatingsSnapshot(String symbol) {
        return new FinancialData(symbol,null,fetch(financial_ratings_path),null);
    }

    public FinancialData getTargetData(String symbol) {
        return new FinancialData(symbol,null,null,fetch(financial_target_path));
    }

    private String fetch(String path) {
        log.info("ESTIMATE PATH {}", path);
        try {
            JsonNode response = restClient.get()
                    .uri(path + apiKey)
                    .retrieve()
                    .body(JsonNode.class);
            return Optional.ofNullable(response).map(o -> o.asText()).orElseGet(() -> "N/A");
        } catch (Exception e) {
            return "N/A";
        }
    }

}
