package com.example.sms.controllers;

import com.example.sms.models.MinMax;
import com.example.sms.repositories.MinMaxRepository;
import com.example.sms.repositories.SupplierRepository;
import com.example.sms.services.MInMaxService;
import com.example.sms.services.SupplierService;
import com.example.sms.models.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    private MInMaxService mInMaxService;

    @Autowired
    private SupplierRepository supplierRepository;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;

    }


    @RequestMapping("/supplierlist")
    public String showSupplier(Model model) {
        List<Supplier> supplierList = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", supplierList);
        return "supplierlist"; // The name of the Thymeleaf template
    }


    @RequestMapping("/addsupplier")
    public String addSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "addsupplier"; // The name of the Thymeleaf template
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "login"; // The name of the Thymeleaf template
    }

    @RequestMapping("/forgot")
    public String forgot(Model model) {
        return "forgot"; // The name of the Thymeleaf template
    }

    // This handles form submission when login is clicked
    @PostMapping("/login")
    public String handleLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        return "home"; // The name of the Thymeleaf template
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier, Model model) {
        // Log the incoming supplier data for debugging
        System.out.println("SUPPLIER data to update: " + supplier);

        // Find the existing supplier by ID
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Check if a supplier with the same name and category already exists (excluding the current supplier)
        Optional<Supplier> duplicateSupplier = supplierRepository.findByNameAndProductCategory(supplier.getName(), supplier.getProductCategory());
        if (duplicateSupplier.isPresent() && !duplicateSupplier.get().getId().equals(id)) {
            model.addAttribute("errorMessage", "A supplier with the same name and category already exists.");
            model.addAttribute("supplier", existingSupplier);
            return "supplier-form"; // Replace with the correct view name for displaying the update form
        }

        // Update the existing supplier with new data
        existingSupplier.setName(supplier.getName());
        existingSupplier.setProductCategory(supplier.getProductCategory());
        existingSupplier.setProductQuality(supplier.getProductQuality());
        existingSupplier.setSupplierRating(supplier.getSupplierRating());
        existingSupplier.setMinPrice(supplier.getMinPrice());
        existingSupplier.setMaxPrice(supplier.getMaxPrice());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setSupplierAddress(supplier.getSupplierAddress());

        // Save the updated supplier
        supplierRepository.save(existingSupplier);

        // Redirect to the supplier list page after updating
        return "redirect:/suppliers"; // Adjust the redirect path as needed
    }




    @RequestMapping("/analysis")
    public String analysis(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers(); // Get all suppliers from the DB

        // Aggregate data for productCategory and productQuality
        Map<String, Long> categoryCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getProductCategory, Collectors.counting()));
        Map<String, Long> qualityCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getProductQuality, Collectors.counting()));
        Map<Double, Long> ratingCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getSupplierRating, Collectors.counting()));



        // Add data to the models
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("qualityCount", qualityCount);
        model.addAttribute("ratingCount", ratingCount);

        return "analysis"; // The name of the Thymeleaf template
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    @RequestMapping("/aboutus")
    public String aboutus(Model model) {
        return "aboutus"; // The name of the Thymeleaf template
    }

    @RequestMapping("/contact")
    public String contact(Model model) {
        return "contact"; // The name of the Thymeleaf template
    }

    @RequestMapping("/submitSupplier")
    public String submitForm(@ModelAttribute("supplier") Supplier supplier) {
        supplierService.addSupplier(supplier);
        return "addsupplier";
    }

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

    @GetMapping("/product/min-price")
    public Double getMinPriceRange() {
        return supplierService.getMinPriceRange();
    }

    @GetMapping("/product/max-price")
    public Double getMaxPriceRange() {
        return supplierService.getMaxPriceRange();
    }

    @RequestMapping("/report")
    public String report(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers(); // Get all suppliers from the DB

        // Check if there are no suppliers
        if (suppliers.isEmpty()) {
            return "report"; // Return the template without adding any supplier-related attributes
        }

        // Normalize prices and calculate scores for each supplier
        MinMaxRepository minMaxProjection = supplierService.getMinAndMaxValues();
        MinMax minMax = new MinMax();
        minMax.setMaxPrice(minMaxProjection.getMaxValue());
        minMax.setMinPrice(minMaxProjection.getMinValue());

        Long currentId = mInMaxService.save(minMax);
        System.out.println("currentId : " + currentId);

        for (Supplier s : suppliers) {
            s.setNormalizePrice(
                    s.minMaxNormalizePrice(
                            s.getPriceRange(),
                            minMaxProjection.getMinValue(),
                            minMaxProjection.getMaxValue()));
            double result = round(s.getNormalizedQuality() * 0.4 + s.getNormalizedRating() * 0.3 + s.getNormalizePrice() * 0.3, 2);
            s.setScore(result);
            supplierRepository.save(s);
        }

        // Group suppliers by category
        Map<String, List<Supplier>> suppliersByCategory = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getProductCategory));

        // Sort suppliers within each category by score (descending) and limit to top 5
        Map<String, List<Supplier>> topFiveSuppliersByCategory = new HashMap<>();
        for (Map.Entry<String, List<Supplier>> entry : suppliersByCategory.entrySet()) {
            List<Supplier> sortedSuppliers = entry.getValue().stream()
                    .sorted((s1, s2) -> Double.compare(s2.getScore(), s1.getScore())) // Sort by score descending
                    .limit(5) // Limit to top 5
                    .collect(Collectors.toList());
            topFiveSuppliersByCategory.put(entry.getKey(), sortedSuppliers);
        }

        // Add the top five suppliers by category to the model
        model.addAttribute("topFiveSuppliersByCategory", topFiveSuppliersByCategory);

        // Add min and max prices to the model
        model.addAttribute("minPrice", minMaxProjection.getMinValue());
        model.addAttribute("maxPrice", minMaxProjection.getMaxValue());

        return "report"; // The name of the Thymeleaf template
    }

    @RequestMapping("/analysis1")
    public String analysis1(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers(); // Get all suppliers from the DB

        // Aggregate data for productCategory and productQuality
        Map<String, Long> categoryCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getProductCategory, Collectors.counting()));
        Map<String, Long> qualityCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getProductQuality, Collectors.counting()));
        Map<Double, Long> ratingCount = suppliers.stream()
                .collect(Collectors.groupingBy(Supplier::getSupplierRating, Collectors.counting()));

        // Add data to the models
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("qualityCount", qualityCount);
        model.addAttribute("ratingCount", ratingCount);

        return "analysis1"; // The name of the Thymeleaf template
    }



}

