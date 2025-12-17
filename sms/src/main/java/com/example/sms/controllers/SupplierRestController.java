package com.example.sms.controllers;

import com.example.sms.models.Supplier;
import com.example.sms.services.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierRestController {

    private final SupplierService supplierService;

    public SupplierRestController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/")
    public List<Supplier> getSuppliers(){
        return supplierService.getAllSuppliers();
    }

    @PostMapping("/submitSupplier")
    public ResponseEntity<?> addSupplier(@RequestBody Supplier supplier) {
        try {
            Supplier savedSupplier = supplierService.addSupplier(supplier);
            return ResponseEntity.ok(savedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
//
//    @DeleteMapping(path = "{supplierId}")
//    public void deleteSupplier(@PathVariable("supplierId") Long supplierId) {
//        supplierService.deleteSupplier(supplierId);
//    }

    // Endpoint to create a new supplier
    // Endpoint to create a new supplier
    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    // Endpoint to fetch all suppliers
    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }


}
