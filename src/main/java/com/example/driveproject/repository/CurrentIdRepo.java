package com.example.driveproject.repository;

import com.example.driveproject.entity.CurrentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentIdRepo extends JpaRepository<CurrentId, String> {
}
