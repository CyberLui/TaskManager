package com.evtech.taskmanager.repositories;

import com.evtech.taskmanager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
