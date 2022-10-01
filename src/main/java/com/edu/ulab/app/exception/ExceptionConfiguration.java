package com.edu.ulab.app.exception;

import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class ExceptionConfiguration {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<BaseWebResponse> handleMissingRequestHeaderException(ConstraintViolationException ex) {
        BaseWebResponse baseWebResponse = new BaseWebResponse(ex.getMessage());
        ex.getConstraintViolations().forEach(obj ->
                baseWebResponse.setErrorMessage(obj.getConstraintDescriptor().getMessageTemplate()));
        return new ResponseEntity<>(baseWebResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<BaseWebResponse> handleMissingRequestHeaderException(DataIntegrityViolationException ex) {
        BaseWebResponse baseWebResponse = new BaseWebResponse(ex.getCause().getMessage().split(";")[0]);

        return new ResponseEntity<>(baseWebResponse, HttpStatus.BAD_REQUEST);
    }
}
