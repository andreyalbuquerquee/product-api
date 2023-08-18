package br.com.product.productapi.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.product.productapi.models.Stock;

@FeignClient("stocks")
public interface StocksClient {
    @GetMapping("/estoques/{id}")
    Stock getStock(@PathVariable String id);

    @PutMapping("/estoques/{id}")
    Stock updateStock(@PathVariable String id, Stock updateStock);
}
