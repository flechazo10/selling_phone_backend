package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_specification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecification {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "screen_size")
    private String screenSize;

    @Column(name = "screen_tech")
    private String screenTech;

    @Column(name = "rear_camera", columnDefinition = "TEXT")
    private String rearCamera;

    @Column(name = "front_camera")
    private String frontCamera;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "ram")
    private String ram;

    @Column(name = "rom")
    private String rom;

    @Column(name = "battery")
    private String battery;

    @Column(name = "os")
    private String os;

    @Column(name = "screen_features", columnDefinition = "TEXT")
    private String screenFeatures;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
