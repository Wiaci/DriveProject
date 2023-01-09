package com.example.driveproject.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DriveService {
    public List<File> getFileList(Drive drive) throws IOException {
        if (drive != null) {
            FileList result = drive.files().list()
                    .setQ("'me' in owners")
                    .setFields("files(id, name, mimeType)")
                    .execute();

            return result.getFiles();
        }
        return new ArrayList<>();
    }

    public String copyFile(String fileId, Drive destinationDrive) throws IOException {
        String newId = destinationDrive.files().copy(fileId, new File())
                .execute().getId();
        shareFile(newId, destinationDrive);
        return newId;
    }

    public void downloadFile(String fileId, Drive drive, OutputStream stream) throws IOException {
        drive.files().get(fileId).executeMediaAndDownloadTo(stream);
    }

    public void shareFile(String id, Drive drive) throws IOException {
        Permission permission = new Permission();
        permission.setRole("reader");
        permission.setType("anyone");
        drive.permissions().create(id, permission).execute();
    }

    public boolean fileExists(String id, Drive drive) {
        try {
            drive.files().get(id).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
