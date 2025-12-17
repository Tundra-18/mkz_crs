package com.example.sms.models;

import jakarta.persistence.*;

@Entity
@Table
public class Supplier {


    @Id
    @SequenceGenerator(
            name = "supplier_sequence",
            sequenceName = "supplier_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "supplier_sequence"
    )
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String supplierAddress;
    private String productCategory;
    private String productQuality;

    private Double supplierRating;

    private Double minPrice;
    private Double maxPrice;

    private Double normalizedQuality; // Normalized value for quality

    @Column(nullable = true) // nullable = true, if price range is optional
    private Double priceRange;
    private Double normalizedRating;
    private Double normalizePrice;
    private Double score;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setNormalizedRating(Double normalizedRating) {
        this.normalizedRating = normalizedRating;
    }

    public Double getNormalizePrice() {
        return normalizePrice;
    }

    public void setNormalizePrice(Double normalizePrice) {
        this.normalizePrice = normalizePrice;
    }

    //    no argument constructor
    public Supplier() {

    }

    public Supplier(long id,
                    String productCategory,
                    String productQuality,
                    String name,
                    Double rating,
                    Double minimumPricing,
                    Double maximumPricing,
                    String email,
                    String phone,
                    String address) {
        this.id = id;
        this.productCategory = productCategory;
        this.productQuality = productQuality;
        this.name = name;
        this.supplierRating = rating;
        this.minPrice = minimumPricing;
        this.maxPrice = maximumPricing;
        this.email = email;
        this.phone = phone;
        this.supplierAddress = address;
        normalizeProductQuality();
    }

    public Supplier(String productCategory,
                    String productQuality,
                    String name,
                    Double rating,
                    Double minimumPricing,
                    Double maximumPricing,
                    String email,
                    String phone,
                    String address) {
        this.productCategory = productCategory;
        this.productQuality = productQuality;
        this.name = name;
        this.supplierRating = rating;
        this.minPrice = minimumPricing;
        this.maxPrice = maximumPricing;
        this.email = email;
        this.phone = phone;
        this.supplierAddress = address;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String address) {
        this.supplierAddress = address;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuality() {
        return productQuality;
    }

    public void setProductQuality(String productQuality) {
        this.productQuality = productQuality;
        normalizeProductQuality();
    }

    public Double getSupplierRating() {
        return supplierRating;
    }

    public void setSupplierRating(Double rating) {
        this.supplierRating = rating;
        updateNormalizedRating(); // Recalculate normalized supplierRating whenever supplierRating is set
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
        updatePriceRange();
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
        updatePriceRange();
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", productQuality='" + productQuality + '\'' +
                ", normalizedQuality=" + normalizedQuality +
                ", supplierRating=" + supplierRating +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", supplierAddress='" + supplierAddress + '\'' +
                '}';
    }

    public Double getNormalizedQuality() {
        return normalizedQuality;
    }

    public void setNormalizedQuality(Double normalizedQuality) {
        this.normalizedQuality = normalizedQuality;
    }


    // Method to normalize the product quality
    public void normalizeProductQuality() {
        int qualityValue = mapQualityToNumeric(this.productQuality);
        this.normalizedQuality = minMaxNormalize(qualityValue, 0, 4); // Normalize between 0 and 4
    }

    // Map quality text to numeric value
    private int mapQualityToNumeric(String quality) {
        return switch (quality.toLowerCase()) {
            case "very low" -> 0;
            case "low" -> 1;
            case "medium" -> 2;
            case "high" -> 3;
            case "high end" -> 4;
            default -> -1; // Invalid quality
        };
    }

    // Min-Max normalization formula
    public double minMaxNormalize(double value, double min, double max) {
        double normalizedValue = (value - min) / (max - min);
        return (double) Math.round(normalizedValue * 100) / 100;
    }

    public double minMaxNormalizePrice(double value, double min, double max) {
        double normalizedValue = 1 - (value - min) / (max - min);
        return (double) Math.round(normalizedValue * 100) / 100;
    }

    // Method to calculate price range
    public Double getPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return maxPrice - minPrice;
        }
        return 0.0;
    }

    // You can update the priceRange field whenever minPrice or maxPrice changes
    public void updatePriceRange() {
        this.priceRange = getPriceRange();
    }

    public Double getNormalizedRating() {
        if (supplierRating != null && supplierRating >= 0.1 && supplierRating <= 10.0) {
            this.normalizedRating = minMaxNormalize(supplierRating, 0.1, 10.0); // Normalize between 0 and 1
        } else {
            this.normalizedRating = null;  // If supplierRating is invalid, set normalizedRating to null or a default value.
        }
        return normalizedRating;
    }

    @PrePersist
    @PreUpdate
    public void updateNormalizedRating() {
        this.normalizedRating = getNormalizedRating();
    }


//    public Double getMinPrice(MinMaxProjection minMaxProjection){
//        return minMaxProjection != null ? minMaxProjection.getMinValue() : null;
//    }
//
//
//    public Double getMaxPrice(MinMaxProjection minMaxProjection){
//        return minMaxProjection != null ? minMaxProjection.getMinValue() : null;
//    }


}

