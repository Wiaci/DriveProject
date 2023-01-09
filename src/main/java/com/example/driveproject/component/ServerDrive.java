package com.example.driveproject.component;

import com.example.driveproject.service.DriveService;
import com.example.driveproject.service.GoogleService;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/*
* Бин для работы с диском сервера
* */
@Component
@RequiredArgsConstructor
public class ServerDrive {

    private Drive drive;
    private final DriveService driveService;
    private final String SERVER_NAME = "SERVER";
    private final GoogleService googleService;

    @PostConstruct
    private void init() {
        try {
            drive = googleService.getServerDrive(SERVER_NAME);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void shareFile(String id) throws IOException {
        driveService.shareFile(id, drive);
    }

    public List<File> getFiles() throws IOException {
        return driveService.getFileList(drive);
    }


}
