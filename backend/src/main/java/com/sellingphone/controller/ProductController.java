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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/product
     *
     * Các query param tuỳ chọn:
     *   keyword   – tìm theo tên sản phẩm
     *   brandId   – lọc theo thương hiệu
     *   minPrice  – giá tối thiểu
     *   maxPrice  – giá tối đa
     *   inStock   – true = chỉ hiện còn hàng
     *   page      – trang (default 0)
     *   size      – số phần tử / trang (default 10)
     *   sortBy    – "productId" | "productName" | "priceAsc" | "priceDesc" (default "productId")
     *   sortDir   – "asc" | "desc" (default "desc", chỉ áp dụng khi sortBy là productId/productName)
     *
     * Ví dụ:
     *   /api/product?keyword=iphone&brandId=1&minPrice=10000000&maxPrice=30000000&inStock=true&sortBy=priceAsc&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(required = false)              String     keyword,
            @RequestParam(required = false)              Integer    brandId,
            @RequestParam(required = false)              BigDecimal minPrice,
            @RequestParam(required = false)              BigDecimal maxPrice,
            @RequestParam(required = false)              Boolean    inStock,
            @RequestParam(defaultValue = "0")            int        page,
            @RequestParam(defaultValue = "10")           int        size,
            @RequestParam(defaultValue = "productId")    String     sortBy,
            @RequestParam(defaultValue = "desc")         String     sortDir) {

        Page<ProductResponse> products = productService.getAllProducts(
                keyword, brandId, minPrice, maxPrice, inStock,
                page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.ok("Lay danh sach san pham thanh cong", products));
    }

    // GET /api/product/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(@PathVariable Integer id) {
        ProductDetailResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Lay chi tiet san pham thanh cong", product));
    }
}