package br.com.product.productapi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.product.productapi.services.ProductsService;
import br.com.product.productapi.shared.ProductDto;
import br.com.product.productapi.shared.ProductStockDto;
import br.com.product.productapi.utils.ReturnServiceProps;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProductController {

    @Autowired
    private ProductsService service;
    
    @GetMapping
    private ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAll());
    }

    @GetMapping("/{id}")
    private ResponseEntity<Object> getProductById(@PathVariable String id) {
        Optional<ProductStockDto> productStockDtoOptional = service.getById(id);

        if (!productStockDtoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado."); 
        }

        return ResponseEntity.status(HttpStatus.OK).body(productStockDtoOptional.get());
    }

    @PostMapping
    private ResponseEntity<Object> createProduct(@RequestBody @Valid ProductDto product) {
        ReturnServiceProps createProps = service.create(product);
        
        if(!createProps.isStockServiceOk()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Operação não pode ser concluída, serviço de estoque indisponível.");
        }
        if (createProps.getStockExists() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id de estoque não encontrado.");
        }
        if (!createProps.isHasFreeSpace()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Espaço de estoque insuficiente para armazenar o produto!");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createProps.getProductDto());
    }

    @PutMapping("/{id}")
    private ResponseEntity<Object> updateProduct(@PathVariable String id, @RequestBody @Valid ProductDto product) {
        Optional<ProductStockDto> productStockDtoOptional = service.getById(id);
        ReturnServiceProps updateByIdProps = service.updateById(id, product);
        
        if (!productStockDtoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }
        if(!updateByIdProps.isStockServiceOk()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Operação não pode ser concluída, serviço de estoque indisponível.");
        }
        if (updateByIdProps.getStockExists() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id de estoque não encontrado.");
        }
        if (!updateByIdProps.isHasFreeSpace()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Espaço de estoque insuficiente para armazenar o produto!");
        }
        
        return ResponseEntity.status(HttpStatus.OK).body(updateByIdProps.getProductDto());
    }
    
    @DeleteMapping("/{id}")
    private ResponseEntity<Object> deleteProductById(@PathVariable String id) {
        Optional<ProductStockDto> productStockDtoOptional = service.getById(id);

        if (!productStockDtoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado.");
        }
        if (!service.deleteById(id)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Operação não pode ser concluída, serviço de estoque indisponível.");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    
}

