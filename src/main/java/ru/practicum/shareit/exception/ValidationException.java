package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ValidationException extends RuntimeException {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }
}
