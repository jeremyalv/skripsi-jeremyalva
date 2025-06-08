package com.jeremyalv.flow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeremyalv.flow.dto.product.request.CreateProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.DeleteProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.EditProductRequestDTO;
import com.jeremyalv.flow.dto.product.request.GetProductRequestDTO;
import com.jeremyalv.flow.dto.product.response.CreateProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.EditProductResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetAllProductsResponseDTO;
import com.jeremyalv.flow.dto.product.response.GetProductResponseDTO;
import com.jeremyalv.flow.model.Product;
import com.jeremyalv.flow.model.User;
import com.jeremyalv.flow.service.product.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts() {
        GetAllProductsResponseDTO response = productService.getProducts();

        return ResponseEntity.ok(response.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = null;
        if (userDetails != null) {
            if (userDetails instanceof User) {
                userId = ((User) userDetails).getId();
            }
        }
        GetProductRequestDTO request = GetProductRequestDTO.builder()
                .id(id)
                .userId(userId)
                .build();
        
        GetProductResponseDTO response = productService.getProductDetail(request);

        return ResponseEntity.ok(response.getProduct());
    }

    @PostMapping("/")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid CreateProductRequestDTO request) {
        CreateProductResponseDTO response = productService.createProduct(request);

        return new ResponseEntity<>(response.getProduct(), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> editProduct(@PathVariable Long id, @RequestBody @Valid EditProductRequestDTO request) {
        request.setId(id);

        EditProductResponseDTO response = productService.editProduct(request);

        return ResponseEntity.ok(response.getProduct());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        DeleteProductRequestDTO request = DeleteProductRequestDTO.builder().id(id).build();

        productService.deleteProduct(request);

        return ResponseEntity.noContent().build();
    }
}
