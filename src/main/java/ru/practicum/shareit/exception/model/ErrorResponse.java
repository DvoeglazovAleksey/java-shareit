package ru.practicum.shareit.exception.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
