package com.evtech.taskmanager.services;


import com.evtech.taskmanager.entities.Task;
import com.evtech.taskmanager.entities.enuns.Priority;
import com.evtech.taskmanager.entities.enuns.Status;
import com.evtech.taskmanager.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public Task findById(Long id){
        Optional<Task> obj = taskRepository.findById(id);
        return obj.orElseThrow(()-> new ResourceAccessException("Task NOt Found!"));
    }

    public Task insert(Task obj){
        obj.setCreationDate(LocalDateTime.now());
        obj.setStatus(Status.PENDENTE);
        obj.setPriority(Priority.MEDIUM);
        return taskRepository.save(obj);
    }

    public Task update(Long id, Task obj){
        try{
            Task entity = taskRepository
                    .findById(id).orElseThrow(()-> new EntityNotFoundException("TAsk Not Found! " + id));

        updateData(entity, obj);
        return taskRepository.save(entity);
        }catch (Exception e){
            throw new RuntimeException("Update Task Error! " + e.getMessage());
        }
    }

    private void updateData(Task entity, Task obj) {
        entity.setTitle(obj.getTitle());
        entity.setDescription(obj.getDescription());
        entity.setStatus(obj.getStatus());
    }

    public void delete(Long id){
        taskRepository.deleteById(id);
    }
}
