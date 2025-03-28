package com.evtech.taskmanager.entities;


import com.evtech.taskmanager.entities.enuns.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_tb")
public class Task {


    @Id
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



    public Task(Long id, String title, User user) {
        this.id = id;
        this.title = title;
        this.creationDate = LocalDateTime.now();
        this.status = Status.PENDENTE;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
