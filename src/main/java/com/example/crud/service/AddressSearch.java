package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressSearch {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProductRepository repository;

    public AddressSearch(RestTemplate restTemplate, ObjectMapper objectMapper, ProductRepository repository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    public boolean checkAvailability(String id, String cep) {
        Product product = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        Address address = searchByCep(cep);

        return Boolean.TRUE.equals(product.getActive())
                && address.getLocalidade().equalsIgnoreCase(product.getDistribution_center());
    }

    public Address searchByCep(String cep) {
        if (cep == null || !cep.matches("[0-9]{8}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um CEP com 8 números.");
        }

        Address address;
        try {
            address = restTemplate.getForObject("https://viacep.com.br/ws/{cep}/json/",
                    Address.class, cep);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível consultar o ViaCEP. Tente novamente mais tarde.");
        }

        if (address == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "O ViaCEP retornou uma resposta vazia.");
        }
        if (Boolean.TRUE.equals(address.getErro())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
        }
        if (address.getLocalidade() == null || address.getLocalidade().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "O ViaCEP não retornou a cidade.");
        }

        return address;
    }

    public String searchAddress(String state, String city, String street) {
        String url = "https://viacep.com.br/ws/{state}/{city}/{street}/json/";

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("state", state);
        uriVariables.put("city", city);
        uriVariables.put("street", street);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
            if (response.getBody() == null || response.getBody().isBlank()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "O ViaCEP retornou uma resposta vazia.");
            }
            List<Address> addresses = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Address.class));
            if (addresses == null || addresses.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.");
            }
            String cep = addresses.get(0).getCep();
            return cep;
        } catch (JsonProcessingException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Não foi possível consultar o ViaCEP.");
        }
    }
}
