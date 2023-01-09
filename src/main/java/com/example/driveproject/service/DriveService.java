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

    /*
    * Достаем информацию о файлах на диске
    * */
    public List<File> getFileList(Drive drive) throws IOException {
        if (drive != null) {
            FileList result = drive.files().list()
                    .setQ("'me' in owners")                     // Не достаем shared with me файлы, а только те, которые
                                                                // реально хранятся на диске
                    .setFields("files(id, name, mimeType)")     // Берем id файла, его имя и mimeType, по нему будем
                                                                // определять, можно ли его скачать
                    .execute();

            return result.getFiles();
        }
        return new ArrayList<>();
    }

    /*
    * Копируем файл с id на destinationDrive. Возвращаем id скопированного файла.
    * */
    public String copyFile(String fileId, Drive destinationDrive) throws IOException {
        String newId = destinationDrive.files().copy(fileId, new File())
                .execute().getId();
        shareFile(newId, destinationDrive);
        return newId;
    }

    public void downloadFile(String fileId, Drive drive, OutputStream stream) throws IOException {
        drive.files().get(fileId).executeMediaAndDownloadTo(stream);
    }

    /*
    * Добавляем возможность чтения файла для всех
    * */
    public void shareFile(String id, Drive drive) throws IOException {
        Permission permission = new Permission();
        permission.setRole("reader");
        permission.setType("anyone");
        drive.permissions().create(id, permission).execute();
    }

    /*
    * Проверяем, доступен ли для drive файл с id
    * */
    public boolean fileExists(String id, Drive drive) {
        try {
            drive.files().get(id).execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
