package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemRequestServiceIT {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void getByUserId() {
        User requestor = new User(null, "User", "u@mail.com");
        ItemRequest itemRequest = new ItemRequest(null, "Тример", requestor, LocalDateTime.now());
        em.persist(requestor);
        em.persist(itemRequest);

        ItemRequestDto result = itemRequestService.getById(requestor.getId(), itemRequest.getId());

        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.getCreated(), equalTo(itemRequest.getCreated()));
    }
}