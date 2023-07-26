package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    @NotBlank
    @Size(max = 250)
    String text;
    String authorName;
    LocalDateTime created;
}
