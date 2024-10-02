package com.invest.indices.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorNode {
    private String exceptionCode;
    private String errorMessage;
}
