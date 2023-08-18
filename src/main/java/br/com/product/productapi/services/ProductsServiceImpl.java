package br.com.product.productapi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.product.productapi.httpClient.StocksClient;
import br.com.product.productapi.models.Product;
import br.com.product.productapi.models.Stock;
import br.com.product.productapi.repository.ProductsRepository;
import br.com.product.productapi.shared.ProductDto;
import br.com.product.productapi.shared.ProductStockDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private ProductsRepository repository;

    @Autowired
    private StocksClient stocksClient;

    @Override
    public List<ProductDto> getAll() {
        return repository.findAll().stream().map(p -> new ProductDto(p.getId(), p.getName(), p.getValue(), p.getUnity(), p.getStockId())).toList();
    }

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackGetById")
    @Override
    public Optional<ProductStockDto> getById(String id) {
        Optional<Product> product = repository.findById(id);

        if (product.isPresent()) {
            Stock stock = stocksClient.getStock(product.get().getStockId());
        
            ProductStockDto productStock = this.productStockDto(product, stock);

            return Optional.of(productStock);
        } else {
            return Optional.empty();
        }
    }    

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackGetById")
    @Override
    public ProductDto create(ProductDto newProductDto) {
        Stock stock = stocksClient.getStock(newProductDto.stockId());
        
        if (stock.getFreeSpace() < newProductDto.unity()) {
            return null;
        }
        
        Product product = new Product(newProductDto);
        repository.save(product);

        stock.setUsedSpace(stock.getUsedSpace() + product.getUnity());

        stocksClient.updateStock(stock.getId(), stock);

        return this.productDto(product);
        
    }

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackUpdateById")
    @Override
    public Object[] updateById(String id, ProductDto updateProductDto) {
        Object[] updateByIdProps = new Object[3];
        Product product = repository.findById(id).orElse(null);
        
        if (product != null) {
            Product productUpdated = new Product(updateProductDto);
            productUpdated.setId(id);
            Stock stock = stocksClient.getStock(updateProductDto.stockId());
            
            if (stock == null) {
                updateByIdProps[0] = stock;
                return updateByIdProps;
            }

            boolean isUnityHigher = productUpdated.getUnity() > product.getUnity();
            boolean hasFreeSpace = (updateProductDto.unity() - product.getUnity()) <= stock.getFreeSpace();
            updateByIdProps[0] = stock;
            updateByIdProps[1] = hasFreeSpace; 
            
            if (isUnityHigher == true && hasFreeSpace == true) {
                stock.setUsedSpace(stock.getUsedSpace() + (updateProductDto.unity() - product.getUnity()));
                stocksClient.updateStock(stock.getId(), stock);
                repository.save(productUpdated);
            
                ProductDto productUpdatedDto =  this.productDto(productUpdated);
                updateByIdProps[2] = (productUpdatedDto);
            }

            if (isUnityHigher == false) {
                stock.setUsedSpace(stock.getUsedSpace() - (product.getUnity() - updateProductDto.unity()));
                stocksClient.updateStock(stock.getId(), stock);
                repository.save(productUpdated);
                ProductDto productUpdatedDto =  this.productDto(productUpdated);
                updateByIdProps[2] = (productUpdatedDto);
            }
        }  
        return updateByIdProps;
    
    }

    @CircuitBreaker(name = "updateStock", fallbackMethod = "fallbackBlockOperation")
    @Override
    public boolean deleteById(String id) {
        Optional<ProductStockDto> productDto = this.getById(id);
        
        Stock stock = new Stock(productDto.get().stock().getId(), productDto.get().stock().getTotalSpace(), productDto.get().stock().getUsedSpace());
      
        stock.setUsedSpace(stock.getUsedSpace() - productDto.get().unity());

        stocksClient.updateStock(productDto.get().stock().getId(), stock);
        
        repository.deleteById(id);

        return true;
    }

    
    private ProductDto productDto(Product product) {
        return new ProductDto(product.getId(),
        product.getName(),
        product.getValue(),
        product.getUnity(),
        product.getStockId());
    }

    private ProductStockDto productStockDto(Optional<Product> productStock, Stock stock) {
        return new ProductStockDto(productStock.get().getId(), productStock.get().getName(), productStock.get().getValue(), productStock.get().getUnity(), stock);
    }
    
    public Optional<ProductStockDto> fallbackGetById(String id, Exception e) {
        Optional<Product> product = repository.findById(id);

        if (product.isPresent()) {
            ProductStockDto productStock = this.productStockDto(product, null);
            return Optional.of(productStock);
        }
        
        return Optional.empty();     
    } 
    
    public Object[] fallbackUpdateById(String id, ProductDto updateProductDto, Exception e) {
        Object[] updateByIdProps = new Object[3];
        return updateByIdProps;
    }  
 
    public boolean fallbackBlockOperation(String id, Exception e) {
        return false;
    }
    
}
