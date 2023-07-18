package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {

    public EmailException(String message) {
        super(message);
    }
}
