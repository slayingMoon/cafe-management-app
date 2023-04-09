package com.example.cafebackend.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String name;

    private String email;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    private BigDecimal total;

    @Column(name = "product_details", columnDefinition = "json")
    private String productDetails;

    @Column(name = "created_by")
    private String createdBy;
}
