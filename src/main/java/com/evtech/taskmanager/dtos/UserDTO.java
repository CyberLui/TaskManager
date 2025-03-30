package com.evtech.taskmanager.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDTO(Long id,
                      String name,
                      String username,
                      @NotBlank String email,
                      String password,
                      List<TaskDTO> tasks){
}
