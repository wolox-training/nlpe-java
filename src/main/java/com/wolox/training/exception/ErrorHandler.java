package com.wolox.training.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            BookNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<Response> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ex.getMessage()));
    }

    @ExceptionHandler({
            BookIdMismatchException.class,
            UserIdMismatchException.class,
            ConstraintViolationException.class,
            DataIntegrityViolationException.class,
            BookAlreadyOwnedException.class
    })
    public ResponseEntity<Response> handleBadRequest(Exception ex) {

        Response response = new Response(ex.getMessage());
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ex;
            response = new Response(this.buildErrorList(e.getConstraintViolations()));
        }

        return ResponseEntity.badRequest().body(response);
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
