package com.evtech.taskmanager.controllers;

import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Role;
import com.evtech.taskmanager.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Middleware para verificar se o usuário é admin
    @ModelAttribute
    public void checkAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null || !userRole.equals("ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "Acesso negado. Você precisa ser administrador.");
            throw new RuntimeException("Acesso negado");
        }
    }

    // Lista todos os usuários
    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        try {
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
            model.addAttribute("roles", Role.values());
            return "admin/users";
        } catch (Exception e) {
            return "redirect:/tasks?error=" + e.getMessage();
        }
    }

    // Exibe formulário para editar usuário
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "admin/edit-user";
        } catch (Exception e) {
            return "redirect:/admin/users?error=" + e.getMessage();
        }
    }

    // Processa a edição de usuário
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, String name, String email,
                             String password, String role, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            user.setName(name);
            user.setEmail(email);

            // Atualiza a senha se fornecida
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }

            // Atualiza o papel se fornecido
            if (role != null && !role.isEmpty()) {
                user.setRole(Role.valueOf(role));
            }

            userService.update(id, user);

            redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // Exclui um usuário
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Usuário excluído com sucesso!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir usuário: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }
}

