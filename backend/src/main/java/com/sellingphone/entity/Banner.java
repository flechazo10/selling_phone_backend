package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "isActive", columnDefinition = "BIT(1)")
    private Boolean isActive;

    @Column(name = "linkUrl", length = 1000)
    private String linkUrl;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
