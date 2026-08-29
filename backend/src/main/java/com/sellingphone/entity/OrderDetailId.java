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
public class OrderDetailId implements Serializable {

    @Column(name = "OrderID_FK")
    private Integer orderId;

    @Column(name = "VersionID_FK")
    private Integer versionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDetailId that)) return false;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, versionId);
    }
}
