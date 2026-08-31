package com.sellingphone.controller;

import com.sellingphone.dto.response.ApiResponse;
import com.sellingphone.dto.response.ProductDetailResponse;
import com.sellingphone.dto.response.ProductResponse;
import com.sellingphone.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/product?page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<ProductResponse> products = productService.getAllProducts(page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach san pham thanh cong", products));
    }

    // GET /api/product/search?keyword=iphone&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> products = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Tim kiem san pham thanh cong", products));
    }

    // GET /api/product/category/1?page=0&size=10
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> products = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Lay san pham theo danh muc thanh cong", products));
    }

    // GET /api/product/brand/1?page=0&size=10
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByBrand(
            @PathVariable Integer brandId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductResponse> products = productService.getProductsByBrand(brandId, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Lay san pham theo thuong hieu thanh cong", products));
    }

    // GET /api/product/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Integer id) {
        ProductDetailResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lay chi tiet san pham thanh cong", product));
    }
}