package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private final Valid valid;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    @Override
    public ItemRequestDto add(long userId, ItemRequestDto itemRequestDto) {
        User requestor = valid.checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getByUserId(long userId) {
        valid.checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(userId);
        return addItemsInItemRequest(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        valid.checkUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from, size, sort);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdNot(userId, pageRequest);
        return addItemsInItemRequest(itemRequests);
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        valid.checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("ItemRequest запрос с id %s не найден", requestId));
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> itemDtoList = itemRepository.findByRequestId(requestId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        if (!itemDtoList.isEmpty()) {
            requestDto.setItems(itemDtoList);
        }
        return requestDto;
    }

    private List<ItemRequestDto> addItemsInItemRequest(List<ItemRequest> itemRequests) {
        List<Long> itemRequestsId = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.findAllByRequestIdIn(itemRequestsId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        if (itemDtos.isEmpty()) {
            return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        } else {
            Map<Long, List<ItemDto>> itemMap = new HashMap<>();
            for (ItemDto itemDto : itemDtos) {
                itemMap.put(itemDto.getRequestId(), Collections.singletonList(itemDto));
            }
            List<ItemRequestDto> itemRequestDtoList = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
            for (ItemRequestDto itemRequest : itemRequestDtoList) {
                itemRequest.setItems(itemMap.get(itemRequest.getId()));
            }
            return itemRequestDtoList;
        }
    }
}
