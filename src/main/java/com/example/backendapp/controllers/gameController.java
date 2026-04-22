package com.example.backendapp.controllers;

import com.example.backendapp.models.Participation;
import com.example.backendapp.models.room;
import com.example.backendapp.models.user;
import com.example.backendapp.repositories.participationRepository;
import com.example.backendapp.repositories.roomRepository;
import com.example.backendapp.repositories.userRepository;
import com.example.backendapp.services.gameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor

public class gameController {
    private final userRepository userRepository;
    private final gameService gameService;
    private final roomRepository roomRepository;
    private final participationRepository participationRepository;

    @GetMapping("/rooms")
    public List<room> getAllRooms() {
        return roomRepository.findAllByStatus("WAITING");
    }

    @GetMapping("/user/{id}")
    public user getUserInfo(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("/join")
    public void joinRoom(@RequestParam Long userId,
                         @RequestParam Long roomId,
                         @RequestParam Integer prediction) {
        gameService.joinRoom(userId, roomId, prediction);
    }

    @PostMapping("/buy-boost")
    public ResponseEntity<?> buyBoost(@RequestParam Long userId, @RequestParam Long roomId) {
        try {
            gameService.activateBoost(userId, roomId);
            return ResponseEntity.ok("Буст успешно активирован");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public List<Participation> getUserHistory(@PathVariable Long userId) {
        return participationRepository.findAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<room> searchRooms(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false) Integer maxSeats,
            @RequestParam(required = false) Double minPrize
    ) {
        return roomRepository.findAll((Specification<room>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), "WAITING"));

            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("entryPrice"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("entryPrice"), maxPrice));

            if (minSeats != null) predicates.add(cb.greaterThanOrEqualTo(root.get("maxPlayers"), minSeats));
            if (maxSeats != null) predicates.add(cb.lessThanOrEqualTo(root.get("maxPlayers"), maxSeats));

            if (minPrize != null && minPrize > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("prizePoolPercentage"), minPrize));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
