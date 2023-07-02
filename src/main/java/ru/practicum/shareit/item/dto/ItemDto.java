package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemForOwner;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingInItemForOwner lastBooking;
    private BookingInItemForOwner nextBooking;
    private List<CommentDto> comments;
}
