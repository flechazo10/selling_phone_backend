package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cartdetail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDetail {

    @EmbeddedId
    private CartDetailId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "CartID_FK", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("versionId")
    @JoinColumn(name = "VersionID_FK", nullable = false)
    private Version version;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}
