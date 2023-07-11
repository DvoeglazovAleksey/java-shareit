package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getByUserId(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);
}
