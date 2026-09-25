package com.example.crud.controllers;

import com.example.crud.domain.product.Product;
import com.example.crud.domain.product.ProductRepository;
import com.example.crud.domain.category.RequestCategory;
import com.example.crud.domain.product.RequestProduct;
import com.example.crud.service.AddressSearch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository repository;
    private final AddressSearch addressSearch;

    @Autowired
    public ProductController(ProductRepository repository, AddressSearch addressSearch) {
        this.repository = repository;
        this.addressSearch = addressSearch;
    }

    @GetMapping("/availability/{id}")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String id, @RequestParam String cep) {
        return ResponseEntity.ok(addressSearch.checkAvailability(id, cep));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        var allProducts = repository.findAllByActiveTrue();
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/cep")
    public ResponseEntity<String> verifyAvailability(@RequestParam String state, @RequestParam String city, @RequestParam String street){
        String cep = addressSearch.searchAddress(state, city, street);
        return ResponseEntity.ok(cep);
    }

    @GetMapping("/endpoint1")
    public ResponseEntity<List<Product>> getAllProducts1(@RequestParam String categoryAsParam){
        var allProducts = repository.findAllByCategory(categoryAsParam);
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/endpoint2/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id){
        Product product = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/endpoint3/top5byprice")
    public ResponseEntity<List<Product>> getAllProducts3(){
        var allProducts = repository.findAllByActiveTrue();

        List<Product> topFive = allProducts
                .stream()
                .sorted(Comparator.comparingInt(Product::getPrice).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topFive);
    }

    @GetMapping("/category/{categoryAsPath}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @RequestHeader String categoryAsHeader,
            @PathVariable String categoryAsPath,
            @RequestBody @Valid RequestCategory categoryAsBody,
            @RequestParam String categoryAsParam
    ){
        var allProducts = repository.findAllByActiveTrue();
        List<Product> filteredProducts = new ArrayList<>();

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            if (categoryAsParam.equals(product.getCategory())) {
                filteredProducts.add(product);
            }
        }
        return ResponseEntity.ok(filteredProducts);
    }

    @PostMapping
    public ResponseEntity<Void> registerProduct(@RequestBody @Valid RequestProduct data){
        Product newProduct = new Product(data);
        repository.save(newProduct);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid RequestProduct data){
        if (data.id() == null || data.id().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Informe o ID do produto.");
        }
        Optional<Product> optionalProduct = repository.findById(data.id());
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(data.name());
            product.setPrice(data.price());
            product.setCategory(data.category());
            product.setDistribution_center(data.distributionCenter());
            return ResponseEntity.ok(product);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(@PathVariable String id){
        Optional<Product> optionalProduct = repository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(false);
            return ResponseEntity.noContent().build();
        } else {
            throw new EntityNotFoundException();
        }
    }

}
