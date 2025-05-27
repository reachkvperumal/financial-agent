package com.kv.llm.financial.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Builder
@Data
public class UserReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String symbol;

    @NotBlank
    private String query;
}
