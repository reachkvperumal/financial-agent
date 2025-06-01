package com.kv.llm.financial.agent.config;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface FinancialAgent {

    @SystemMessage("${ai.system-message}")
    String analyzeStock(@UserMessage String query);
}
