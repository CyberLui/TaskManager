package com.evtech.taskmanager.dtos;

import com.evtech.taskmanager.entities.Task;
import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Priority;
import com.evtech.taskmanager.entities.enuns.Status;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Long userId;
    private Priority priority;
    private LocalDateTime creationDate;
    private Status status;  // Novo campo

    public TaskDTO() {}

    public TaskDTO(Long id, String title, String description, Long userId, Priority priority, LocalDateTime creationDate, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.creationDate = creationDate;
        this.status = status != null ? status : Status.PENDENTE;
    }

    public static TaskDTO fromEntity(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getPriority(),
                task.getCreationDate(),
                task.getStatus() // Pegando o status da entidade
        );
    }

    public Task toEntity() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setPriority(this.priority != null ? this.priority : Priority.MEDIUM);
        task.setCreationDate(LocalDateTime.now());
        task.setStatus(Status.PENDENTE); // Definindo o status padrão como PENDENTE

        if (this.userId != null) {
            task.setUser(new User(this.userId));
        }

        return task;
    }

    public Long getId() {
        return id;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreationDate() { return creationDate; }

    public Status getStatus() { return status; }

}
