package com.evtech.taskmanager.services;

import com.evtech.taskmanager.dtos.TaskDTO;
import com.evtech.taskmanager.entities.Task;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Priority;
import com.evtech.taskmanager.entities.enuns.Status;
import com.evtech.taskmanager.exceptions.ResourceNotFoundException;
import com.evtech.taskmanager.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService){
        this.taskRepository = taskRepository;
        this.userService =userService;
    }

    public List<TaskDTO> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        return TaskDTO.fromEntity(task);
    }

    @Transactional
    public TaskDTO insert(TaskDTO dto, Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório.");
        }

        Task task = dto.toEntity();

        // Busca o usuário no banco pelo ID e associa à Task
        User user = userService.findById(userId);
        task.setUser(user);
        task.setCreationDate(LocalDateTime.now());
        task.setStatus(Status.PENDENTE); // Garante que o status seja PENDENTE

        // Salva a Task
        task = taskRepository.save(task);

        return TaskDTO.fromEntity(task);
    }


    @Transactional
    public TaskDTO update(Long id, TaskDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        if (dto.getUserId() != null) {
            task.setUser(userService.findById(dto.getUserId()));
        }

        task = taskRepository.save(task);
        return TaskDTO.fromEntity(task);
    }

    private void updateData(Task entity, Task obj, Long userId) {
        entity.setTitle(obj.getTitle());
        entity.setDescription(obj.getDescription());
        entity.setStatus(obj.getStatus());
        entity.setPriority(obj.getPriority());
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada");
        }
        taskRepository.deleteById(id);
    }
}

