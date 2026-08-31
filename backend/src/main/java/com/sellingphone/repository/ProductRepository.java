package com.sellingphone.repository;

import com.sellingphone.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Lay danh sach san pham con hoat dong (status = 1), co phan trang
    Page<Product> findByStatus(Byte status, Pageable pageable);

    // Lay danh sach san pham, sap xep theo gia thap nhat cua cac version tang dan
    @Query(value = "SELECT p FROM Product p LEFT JOIN p.versions v WHERE p.status = 1 GROUP BY p ORDER BY MIN(v.price) ASC",
           countQuery = "SELECT count(p) FROM Product p WHERE p.status = 1")
    Page<Product> findAllActiveSortByMinPriceAsc(Pageable pageable);

    // Lay danh sach san pham, sap xep theo gia thap nhat cua cac version giam dan
    @Query(value = "SELECT p FROM Product p LEFT JOIN p.versions v WHERE p.status = 1 GROUP BY p ORDER BY MIN(v.price) DESC",
           countQuery = "SELECT count(p) FROM Product p WHERE p.status = 1")
    Page<Product> findAllActiveSortByMinPriceDesc(Pageable pageable);


    // Loc theo category
    Page<Product> findByStatusAndCategory_CategoryId(Byte status, Integer categoryId, Pageable pageable);

    // Loc theo brand
    Page<Product> findByStatusAndBrand_BrandId(Byte status, Integer brandId, Pageable pageable);

    // Tim kiem theo ten san pham
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);

    // Lay chi tiet 1 san pham (eager load brand, category, versions)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.category LEFT JOIN FETCH p.versions WHERE p.productId = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Integer id);
}