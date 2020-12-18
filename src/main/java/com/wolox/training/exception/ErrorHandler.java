package com.wolox.training.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            BookNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        return handleExceptionInternal(
                ex,
                new Response(ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler({
            BookIdMismatchException.class,
            ConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {

        Response response = new Response(ex.getMessage());
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ex;
            response = new Response(this.buildErrorList(e.getConstraintViolations()));
        }

        return handleExceptionInternal(
                ex,
                response,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Response {

        private String message;
        private List<Err> errors;
        private Timestamp date;

        private Response(String message) {
            this.message = message;
            this.date = new Timestamp(System.currentTimeMillis());
        }

        private Response(List<Err> errors) {
            this.errors = errors;
            this.date = new Timestamp(System.currentTimeMillis());
        }

        public String getMessage() {
            return message;
        }

        public Timestamp getDate() {
            return date;
        }

        public List<Err> getErrors() {
            return errors;
        }
    }

    private static class Err {
        String property;
        String message;

        private Err(String property, String message) {
            this.property = property;
            this.message = message;
        }

        public String getProperty() {
            return property;
        }

        public String getMessage() {
            return message;
        }
    }

    private List<Err> buildErrorList(Set<ConstraintViolation<?>> list) {
        return list
                .stream()
                .map(err -> new Err(err.getPropertyPath().toString(), err.getMessage()))
                .collect(Collectors.toList());
    }
}
