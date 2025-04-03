package com.evtech.taskmanager.controllers;

import com.evtech.taskmanager.dtos.UserDTO;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(String name, String email, String currentPassword,
                                String newPassword, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findById(userId);

            // Verifica a senha atual
            if (!user.getPassword().equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Senha atual incorreta");
                return "redirect:/profile?error";
            }

            // Atualiza os dados
            user.setName(name);
            user.setEmail(email);

            // Atualiza a senha se uma nova foi fornecida
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }

            userService.update(userId, user);

            // Atualiza o nome na sessão
            session.setAttribute("userName", user.getName());

            redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
            return "redirect:/profile?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/profile?error";
        }
    }
}
