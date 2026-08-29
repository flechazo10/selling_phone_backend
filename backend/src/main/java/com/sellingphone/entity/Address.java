package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressID")
    private Integer addressId;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "is_default", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Byte isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID_FK", nullable = false)
    private User user;
}
