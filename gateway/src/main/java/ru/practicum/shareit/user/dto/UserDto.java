package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Email некорректный")
    private String email;
}