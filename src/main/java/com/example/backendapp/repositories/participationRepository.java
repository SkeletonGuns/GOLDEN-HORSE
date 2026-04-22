package com.example.backendapp.repositories;

import com.example.backendapp.models.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface participationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByUserIdAndRoomId(Long userId, Long roomId);
    List<Participation> findAllByRoomId(Long roomId);
    List<Participation> findAllByUserId(Long userId);
}
