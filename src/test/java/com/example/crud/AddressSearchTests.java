package com.example.crud;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.service.AddressSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class AddressSearchTests {
    private ProductRepository repository;
    private MockRestServiceServer server;
    private AddressSearch service;
    private Product product;

    @BeforeEach
    void setup() {
        repository = mock(ProductRepository.class);
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service = new AddressSearch(restTemplate, new ObjectMapper(), repository);
        product = new Product();
        product.setActive(true);
        product.setDistribution_center("Mogi das Cruzes");
        when(repository.findById("p1")).thenReturn(Optional.of(product));
    }

    @Test
    void returnsTrueForSameCity() {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess("{\"localidade\":\"Mogi das Cruzes\"}", MediaType.APPLICATION_JSON));
        assertTrue(service.checkAvailability("p1", "08773380"));
        server.verify();
    }

    @Test
    void returnsFalseForDifferentCity() {
        server.expect(requestTo("https://viacep.com.br/ws/50010000/json/"))
                .andRespond(withSuccess("{\"localidade\":\"Recife\"}", MediaType.APPLICATION_JSON));
        assertFalse(service.checkAvailability("p1", "50010000"));
        server.verify();
    }

    @Test
    void rejectsInvalidCepBeforeCallingApi() {
        for (String cep : new String[]{null, "", "123", "abcdefgh", "08773-380", "08773--380", "087733800", " 08773380"}) {
            ResponseStatusException error = assertThrows(ResponseStatusException.class,
                    () -> service.checkAvailability("p1", cep));
            assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        }
        server.verify();
    }

    @Test
    void rejectsUnknownProduct() {
        assertThrows(EntityNotFoundException.class, () -> service.checkAvailability("missing", "08773380"));
        server.verify();
    }

    @Test
    void rejectsUnknownCep() {
        server.expect(requestTo("https://viacep.com.br/ws/00000000/json/"))
                .andRespond(withSuccess("{\"erro\":\"true\"}", MediaType.APPLICATION_JSON));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.checkAvailability("p1", "00000000"));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        server.verify();
    }

    @Test
    void handlesConnectionFailure() {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withException(new IOException("Falha de conexão")));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.checkAvailability("p1", "08773380"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, error.getStatusCode());
        server.verify();
    }

    @Test
    void handlesIncompleteResponse() {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.checkAvailability("p1", "08773380"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, error.getStatusCode());
        server.verify();
    }

    @Test
    void inactiveProductIsUnavailable() {
        product.setActive(false);
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
                .andRespond(withSuccess("{\"localidade\":\"Mogi das Cruzes\"}", MediaType.APPLICATION_JSON));
        assertFalse(service.checkAvailability("p1", "08773380"));
        server.verify();
    }
}
