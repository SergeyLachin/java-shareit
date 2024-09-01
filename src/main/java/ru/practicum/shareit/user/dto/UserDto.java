package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    @NotNull
    private Long id;
    @Email
    @NotBlank
    @NotEmpty
    private String email;
    @NotBlank
    @NotEmpty
    private String name;

    public UserDto(Long id, String name, String email) {
    }
}

