package com.evtech.taskmanager.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(Long id,
                      String name,
                      String username,
                      @NotBlank String email,
                      String password ) {
}
