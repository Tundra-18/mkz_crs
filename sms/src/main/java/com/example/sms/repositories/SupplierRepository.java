package com.example.sms.repositories;

import com.example.sms.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long>
{
    @Query("SELECT MIN(s.priceRange) FROM Supplier s")
    Double findMinPriceRange();

    @Query("SELECT MAX(s.priceRange) FROM Supplier s")
    Double findMaxPriceRange();

    @Query(value = "SELECT MIN(f.price_range) AS min_value, MAX(f.price_range) AS max_value FROM supplier f", nativeQuery = true)
    MinMaxRepository findMinAndMaxValues();

    Optional<Supplier> findByNameAndProductCategory(String name, String productCategory);

}
