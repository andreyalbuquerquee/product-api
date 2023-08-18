package br.com.product.productapi.services;

import java.util.List;
import java.util.Optional;

import br.com.product.productapi.shared.ProductDto;
import br.com.product.productapi.shared.ProductStockDto;

public interface ProductsService {
    List<ProductDto> getAll();
    Optional<ProductStockDto> getById(String id);
    ProductDto create(ProductDto newProductDto);
    Object[] updateById(String id, ProductDto updateProductDto);
    boolean deleteById(String id);
}
