package io.virinchi.jesterpcs.model;

public class Product {

    private int productId;
    private int categoryId;
    private String productName;
    private String brand;
    private double price;
    private String description;
    private String specifications;
    private String imageUrl;
    private int stockQuantity;
    private String dateAdded;

    private String socketType;
    private String ramType;
    private Integer ramSpeed;
    private String storageInterface;
    private Integer wattage;
    private Double gpuLength;
    private Double coolerHeight;
    private String performanceTier;

    private String categoryName;

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public String getRamType() {
        return ramType;
    }

    public void setRamType(String ramType) {
        this.ramType = ramType;
    }

    public Integer getRamSpeed() {
        return ramSpeed;
    }

    public void setRamSpeed(Integer ramSpeed) {
        this.ramSpeed = ramSpeed;
    }

    public String getStorageInterface() {
        return storageInterface;
    }

    public void setStorageInterface(String storageInterface) {
        this.storageInterface = storageInterface;
    }

    public Integer getWattage() {
        return wattage;
    }

    public void setWattage(Integer wattage) {
        this.wattage = wattage;
    }

    public Double getGpuLength() {
        return gpuLength;
    }

    public void setGpuLength(Double gpuLength) {
        this.gpuLength = gpuLength;
    }

    public Double getCoolerHeight() {
        return coolerHeight;
    }

    public void setCoolerHeight(Double coolerHeight) {
        this.coolerHeight = coolerHeight;
    }

    public String getPerformanceTier() {
        return performanceTier;
    }

    public void setPerformanceTier(String performanceTier) {
        this.performanceTier = performanceTier;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}