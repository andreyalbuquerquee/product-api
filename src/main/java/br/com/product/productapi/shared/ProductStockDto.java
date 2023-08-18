package br.com.product.productapi.shared;

import br.com.product.productapi.models.Stock;

public record ProductStockDto (
    String id,

    String name,

    Double value,

    int unity,

    Stock stock
) { }

