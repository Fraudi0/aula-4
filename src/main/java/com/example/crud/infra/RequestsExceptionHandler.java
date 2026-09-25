package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Produto não encontrado.", 404);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDTO> handleIntegrationError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ExceptionDTO(exception.getReason(), exception.getStatusCode().value()));
    }
}
