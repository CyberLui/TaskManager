package com.evtech.taskmanager.services;


import com.evtech.taskmanager.entities.Task;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Priority;
import com.evtech.taskmanager.entities.enuns.Status;
import com.evtech.taskmanager.repositories.TaskRepository;
import com.evtech.taskmanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public Task findById(Long id){
        Optional<Task> obj = taskRepository.findById(id);
        return obj.orElseThrow(()-> new ResourceAccessException("Task NOt Found!"));
    }

    public Task insert(Task obj, Long userId){
        Optional<User> user = userRepository.findById(userId);
        obj.setCreationDate(LocalDateTime.now());
        obj.setStatus(Status.PENDENTE);
        obj.setPriority(Priority.MEDIUM);
        obj.setUser(user);
        return taskRepository.save(obj);
    }

    public Task update(Long id, Task obj, @RequestParam(required = false) Long userId){
        try{
            Task entity = taskRepository
                    .findById(id).orElseThrow(()-> new EntityNotFoundException("TAsk Not Found! " + id));

        updateData(entity, obj, userId);
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found! ID: " + userId));
            entity.setUser(Optional.ofNullable(user));
        }
        return taskRepository.save(entity);
        }catch (Exception e){
            throw new RuntimeException("Update Task Error! " + e.getMessage());
        }
    }

    private void updateData(Task entity, Task obj, Long userId) {
        entity.setTitle(obj.getTitle());
        entity.setDescription(obj.getDescription());
        entity.setStatus(obj.getStatus());
        entity.setPriority(obj.getPriority());
    }

    public void delete(Long id){
        taskRepository.deleteById(id);
    }
}
