package com.kv.llm.financial.agent.client;

import com.kv.llm.financial.agent.dto.FinancialData;
import com.kv.llm.financial.agent.service.FmpSvc;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class McpClientToolProvider {

    private final FmpSvc fmpSvc;

    public McpClientToolProvider(FmpSvc fmpSvc) {
        this.fmpSvc = fmpSvc;
    }

    @Tool("Fetches comprehensive financial data (analyst price target, rating, revenue, and EPS) for a given stock symbol")
    public FinancialData getFinancialData(String symbol) {
        return fmpSvc.getFinancialData(symbol);
    }

    @Tool("Fetches analyst financial estimates for a stock symbol from FMP")
    public FinancialData getAnalystEstimates(String symbol) {
        return fmpSvc.getAnalystEstimates(symbol);
    }

    @Tool("Fetches financial ratings snapshot for a stock symbol from FMP")
    public FinancialData getRatingsSnapshot(String symbol) {
        return fmpSvc.getRatingsSnapshot(symbol);
    }

    @Tool("Fetches price target summary for a stock symbol from FMP")
    public FinancialData getTargetData(String symbol) {
        return fmpSvc.getTargetData(symbol);
    }
}
