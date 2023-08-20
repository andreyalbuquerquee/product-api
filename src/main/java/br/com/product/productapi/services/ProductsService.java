package br.com.product.productapi.services;

import java.util.List;
import java.util.Optional;

import br.com.product.productapi.shared.ProductDto;
import br.com.product.productapi.shared.ProductStockDto;
import br.com.product.productapi.utils.ReturnServiceProps;

public interface ProductsService {
    List<ProductDto> getAll();
    Optional<ProductStockDto> getById(String id);
    ReturnServiceProps create(ProductDto newProductDto);
    ReturnServiceProps updateById(String id, ProductDto updateProductDto);
    boolean deleteById(String id);
}
