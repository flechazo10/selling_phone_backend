package com.sellingphone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID_FK", nullable = false)
    private User user;

    @Column(name = "Total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "ReceiverName", nullable = false)
    private String receiverName;

    @Column(name = "PhoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "ShippingAddress", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
}
