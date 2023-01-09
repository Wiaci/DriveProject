package com.example.driveproject.controller;

import com.example.driveproject.service.GoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@SessionAttributes("username")
public class AuthController {

    private final GoogleService googleService;

    /*
    * Обработка запросов на предоставление доступа к диску пользователя
    * */
    @PostMapping("/access")
    public String getAccess(Model model) {
        String username = (String) model.getAttribute("username");
        if (username == null) {
            return "redirect:/googlesignin";
        }
        return "redirect:/";
    }

    /*
    * Обработка запросов на закрытие доступа
    * */
    @PostMapping("/close")
    public String close(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/";
    }


    /*
    * Обработка запросов на гугл-авторизацию
    * */
    @GetMapping("/googlesignin")
    public void doGoogleSignIn(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleService.getRedirectUrl());
    }

    /*
     * Обработка перенаправлений на гугл-авторизацию.
     * Достаем из запроса код авторизации и сохраняем в файл
     * */
    @GetMapping("/oauth")
    public String saveAuthorizationCode(HttpServletRequest request, Model model) throws IOException {
        String code = request.getParameter("code");
        if (code != null) {
            String username = (String) model.getAttribute("username");
            if (username == null) {
                username = Double.toString(Math.random());
                model.addAttribute("username", username);
            }
            googleService.saveCode(code, username);
        }
        return "redirect:/";
    }



}
