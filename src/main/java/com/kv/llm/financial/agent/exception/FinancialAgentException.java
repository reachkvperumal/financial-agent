package com.kv.llm.financial.agent.exception;

public class FinancialAgentException extends RuntimeException {

    public FinancialAgentException() {
        super();
    }

    public FinancialAgentException(String message) {
        super(message);
    }

    public FinancialAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public FinancialAgentException(Throwable cause) {
        super(cause);
    }

    protected FinancialAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
