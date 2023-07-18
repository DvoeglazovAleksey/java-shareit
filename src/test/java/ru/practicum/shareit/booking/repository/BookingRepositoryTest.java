package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    private BookingRepository repository;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");
    private final int size = 30;
    private final PageRequest page = PageRequest.of(0, size, SORT);
    private final User owner = new User(null, "Owner", "o@mail.ru");
    private final Item item = new Item(null, "Кусторез", "Бывалый", true, owner, null);

    private final User booker = new User(null, "Booker", "a@mail.ru");
    private final Status status = Status.APPROVED;
    private final Booking booking = new Booking(null, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1), item, booker, status);

    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(item);
        em.persist(booker);
        em.persist(booking);
    }

    @Test
    void findAllByBookerId_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByBookerId(booker.getId(), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByItem_OwnerId_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerId(owner.getId(), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter_thenReturnBoorinkList_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(20), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByItem_OwnerIdAndEndIsBefore_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerIdAndEndIsBefore(owner.getId(),
                LocalDateTime.now().plusHours(2), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndStartIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByBookerIdAndStartIsAfter(booker.getId(),
                LocalDateTime.now().plusHours(1), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByItem_OwnerIdAndStartIsAfter_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerIdAndStartIsAfter(owner.getId(),
                LocalDateTime.now().plusHours(1), page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndStatus_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByBookerIdAndStatus(booker.getId(),
                status, page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findAllByItem_OwnerIdAndStatus_thenReturnBookingList() {
        List<Booking> actualList = repository.findAllByItem_OwnerIdAndStatus(owner.getId(),
                status, page);

        assertNotNull(actualList);
        assertEquals(booking.getItem().getId(), actualList.get(0).getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndStartBeforeAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItem_IdAndStartBeforeAndStatus(item.getId(), LocalDateTime.now().plusHours(3), status, SORT);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndStartAfterAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItem_IdAndStartAfterAndStatus(item.getId(), LocalDateTime.now().plusHours(1),
                status, SORT);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus_thenReturnBooking() {
        Booking actualBooking = repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(item.getId(),
                booker.getId(), LocalDateTime.now().plusHours(3), status);

        assertNotNull(actualBooking);
        assertEquals(booking.getItem().getId(), actualBooking.getItem().getId());
    }
}