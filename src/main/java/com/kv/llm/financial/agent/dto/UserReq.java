package com.kv.llm.financial.agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Builder
@Data
public class UserReq implements Serializable {

    @NotBlank
    private String symbol;

    @NotBlank
    private String query;
}
