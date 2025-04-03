package com.evtech.taskmanager.controllers;


import com.evtech.taskmanager.dtos.UserDTO;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Role;
import com.evtech.taskmanager.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Página inicial - redireciona para login se não estiver autenticado
    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        return "redirect:/tasks";
    }

    // Exibe a página de login
    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/tasks";
        }
        return "login";
    }

    // Processa o login
    @PostMapping("/login")
    public String processLogin(String email, String password, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
                User user = userOpt.get();
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userRole", user.getRole().toString());

                // Redireciona para área admin se for admin
                if (user.getRole() == Role.ROLE_ADMIN) {
                    return "redirect:/admin/users";
                }

                return "redirect:/tasks";
            } else {
                redirectAttributes.addFlashAttribute("error", "Email ou senha inválidos");
                return "redirect:/login?error";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao fazer login: " + e.getMessage());
            return "redirect:/login?error";
        }
    }

    // Exibe a página de cadastro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO(null, "", "", "", "", new ArrayList<>()));
        return "register";
    }

    // Processa o cadastro
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") UserDTO userDTO,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Verifica se o email já existe
            if (userService.findByEmail(userDTO.email()).isPresent()) {
                result.rejectValue("email", "error.user", "Este email já está em uso");
                return "register";
            }

            // Cria o usuário
            User user = new User();
            user.setName(userDTO.name());
            user.setEmail(userDTO.email());
            user.setPassword(userDTO.password());

            userService.insert(user);

            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso!");
            return "redirect:/login?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar: " + e.getMessage());
            return "redirect:/register?error";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout";
    }



}
