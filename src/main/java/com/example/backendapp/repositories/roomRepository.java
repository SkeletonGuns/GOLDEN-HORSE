package com.example.backendapp.repositories;

import com.example.backendapp.models.room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface roomRepository extends JpaRepository<room, Long>, JpaSpecificationExecutor<room>{
    List<room> findAllByStatus(String status);

}
