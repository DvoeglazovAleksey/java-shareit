package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceTestIT {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void getItemsByOwnerId() {
        User owner = new User(null, "User1", "u@email.com");
        Boolean available = true;
        Item item = new Item(null, "Item1", "GoodItem", available, owner, null);
        em.persist(owner);
        em.persist(item);
        User booker = new User(null, "Booker", "b@email.com");
        LocalDateTime startNext = LocalDateTime.now().plusHours(1);
        LocalDateTime endNext = LocalDateTime.now().plusHours(2);
        Booking bookingNext = new Booking(null, startNext,
                endNext, item, booker, Status.APPROVED);
        LocalDateTime startLast = LocalDateTime.now().minusHours(2);
        LocalDateTime endLast = LocalDateTime.now().minusHours(1);
        Booking bookingLast = new Booking(null, startLast,
                endLast, item, booker, Status.APPROVED);
        em.persist(booker);
        em.persist(bookingNext);
        em.persist(bookingLast);

        List<ItemDto> resultList = itemService.getItemsByOwnerId(owner.getId(), 0, 30);

        assertEquals(resultList.size(), 1);
        assertThat(resultList.get(0).getDescription(), equalTo(item.getDescription()));

    }
}