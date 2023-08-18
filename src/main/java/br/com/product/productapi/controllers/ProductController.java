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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProductController {

    @Autowired
    private ProductsService service;
    
    @GetMapping
    private ResponseEntity<List<ProductDto>> getAllProducts() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    private ResponseEntity<ProductStockDto> getProductById(@PathVariable String id) {
        Optional<ProductStockDto> product = service.getById(id);

        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }

        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    @PostMapping
    private ResponseEntity<?> createProduct(@RequestBody @Valid ProductDto product) {
        ProductDto newProduct = service.create(product);
        
        if (newProduct == null) {          
            return new ResponseEntity<String>("Estoque insuficiente para armazenar o produto!" ,HttpStatus.CONFLICT);
        }
        
        return new ResponseEntity<ProductDto>(newProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody @Valid ProductDto product) {
        Object[] updateByIdProps = service.updateById(id, product);
        Optional<ProductStockDto> productExist = service.getById(id);

        if (productExist.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    
        if (updateByIdProps[0] == null) {
            return new ResponseEntity<String>("Id de estoque não encontrado!", HttpStatus.NOT_FOUND);
        }

        if (updateByIdProps[1].equals(false)) {
            return new ResponseEntity<String>("Espaço de estoque insuficiente para armazenar o produto!", HttpStatus.CONFLICT);
        }
        
        return new ResponseEntity<>(updateByIdProps[2], HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteProductById(@PathVariable String id) {
        Optional<ProductStockDto> product = service.getById(id);

        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if (service.deleteById(id) == false) {
            return new ResponseEntity<String>("Operação não pode ser concluída!", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
}

