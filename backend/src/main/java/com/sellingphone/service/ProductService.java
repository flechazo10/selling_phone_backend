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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper     productMapper;

    /**
     * Lấy danh sách sản phẩm với đầy đủ bộ lọc và sắp xếp.
     *
     * @param keyword  tìm theo tên (nullable)
     * @param brandId  lọc theo thương hiệu (nullable)
     * @param minPrice giá tối thiểu (nullable)
     * @param maxPrice giá tối đa (nullable)
     * @param inStock  chỉ lấy còn hàng (nullable = lấy tất cả)
     * @param page     trang hiện tại (0-indexed)
     * @param size     số phần tử mỗi trang
     * @param sortBy   trường sắp xếp: "productId" | "productName" | "priceAsc" | "priceDesc"
     * @param sortDir  chiều sắp xếp: "asc" | "desc" (chỉ áp dụng khi sortBy không phải giá)
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(
            String keyword,
            Integer brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        // Chuẩn hoá keyword rỗng thành null để query JPQL xử lý đúng
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        Page<Product> products;

        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            // Sort theo giá tăng dần – ORDER BY hardcode trong JPQL
            Pageable pageable = PageRequest.of(page, size);
            products = productRepository.findAllWithFiltersSortByPriceAsc(
                    keyword, brandId, minPrice, maxPrice, inStock, pageable);

        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            // Sort theo giá giảm dần – ORDER BY hardcode trong JPQL
            Pageable pageable = PageRequest.of(page, size);
            products = productRepository.findAllWithFiltersSortByPriceDesc(
                    keyword, brandId, minPrice, maxPrice, inStock, pageable);

        } else {
            // Sort theo các trường entity bình thường (productId, productName...)
            // Mặc định: productId giảm dần (sản phẩm mới nhất trước)
            String validSortBy = isValidSortField(sortBy) ? sortBy : "productId";
            Sort sort = "asc".equalsIgnoreCase(sortDir)
                    ? Sort.by(validSortBy).ascending()
                    : Sort.by(validSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            products = productRepository.findAllWithFilters(
                    keyword, brandId, minPrice, maxPrice, inStock, pageable);
        }

        return products.map(productMapper::toProductResponse);
    }

    /**
     * Chỉ cho phép sort theo các trường hợp lệ của entity Product
     * để tránh PropertyReferenceException.
     */
    private boolean isValidSortField(String sortBy) {
        return sortBy != null && (
                sortBy.equals("productId") ||
                sortBy.equals("productName")
        );
    }

    // Lấy chi tiết 1 sản phẩm
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Integer id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductDetailResponse(product);
    }
}