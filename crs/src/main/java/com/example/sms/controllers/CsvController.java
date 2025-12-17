package com.example.sms.controllers;

import com.example.sms.models.Supplier;
import com.example.sms.repositories.SupplierRepository;
import com.example.sms.services.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class CsvController {


    private final SupplierRepository supplierRepository;
    private final CSVService csvService;

    public CsvController(SupplierRepository supplierRepository, CSVService csvService) {
        this.supplierRepository = supplierRepository;
        this.csvService = csvService;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Process the CSV file (parsing, saving to DB, etc.)
            List<Supplier> supplierList = csvService.readCsvFile(file);
            supplierRepository.saveAll(supplierList);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the file: " + e.getMessage());
        }
    }
}
