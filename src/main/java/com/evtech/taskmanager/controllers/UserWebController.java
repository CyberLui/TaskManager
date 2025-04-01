package com.evtech.taskmanager.controllers;


import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/tasks")
    public String getUserTasks(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("tasks", user.getTaskList());
        return "user-tasks"; // Vai renderizar user-tasks.html em templates/
    }
}

