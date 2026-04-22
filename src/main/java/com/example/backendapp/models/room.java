package com.example.backendapp.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "room")
@Data

public class room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                // 1. Название комнаты
    private Integer entryPrice;     // 2. Цена входа
    private Integer maxPlayers;    // 3. Макс. мест (до 10)
    private Double prizePoolPercentage; // 4. Процент призового фонда
    private Integer boostCost;          // 5. Стоимость буста
    private Integer timerSeconds;       // 6. Таймер до старта (в секундах)

    private String status;
    private LocalDateTime startTime;
}
