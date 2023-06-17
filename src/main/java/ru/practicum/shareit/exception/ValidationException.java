package ru.practicum.shareit.exception;

public class ValidationException extends RuntimeException {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
