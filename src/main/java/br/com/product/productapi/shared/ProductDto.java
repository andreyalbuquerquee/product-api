package br.com.product.productapi.shared;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDto (
    String id,

    @NotEmpty(message = "Nome não pode estar vazio")
    String name,

    @NotNull(message = "Valor não pode estar vazio")
    @Positive(message = "Valor deve ser um valor positivo")
    Double value,

    @NotNull(message = "Unidades não pode estar vazia")
    @Positive(message = "Unidade deve ser um valor positivo")
    int unity,

    @NotNull(message = "Estoque não pode estar vazio")
    String stockId

) { }
