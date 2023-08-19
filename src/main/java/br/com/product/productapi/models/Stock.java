package br.com.product.productapi.models;

public class Stock {
    private String id;
    private int totalSpace;
    private int usedSpace;

    public Stock() {}
    
    public Stock(Stock stock) {
        this.id = stock.id;
        this.totalSpace = stock.totalSpace;
        this.usedSpace = stock.usedSpace;
    }

    public int getFreeSpace() {
        return totalSpace - usedSpace;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getTotalSpace() {
        return totalSpace;
    }
    public void setTotalSpace(int totalSpace) {
        this.totalSpace = totalSpace;
    }
    public int getUsedSpace() {
        return usedSpace;
    }
    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }
}
