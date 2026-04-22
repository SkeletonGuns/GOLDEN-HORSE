package com.example.backendapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data

public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long bonusBalance;
    private Long reservedBalance;
    private boolean isVip;
}
