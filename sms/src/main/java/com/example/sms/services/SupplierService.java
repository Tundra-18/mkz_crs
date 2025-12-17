package com.example.sms.services;

import com.example.sms.models.Supplier;
import com.example.sms.repositories.MinMaxRepository;
import com.example.sms.repositories.SupplierRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier addSupplier(Supplier supplier) {
        // Check if a supplier with the same name and category already exists
        Optional<Supplier> existingSupplier = supplierRepository.findByNameAndProductCategory(
                supplier.getName(), supplier.getProductCategory());

        if (existingSupplier.isPresent()) {
            throw new IllegalArgumentException("A supplier with the same name and category already exists.");
        }

        // Save the new supplier if no duplicate is found
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    public void importCsv(MultipartFile file) throws Exception {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            List<Supplier> suppliers = new ArrayList<>();
            csvReader.readNext(); // Skip the header row
            while ((line = csvReader.readNext()) != null) {
                Supplier record = new Supplier();
                record.setProductCategory(line[0]);
                record.setName(line[1]);
                record.setSupplierRating(Double.valueOf(line[2]));
                record.setProductQuality(line[3]);
                record.setMinPrice(Double.valueOf(line[4]));
                record.setMaxPrice(Double.valueOf(line[5]));
                record.setEmail(line[6]);
                record.setPhone(line[7]);
                record.setSupplierAddress(line[8]);
                // Explicitly update normalizedRating
                record.updateNormalizedRating(); // Ensure normalizedRating is calculated
                suppliers.add(record);
            }
            supplierRepository.saveAll(suppliers);
        }
    }

    public Supplier createSupplier(Supplier supplier) {
        // Normalize the quality before saving
        supplier.normalizeProductQuality();
        supplier.updateNormalizedRating();   // Updates the normalized rating
        return supplierRepository.save(supplier);  // Now, the normalizedRating will be persisted
    }

    public Double getMinPriceRange() {
        return supplierRepository.findMinPriceRange();
    }

    public Double getMaxPriceRange() {
        return supplierRepository.findMaxPriceRange();
    }

    public MinMaxRepository getMinAndMaxValues() {
        return supplierRepository.findMinAndMaxValues();
    }

    public Map<Double, Long> getRatingCount(List<Supplier> suppliers) {
        // Group ratings and count occurrences
        Map<Double, Long> ratingCount = suppliers.stream()
                .collect(Collectors.groupingBy(
                        supplier -> Double.parseDouble(String.format("%.1f", supplier.getSupplierRating())), // Format to 1 decimal place
                        Collectors.counting()
                ));
        // Ensure all ratings from 0.1 to 10.0 are included
        for (double rating = 0.1; rating <= 10.0; rating += 0.1) {
            ratingCount.putIfAbsent(rating, 0L); // Add rating with count 0 if not present
        }

        // Sort the map by rating
        return new TreeMap<>(ratingCount);
    }

}


