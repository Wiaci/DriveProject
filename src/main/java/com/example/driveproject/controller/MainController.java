package com.example.driveproject.controller;

import com.example.driveproject.service.CurrentIdsService;
import com.example.driveproject.service.DriveService;
import com.example.driveproject.service.GoogleService;
import com.example.driveproject.component.ServerDrive;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@SessionAttributes("username")
public class MainController {

    private final ServerDrive serverDrive;
    private final GoogleService googleService;
    private final DriveService driveService;
    private final CurrentIdsService currentIdsService;


    /*
    * Обработка запросов на посещение основной страницы
    * Достаем информацию о файлах на дисках сервера и пользователя (если авторизован),
    * отправляем ее на темплейт
    * */
    @GetMapping("/")
    public String home(Model model) throws IOException {
        model.addAttribute("files", serverDrive.getFiles());
        String username = (String) model.getAttribute("username");
        if (username != null) {
            Drive drive = googleService.getDriveByUsername(username);
            model.addAttribute("files2", driveService.getFileList(drive));
        } else {
            model.addAttribute("files2", new ArrayList<>());
        }
        return "index";
    }

    /*
     * Обработка запросов на копирование файла с сервера на диск пользователя
     * Из базы узнаем, с какого id копировать файл
     * Проверяем, доступен ли файл по полученному id (если нет, копируем оригинальный файл)
     * Копируем файл по id и заносим в базу id получившегося файла
     * В следующий раз тот же файл скачается с нового id.
     *
     * */
    @PostMapping("/copy/{id}")
    public String copy(@PathVariable String id, Model model) throws IOException {
        String username = (String) model.getAttribute("username");
        Drive drive = googleService.getDriveByUsername(username);
        String currentId = currentIdsService.getCurrentId(id);
        if (!currentId.equals(id) && !driveService.fileExists(currentId, drive)) {
            currentIdsService.deleteRecord(id);
            currentId = id;
        }
        if (currentId.equals(id)) {
            serverDrive.shareFile(id);
        }
        String newId = driveService.copyFile(currentId, drive);
        currentIdsService.setCurrentId(id, newId);
        return "redirect:/";
    }

    /*
    * Обработка запросов на скачивание файла с сервера
    * Делаем то же, что и при копировании, скачиваем скопированный файл
    * */
    @PostMapping("/download/{id}")
    public void download(@PathVariable String id, Model model, HttpServletResponse response) throws IOException {
        String username = (String) model.getAttribute("username");
        Drive drive = googleService.getDriveByUsername(username);
        String currentId = currentIdsService.getCurrentId(id);
        if (!currentId.equals(id) && !driveService.fileExists(currentId, drive)) {
            currentIdsService.deleteRecord(id);
            currentId = id;
        }
        if (currentId.equals(id)) {
            serverDrive.shareFile(id);
        }
        String newId = driveService.copyFile(currentId, drive);
        currentIdsService.setCurrentId(id, newId);
        ServletOutputStream stream = response.getOutputStream();
        driveService.downloadFile(newId, drive, stream);
        stream.close();
    }







}
