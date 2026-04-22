package com.example.backendapp.repositories;

import com.example.backendapp.models.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<user, Long> {
}
