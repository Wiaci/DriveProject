package com.example.driveproject.service;

import com.example.driveproject.CurrentId;
import com.example.driveproject.CurrentIdRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentIdsService {

    private final CurrentIdRepo repo;

    public String getCurrentId(String id) {
        Optional<CurrentId> optionalCurrentId = repo.findById(id);
        if (optionalCurrentId.isPresent()) {
            return optionalCurrentId.get().getCurrentId();
        }
        return id;
    }

    public void setCurrentId(String id, String currentId) {
        CurrentId c = new CurrentId(id, currentId);
        repo.save(c);
    }

    public void deleteRecord(String id) {
        repo.deleteById(id);
    }

}
