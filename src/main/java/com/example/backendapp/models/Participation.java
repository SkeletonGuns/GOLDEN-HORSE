package com.example.backendapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "participations")
@Data

public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private user user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private room room;

    @Column(name = "predicted_number")
    private Integer predictedNumber;
    private boolean hasBoost;
    private Integer winAmount;
    private boolean isFinished;
}
