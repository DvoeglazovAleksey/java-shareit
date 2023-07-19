package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
class BookingServiceTestIT {
    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void findAllBookingsByUserId() {
        User owner = new User(null, "User1", "u@email.com");
        Item item = new Item(null, "Item1", "GoodItem", true, owner, null);
        em.persist(owner);
        em.persist(item);
        User booker = new User(null, "Booker", "b@email.com");
        em.persist(booker);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Booking booking = new Booking(null, start,
                end, item, booker, Status.APPROVED);
        em.persist(booking);

        List<BookingDto> resultList = bookingService.findAllBookingsByUserId(booker.getId(), "ALL", 0, 30);

        assertEquals(resultList.size(), 1);
        assertThat(resultList.get(0).getItem().getDescription(), equalTo(booking.getItem().getDescription()));
    }
}