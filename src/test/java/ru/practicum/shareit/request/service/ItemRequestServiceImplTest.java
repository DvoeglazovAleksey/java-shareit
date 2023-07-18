package ru.practicum.shareit.request.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    Valid valid;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private final User requestor = new User(1L, "User", "e@mail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Кусторез", null, null);

    private final ItemRequest itemRequest = new ItemRequest(1L, "Кусторез", requestor, null);

    private final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    private final User owner = new User(2L, "Павел", "p@mail.com");
    private final Item item = new Item(1L, "Кусторез", "Бывалый", true, owner, itemRequest);

    private final int from = 0;

    private final int size = 30;

    private final PageRequest page = PageRequest.of(from / size, size, SORT);

    private final long userId = 1L;

    @Test
    void add() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(valid.checkUser(userId)).thenReturn(requestor);

        ItemRequestDto actualItemRequestDto = itemRequestService.add(1L, itemRequestDto);

        assertEquals(itemRequestDto.getId(), actualItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequestDto.getDescription());
    }

    @Test
    void getByUserId() {
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());
        when(itemRequestRepository.findByRequestorId(userId)).thenReturn(List.of(itemRequest));
        when(valid.checkUser(userId)).thenReturn(requestor);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getByUserId(userId);

        assertEquals(1, itemRequestDtoList.size());
        verify(itemRequestRepository, times(1)).findByRequestorId(userId);

    }

    @Test
    void getAll_thenReturnItemRequests() {
        when(itemRequestRepository.findByRequestorIdNot(userId, page)).thenReturn(List.of(itemRequest));
        when(valid.checkUser(userId)).thenReturn(requestor);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestDto> actualList = itemRequestService.getAll(userId, from, size);

        verify(itemRequestRepository, times(1)).findByRequestorIdNot(userId, page);
        assertEquals(1, actualList.size());
        assertEquals(item.getName(), actualList.get(0).getItems().get(0).getName());
    }

    @Test
    @SneakyThrows
    void getById() {
        long requestId = 1L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(valid.checkUser(userId)).thenReturn(new User());
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto actualItemRequest = itemRequestService.getById(userId, requestId);

        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findByRequestId(requestId);

        assertEquals(requestId, actualItemRequest.getId());
        assertEquals(1, actualItemRequest.getItems().size());
    }
}