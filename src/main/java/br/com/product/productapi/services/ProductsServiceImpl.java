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
import br.com.product.productapi.utils.ReturnServiceProps;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private ProductsRepository repository;

    @Autowired
    private StocksClient stocksClient;

    @Override
    public List<ProductDto> getAll() {
        return repository.findAll().stream().map(product -> createProductDto(product)).toList();
    }

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackGetById")
    @Override
    public Optional<ProductStockDto> getById(String id) {
        Optional<Product> product = repository.findById(id);

        if (product.isPresent()) {
            Stock stock = stocksClient.getStock(product.get().getStockId());
        
            ProductStockDto productStock = this.createProductStockDto(product, stock);

            return Optional.of(productStock);
        } else {
            return Optional.empty();
        }
    }    

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackCreate")
    @Override
    public ReturnServiceProps create(ProductDto newProductDto) {
        ReturnServiceProps createProps = new ReturnServiceProps();
        Stock stock = stocksClient.getStock(newProductDto.stockId());
        createProps.setStockServiceOk(true);

        if (stock == null) {
            createProps.setStockExists(stock);
            return createProps;
        }
        createProps.setStockExists(stock);
        if (stock.getFreeSpace() < newProductDto.unity()) {
            createProps.setHasFreeSpace(false);
            return createProps;
        }
        createProps.setHasFreeSpace(true);
        
        Product product = new Product(newProductDto);
        repository.save(product);

        stock.setUsedSpace(stock.getUsedSpace() + product.getUnity());
        stocksClient.updateStock(stock.getId(), stock);

        createProps.setProductDto(this.createProductDto(product));

        return createProps;
        
    }

    @CircuitBreaker(name = "getStock", fallbackMethod = "fallbackUpdateById")
    @Override
    public ReturnServiceProps updateById(String id, ProductDto updateProductDto) {
        ReturnServiceProps updateByIdProps = new ReturnServiceProps();
        Product product = repository.findById(id).orElse(null);
        
        if (product != null) {
            Product productUpdated = new Product(updateProductDto);
            productUpdated.setId(id);
            Stock stock = stocksClient.getStock(updateProductDto.stockId());
            updateByIdProps.setStockServiceOk(true);
            
            if (stock == null) {
                updateByIdProps.setStockExists(stock);
                return updateByIdProps;
            }

            boolean isUnityHigher = productUpdated.getUnity() > product.getUnity();
            boolean hasFreeSpace = (updateProductDto.unity() - product.getUnity()) <= stock.getFreeSpace();
            updateByIdProps.setStockExists(stock);
            updateByIdProps.setHasFreeSpace(hasFreeSpace);
            
            if (isUnityHigher && hasFreeSpace) {
                stock.setUsedSpace(stock.getUsedSpace() + (updateProductDto.unity() - product.getUnity()));
                stocksClient.updateStock(stock.getId(), stock);
                repository.save(productUpdated);
            
                ProductDto productUpdatedDto = this.createProductDto(productUpdated);
                updateByIdProps.setProductDto(productUpdatedDto);
            }

            if (!isUnityHigher) {
                stock.setUsedSpace(stock.getUsedSpace() - (product.getUnity() - updateProductDto.unity()));
                stocksClient.updateStock(stock.getId(), stock);
                repository.save(productUpdated);
                ProductDto productUpdatedDto = this.createProductDto(productUpdated);
                updateByIdProps.setProductDto(productUpdatedDto);
            }
        }  
        return updateByIdProps;
    
    }

    @CircuitBreaker(name = "updateStock", fallbackMethod = "fallbackDelete")
    @Override
    public boolean deleteById(String id) {
        Optional<ProductStockDto> productDto = this.getById(id);
        Stock stock = new Stock(productDto.get().stock());
        
        stock.setUsedSpace(stock.getUsedSpace() - productDto.get().unity());
        stocksClient.updateStock(productDto.get().stock().getId(), stock);       
        repository.deleteById(id);

        return true;
    }

    private ProductDto createProductDto(Product product) {
        return new ProductDto(
        product.getId(),
        product.getName(),
        product.getValue(),
        product.getUnity(),
        product.getStockId()
        );
    }

    private ProductStockDto createProductStockDto(Optional<Product> productStock, Stock stock) {
        return new ProductStockDto(
        productStock.get().getId(), 
        productStock.get().getName(), 
        productStock.get().getValue(), 
        productStock.get().getUnity(), 
        stock
        );
    }
    
    public Optional<ProductStockDto> fallbackGetById(String id, Exception e) {
        Optional<Product> product = repository.findById(id);

        if (product.isPresent()) {
            ProductStockDto productStock = this.createProductStockDto(product, null);
            return Optional.of(productStock);
        }
        
        return Optional.empty();     
    } 
    
    public ReturnServiceProps fallbackCreate(ProductDto newProductDto, Exception e) {
        ReturnServiceProps createProps = new ReturnServiceProps();
        
        if (e.getLocalizedMessage() != null && e.getLocalizedMessage().contains("404")) {
            createProps.setStockServiceOk(true);
            createProps.setStockExists(null);

            return createProps;
        }
        createProps.setStockServiceOk(false);

        return createProps;
    }
    
    public ReturnServiceProps fallbackUpdateById(String id, ProductDto updateProductDto, Exception e) {;
        ReturnServiceProps updateByIdProps = new ReturnServiceProps();
        
        if (e.getLocalizedMessage() != null && e.getLocalizedMessage().contains("404")) {
            updateByIdProps.setStockServiceOk(true);
            updateByIdProps.setStockExists(null);

            return updateByIdProps;
        }
        updateByIdProps.setStockServiceOk(false);

        return updateByIdProps;
    }  
 
    public boolean fallbackDelete(String id, Exception e) {
        return false;
    }
    
}
