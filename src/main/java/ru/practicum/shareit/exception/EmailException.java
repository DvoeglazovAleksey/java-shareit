package ru.practicum.shareit.exception;

public class EmailException extends RuntimeException {
    private final String parameter;

    public EmailException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
