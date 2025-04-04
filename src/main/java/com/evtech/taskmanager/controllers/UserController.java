package com.evtech.taskmanager.controllers;

import com.evtech.taskmanager.dtos.TaskDTO;
import com.evtech.taskmanager.dtos.UserDTO;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> list = userService.findAll();
        List<UserDTO> dtoList = list.stream()
                .map(user -> {
                    List<TaskDTO> tasksDTO = user.getTaskList().stream()
                            .map(t -> new TaskDTO(
                                    t.getId(),
                                    t.getTitle(),
                                    t.getDescription(),
                                    t.getUser().getId(),
                                    t.getPriority(),
                                    t.getCreationDate(),
                                    t.getStatus()
                            )).toList();

                    return new UserDTO(user.getId(), user.getName(), user.getName(), user.getEmail(), user.getPassword(), tasksDTO);
                }).toList();

        return ResponseEntity.ok().body(dtoList);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        User obj = userService.findById(id);
        List<TaskDTO> tasksDTO = obj.getTaskList().stream()
                .map(t -> new TaskDTO(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getUser().getId(),
                        t.getPriority(),
                        t.getCreationDate(),
                        t.getStatus()
                )).toList();

        UserDTO dto = new UserDTO(obj.getId(), obj.getName(), obj.getUsername(), obj.getEmail(), obj.getPassword(), tasksDTO);        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserDTO dto){
        User obj = new User();
        obj.setEmail(dto.email());
        obj.setPassword(passwordEncoder.encode(dto.password()));
        obj = userService.insert(obj);
        List<TaskDTO> tasksDTO = new ArrayList<>();

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(new UserDTO(
                obj.getId(),
                obj.getName(),
                obj.getUsername() ,
                obj.getEmail(),
                obj.getPassword(),
                tasksDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto){
        User obj = new User();
        obj.setName(dto.name());
        obj.setUsername(dto.username());
        obj.setEmail(dto.email());
        obj.setPassword(passwordEncoder.encode(dto.password()));

        // Atualiza o usuário mantendo as tarefas existentes
        obj = userService.update(id, obj);

        // Converter as tarefas do usuário atualizado para TaskDTO
        List<TaskDTO> tasksDTO = obj.getTaskList().stream()
                .map(t -> new TaskDTO(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getUser().getId(),
                        t.getPriority(),
                        t.getCreationDate(),
                        t.getStatus()
                )).toList();

        return ResponseEntity.ok().body(new UserDTO(
                obj.getId(),
                obj.getName(),
                obj.getUsername(),
                obj.getEmail(),
                obj.getPassword(),
                tasksDTO
        ));
    }
}