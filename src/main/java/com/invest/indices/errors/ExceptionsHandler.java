package com.invest.indices.errors;


import com.invest.indices.domain.errors.InvalidResponseException;
import com.invest.indices.domain.errors.MutualFundExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(InvalidResponseException.class)
    public ResponseEntity<ErrorNode> handleInvalidResponseException(InvalidResponseException invalidResponseException) {
        ErrorNode errorNode = new ErrorNode(invalidResponseException.getExceptionCode(), invalidResponseException.getErrorMessage());
        return new ResponseEntity<>(errorNode, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MutualFundExistsException.class)
    public ResponseEntity<ErrorNode> handleInvalidResponseException(MutualFundExistsException mutualFundExistsException) {
        ErrorNode errorNode = new ErrorNode(mutualFundExistsException.getExceptionCode(), mutualFundExistsException.getErrorMessage());
        return new ResponseEntity<>(errorNode, HttpStatus.BAD_REQUEST);
    }
}
