package com.sellingphone.service;

import com.sellingphone.dto.response.ProductDetailResponse;
import com.sellingphone.dto.response.ProductResponse;
import com.sellingphone.entity.Product;
import com.sellingphone.exception.AppException;
import com.sellingphone.exception.ErrorCode;
import com.sellingphone.mapper.ProductMapper;
import com.sellingphone.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper     productMapper;

    // Lay danh sach san pham (chi hien thi status = 1)
    public Page<ProductResponse> getAllProducts(int page, int size, String sortBy) {
        Page<Product> products;
        
        if ("price_asc".equalsIgnoreCase(sortBy) || "price".equalsIgnoreCase(sortBy)) {
            // Giá tăng dần (minPrice ASC)
            Pageable pageable = PageRequest.of(page, size);
            products = productRepository.findAllActiveSortByMinPriceAsc(pageable);
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            // Giá giảm dần (minPrice DESC)
            Pageable pageable = PageRequest.of(page, size);
            products = productRepository.findAllActiveSortByMinPriceDesc(pageable);
        } else {
            // Mặc định sort theo productId giảm dần (mới nhất)
            Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
            products = productRepository.findByStatus((byte) 1, pageable);
        }
        
        return products.map(productMapper::toProductResponse);
    }

    // Tim kiem theo ten
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchByName(keyword, pageable)
                .map(productMapper::toProductResponse);
    }

    // Loc theo category
    public Page<ProductResponse> getProductsByCategory(Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        return productRepository.findByStatusAndCategory_CategoryId((byte) 1, categoryId, pageable)
                .map(productMapper::toProductResponse);
    }

    // Loc theo brand
    public Page<ProductResponse> getProductsByBrand(Integer brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").descending());
        return productRepository.findByStatusAndBrand_BrandId((byte) 1, brandId, pageable)
                .map(productMapper::toProductResponse);
    }

    // Lay chi tiet 1 san pham
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Integer id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductDetailResponse(product);
    }
}