package com.evtech.taskmanager.services;

import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Role;
import com.evtech.taskmanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        Optional<User> obj = userRepository.findById(id);
        return obj.orElseThrow(()-> new ResourceAccessException("Id Not Found"));
    }

    public User insert(User obj){
        if (obj.getName() == null || obj.getName().isEmpty()) {
            obj.setName(obj.getEmail());
        }
        if (obj.getUsername() == null || obj.getUsername().isEmpty()) {
            obj.setUsername(obj.getEmail());
        }
        obj.setRole(Role.ROLE_USER);
        return userRepository.save(obj);
    }

    public void delete(Long id){
        userRepository.deleteById(id);
    }

    public User update(Long id, User obj){
        try{
            User entity = userRepository
                    .findById(id).orElseThrow(() -> new EntityNotFoundException("User Not Found!: " + id));
            updateData(entity, obj);
            return userRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Update User Error! " + e.getMessage(), e);
        }
    }

    private void updateData(User entity, User obj) {
        entity.setName(obj.getName());
        entity.setUsername(obj.getUsername());
        entity.setEmail(obj.getEmail());
        entity.setPassword(obj.getPassword());


    }


}