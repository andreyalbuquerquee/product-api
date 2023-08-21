package br.com.product.productapi.utils;

import br.com.product.productapi.models.Stock;
import br.com.product.productapi.shared.ProductDto;

public class ReturnServiceProps {
    private boolean isStockServiceOk;
    private Stock stock;
    private boolean hasFreeSpace;
    private ProductDto productDto;
    
    public ReturnServiceProps() {}
    
    public ReturnServiceProps(
    Stock stock,
    boolean hasFreeSpace, 
    ProductDto productDto) {
        this.stock = stock;
        this.hasFreeSpace = hasFreeSpace;
        this.productDto = productDto;
    }

    public boolean isStockServiceOk() {
        return isStockServiceOk;
    }
    public void setStockServiceOk(boolean isStockServiceOk) {
        this.isStockServiceOk = isStockServiceOk;
    }
    public Stock getStockExists() {
        return stock;
    }
    public void setStockExists(Stock stock) {
        this.stock = stock;
    }
    public boolean isHasFreeSpace() {
        return hasFreeSpace;
    }
    public void setHasFreeSpace(boolean hasFreeSpace) {
        this.hasFreeSpace = hasFreeSpace;
    }
    public ProductDto getProductDto() {
        return productDto;
    }
    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }
}
