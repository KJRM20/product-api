package com.practice.functional.practice_functional.controllers;

import com.practice.functional.practice_functional.dto.ProductDTO;
import com.practice.functional.practice_functional.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("")
  public List<ProductDTO> getProducts() {
    return productService.getAllProducts();
  }

  @PostMapping("")
  public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {
    ProductDTO product = productService.addProduct(productDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
  }

  @GetMapping("/sorted")
  public List<ProductDTO> getSortedProducts() {
    return productService.getSortedProducts();
  }

  @GetMapping("/recent")
  public List<ProductDTO> getRecentProducts(@RequestParam(defaultValue = "5") int limit) {
    return productService.getRecentProducts(limit);
  }

  @GetMapping("/modified")
  public List<ProductDTO> getModifiedProducts(@RequestParam(defaultValue = "5") int limit) {
    return productService.getModifiedProducts(limit);
  }

  @GetMapping("/groupByPrice")
  public Map<Double, List<ProductDTO>> groupByPrice() {
    return productService.groupProductsByPrice();
  }

  @GetMapping("/minMax")
  public Map<String, ProductDTO> getMinMaxPriceProducts() {
    return productService.getMinMaxPriceProducts();
  }
}
