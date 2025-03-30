package com.evtech.taskmanager.controllers;

import com.evtech.taskmanager.dtos.UserDTO;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> list = userService.findAll();
        List<UserDTO> dtoList = list.stream()
                .map(t -> new UserDTO(t.getId(), t.getName(), t.getUsername(),t.getEmail() ,t.getPassword()))
                .toList();
        return ResponseEntity.ok().body(dtoList);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        User obj = userService.findById(id);
        UserDTO dto = new UserDTO(obj.getId(), obj.getName(),obj.getUsername() ,obj.getEmail(),obj.getPassword());
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserDTO dto){
        User obj = new User();
        obj.setEmail(dto.email());
        obj.setPassword(dto.password());
        obj = userService.insert(obj);

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
                obj.getPassword())
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
        obj.setName(dto.username());
        obj.setEmail(dto.email());
        obj.setPassword(dto.password());

        obj = userService.update(id, obj);

        return ResponseEntity.ok().body(new UserDTO(
                obj.getId(),
                obj.getName(),
                obj.getUsername() ,
                obj.getEmail(),
                obj.getPassword())
        );
    }
}