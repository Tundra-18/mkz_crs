package com.example.sms.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdk.jfr.Enabled;

@Entity
public class MinMax {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private Double MaxPrice;
    private Double MinPrice;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Double getMaxPrice() {
        return MaxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        MaxPrice = maxPrice;
    }

    public Double getMinPrice() {
        return MinPrice;
    }

    public void setMinPrice(Double minPrice) {
        MinPrice = minPrice;
    }
}
