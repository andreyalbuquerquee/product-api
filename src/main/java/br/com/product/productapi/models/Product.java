package br.com.product.productapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.product.productapi.shared.ProductDto;

@Document("products")
public class Product {

    @Id
    private String id;
    private String name;
    private Double value;
    private int unity;
    private String stockId;


    public String getStockId() {
        return stockId;
    }
    public void setStockId(String stockId) {
        this.stockId = stockId;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getUnity() {
        return unity;
    }
    public void setUnity(int unity) {
        this.unity = unity;
    }


    public Product() {}

    public Product(ProductDto dto){
        this.id = dto.id();
        this.name = dto.name();
        this.value = dto.value();
        this.unity = dto.unity();
        this.stockId = dto.stockId();
    }
}