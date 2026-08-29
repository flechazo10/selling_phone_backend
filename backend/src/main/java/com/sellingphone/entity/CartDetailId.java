package com.sellingphone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailId implements Serializable {

    @Column(name = "CartID_FK")
    private Integer cartId;

    @Column(name = "VersionID_FK")
    private Integer versionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartDetailId that)) return false;
        return Objects.equals(cartId, that.cartId) &&
               Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, versionId);
    }
}
