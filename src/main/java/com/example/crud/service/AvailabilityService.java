package com.example.crud.service;

import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AvailabilityService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AvailabilityService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public boolean getAddressByCep(String cep, Optional<Product> product) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            Address address = objectMapper.readValue(response.getBody(), Address.class);
            String localidade = address.getLocalidade();

            if (product.isPresent()) {
                Product prod = product.get();
                String distributionCenter = prod.getDistribution_center();

                if (localidade.equals(distributionCenter)) {
                    System.out.println("Localidade matches the product's distribution center.");
                    return true;
                } else {
                    System.out.println("Localidade does not match the product's distribution center.");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

}
