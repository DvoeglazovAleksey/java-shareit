package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    @Size(max = 50, message = "Длина описания должна быть до 50 символов")
    private String description;
    private Boolean available;
    private Long requestId;
}
