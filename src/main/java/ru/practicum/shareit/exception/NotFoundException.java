package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    private final String parameter;

    public String getParameter() {
        return parameter;
    }

    public NotFoundException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }
}
