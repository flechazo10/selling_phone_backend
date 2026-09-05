package com.sellingphone.repository;

import com.sellingphone.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // ---------------------------------------------------------------
    // Query tổng hợp: filter keyword, brandId, minPrice, maxPrice,
    // inStock – sắp xếp theo productId / productName (Pageable Sort).
    // ---------------------------------------------------------------
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN p.brand b " +
                   "LEFT JOIN p.versions v " +
                   "WHERE p.status = 1 " +
                   "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                   "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                   "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                   "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0))",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "LEFT JOIN p.brand b " +
                        "LEFT JOIN p.versions v " +
                        "WHERE p.status = 1 " +
                        "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                        "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                        "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0))")
    Page<Product> findAllWithFilters(
            @Param("keyword")  String     keyword,
            @Param("brandId")  Integer    brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock")  Boolean    inStock,
            Pageable pageable);

    // ---------------------------------------------------------------
    // Sort theo giá tăng dần – ORDER BY hardcode vì MIN(v.price)
    // không dùng được với Pageable Sort.
    // ---------------------------------------------------------------
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN p.brand b " +
                   "LEFT JOIN p.versions v " +
                   "WHERE p.status = 1 " +
                   "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                   "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                   "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                   "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0)) " +
                   "GROUP BY p " +
                   "ORDER BY MIN(v.price) ASC",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "LEFT JOIN p.brand b " +
                        "LEFT JOIN p.versions v " +
                        "WHERE p.status = 1 " +
                        "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                        "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                        "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0))")
    Page<Product> findAllWithFiltersSortByPriceAsc(
            @Param("keyword")  String     keyword,
            @Param("brandId")  Integer    brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock")  Boolean    inStock,
            Pageable pageable);

    // ---------------------------------------------------------------
    // Sort theo giá giảm dần
    // ---------------------------------------------------------------
    @Query(value = "SELECT DISTINCT p FROM Product p " +
                   "LEFT JOIN p.brand b " +
                   "LEFT JOIN p.versions v " +
                   "WHERE p.status = 1 " +
                   "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                   "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                   "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                   "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0)) " +
                   "GROUP BY p " +
                   "ORDER BY MIN(v.price) DESC",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                        "LEFT JOIN p.brand b " +
                        "LEFT JOIN p.versions v " +
                        "WHERE p.status = 1 " +
                        "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:brandId IS NULL OR b.brandId = :brandId) " +
                        "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
                        "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0))")
    Page<Product> findAllWithFiltersSortByPriceDesc(
            @Param("keyword")  String     keyword,
            @Param("brandId")  Integer    brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock")  Boolean    inStock,
            Pageable pageable);

    // Lấy chi tiết 1 sản phẩm (eager load brand, category, versions)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.category LEFT JOIN FETCH p.versions WHERE p.productId = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Integer id);
}