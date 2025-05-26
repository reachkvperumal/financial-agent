package com.kv.llm.financial.agent.dto;

import java.math.BigDecimal;

public record FinancialData(String symbol, String analystEstimates, String analystRating, String priceTargetSummary)    {
}
