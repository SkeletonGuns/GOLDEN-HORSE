package com.example.backendapp.services;


import com.example.backendapp.models.room;
import com.example.backendapp.repositories.roomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor

public class schedulerService {
    private final roomRepository roomRepository;
    private final gameService gameService;

    @Scheduled(fixedDelay = 1000)
    public void checkRooms() {
        List<room> activeRooms = roomRepository.findAllByStatus("WAITING");
        for (room room : activeRooms) {
            if (room.getStartTime().plusSeconds(room.getTimerSeconds()).isBefore(LocalDateTime.now())) {
                gameService.finishRound(room.getId());
            }
        }
    }
}
