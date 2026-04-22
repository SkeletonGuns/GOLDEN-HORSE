package com.example.backendapp.controllers;


import com.example.backendapp.models.room;
import com.example.backendapp.repositories.roomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class adminController {
    private final roomRepository roomRepository;

    @PostMapping("/create-room")
    public room createRoom(@RequestBody room room) {
        room.setStatus("WAITING");
        room.setStartTime(LocalDateTime.now());

        // Валидация
        if (room.getMaxPlayers() == null || room.getMaxPlayers() > 10) {
            throw new RuntimeException("Ошибка: Максимальное количество игроков — 10");
        }

        if (room.getTimerSeconds() == null) {
            room.setTimerSeconds(60);
        }

        return roomRepository.save(room);
    }
}
