package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VersionID")
    private Integer versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductID_FK", nullable = false)
    private Product product;

    @Column(name = "colour", nullable = false)
    private String colour;

    @Column(name = "storage", nullable = false)
    private String storage;

    @Column(name = "material")
    private String material;

    @Column(name = "Price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "Stock", nullable = false)
    private Integer stock;

    @Column(name = "ImageURL")
    private String imageUrl;

    // Relationships
    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY)
    private List<CartDetail> cartDetails;

    @OneToMany(mappedBy = "version", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
}
