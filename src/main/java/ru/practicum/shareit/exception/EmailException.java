package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {
    private final String parameter;

    public EmailException(String parameter) {
        this.parameter = parameter;
    }
}
