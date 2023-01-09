package com.example.driveproject.service;

import com.example.driveproject.entity.CurrentId;
import com.example.driveproject.repository.CurrentIdRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrentIdsService {

    private final CurrentIdRepo repo;

    /*
    * Узнаем по id с какого id скачивать нужный файл.
    * Если файл еще не скачивался, в базе еще нет информации о нем, выдаем исходный id
    * */
    public String getCurrentId(String id) {
        Optional<CurrentId> optionalCurrentId = repo.findById(id);
        if (optionalCurrentId.isPresent()) {
            return optionalCurrentId.get().getCurrentId();
        }
        return id;
    }

    public void setCurrentId(String id, String currentId) {
        repo.save(new CurrentId(id, currentId));
    }

    public void deleteRecord(String id) {
        repo.deleteById(id);
    }

}
