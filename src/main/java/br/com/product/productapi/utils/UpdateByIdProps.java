package br.com.product.productapi.utils;

import br.com.product.productapi.models.Stock;
import br.com.product.productapi.shared.ProductDto;

public class UpdateByIdProps {
    private Stock stockExists;
    private boolean hasFreeSpace;
    private ProductDto updateProductDto;
    
    public UpdateByIdProps(
    Stock stockExists,
    boolean hasFreeSpace, 
    ProductDto updateProductDto) {
        this.stockExists = stockExists;
        this.hasFreeSpace = hasFreeSpace;
        this.updateProductDto = updateProductDto;
    }

    public Stock getStockExists() {
        return stockExists;
    }
    public void setStockExists(Stock stockExists) {
        this.stockExists = stockExists;
    }
    public boolean isHasFreeSpace() {
        return hasFreeSpace;
    }
    public void setHasFreeSpace(boolean hasFreeSpace) {
        this.hasFreeSpace = hasFreeSpace;
    }
    public ProductDto getUpdateProductDto() {
        return updateProductDto;
    }
    public void setUpdateProductDto(ProductDto updateProductDto) {
        this.updateProductDto = updateProductDto;
    }
}
