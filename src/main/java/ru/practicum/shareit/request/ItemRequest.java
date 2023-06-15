package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
