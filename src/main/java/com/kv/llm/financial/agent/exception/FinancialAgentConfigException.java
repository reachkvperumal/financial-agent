package com.kv.llm.financial.agent.exception;

public class FinancialAgentConfigException extends RuntimeException {
    public FinancialAgentConfigException() {
    }

    public FinancialAgentConfigException(String message) {
        super(message);
    }

    public FinancialAgentConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinancialAgentConfigException(Throwable cause) {
        super(cause);
    }

    public FinancialAgentConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
