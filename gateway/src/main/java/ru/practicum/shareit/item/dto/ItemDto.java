package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    @Size(max = 250)
    private String description;
    private Boolean available;
    private Long requestId;
}
