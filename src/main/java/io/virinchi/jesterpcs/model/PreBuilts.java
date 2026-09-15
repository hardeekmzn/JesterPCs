package io.virinchi.jesterpcs.model;

import java.util.List;

public class PreBuilts {

    private int prebuiltId;
    private String key;
    private String name;
    private String title;
    private String description;
    private String targetResolution;
    private String performance;
    private String bestFor;
    private String imageUrl;
    private List<Product> products;

    public PreBuilts() {
    }

    public PreBuilts(
            int prebuiltId,
            String key,
            String name,
            String title,
            String description,
            String targetResolution,
            String performance,
            String bestFor,
            String imageUrl,
            List<Product> products) {

        this.prebuiltId = prebuiltId;
        this.key = key;
        this.name = name;
        this.title = title;
        this.description = description;
        this.targetResolution = targetResolution;
        this.performance = performance;
        this.bestFor = bestFor;
        this.imageUrl = imageUrl;
        this.products = products;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPrebuiltId() {
        return prebuiltId;
    }

    public void setPrebuiltId(int prebuiltId) {
        this.prebuiltId = prebuiltId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetResolution() {
        return targetResolution;
    }

    public void setTargetResolution(String targetResolution) {
        this.targetResolution = targetResolution;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getBestFor() {
        return bestFor;
    }

    public void setBestFor(String bestFor) {
        this.bestFor = bestFor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotal() {

        if (products == null) {
            return 0;
        }

        return products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }
}