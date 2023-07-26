package ru.practicum.shareit.error.model;

import lombok.Data;

@Data
public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
