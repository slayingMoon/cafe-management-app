package com.example.cafebackend.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data //generates getters, setters and constructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String contactNumber;

    @Column(unique = true, nullable = false)
    private String email; //email will be used as username

    @Column(nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private String isVerified;

    private String role;

}
