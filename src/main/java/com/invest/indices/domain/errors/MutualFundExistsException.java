package com.invest.indices.domain.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutualFundExistsException extends RuntimeException {
    private String exceptionCode;
    private String errorMessage;
}
