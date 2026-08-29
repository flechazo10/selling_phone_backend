package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BrandID_FK", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID_FK", nullable = false)
    private Category category;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "description")
    private String description;

    @Column(name = "Image")
    private String image;

    @Column(name = "status", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Byte status;

    // Relationships
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductSpecification specification;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Version> versions;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews;
}
