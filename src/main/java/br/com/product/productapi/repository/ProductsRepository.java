package br.com.product.productapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.product.productapi.models.Product;

public interface ProductsRepository extends MongoRepository<Product, String>{
    
}
