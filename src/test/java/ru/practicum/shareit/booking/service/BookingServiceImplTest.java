package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    BookingServiceImpl service;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    Valid valid;
    private final User booker = new User(1L, "Booker", "b@mail.com");
    private final User owner = new User(2L, "Owner", "o@mail.com");
    long ownerId = 2L;
    private final Item item = new Item(1L, "Кусторез", "Бывалый", true, owner, new ItemRequest());
    private final LocalDateTime start = LocalDateTime.now().plusHours(1);
    private final LocalDateTime end = LocalDateTime.now().plusHours(2);
    private final Booking booking = new Booking(1L, start, end, item, booker, Status.APPROVED);
    private final int from = 0;
    private final int size = 30;

    @Test
    void findById_thenReturnBooking() {
        long bookingId = 1L;
        when(valid.checkUser(anyLong())).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> service.findById(bookingId, 3L));
    }

    @Test
    void findById_thenReturnValidation() {
        long bookingId = 1L;
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto actualBooking = service.findById(bookingId, 2L);

        assertEquals(actualBooking.getBooker().getId(), bookingId);
        assertEquals(actualBooking.getStatus(), Status.APPROVED);
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusAll() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "ALL", from, size);

        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusCurrent() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "CURRENT", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusPast() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "PAST", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusFuture() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "FUTURE", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusWaiting() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "WAITING", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusRejected() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByUserId(ownerId, "REJECTED", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByUserId_thenReturnBookingsWithStatusNoValid() {
        when(valid.checkUser(ownerId)).thenReturn(owner);

        assertThrows(IllegalStatusException.class,
                () -> service.findAllBookingsByUserId(ownerId, "NoValid", from, size));

        verify(bookingRepository, never()).findAllByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusAll() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerId(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "ALL", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusCurrent() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "CURRENT", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusPast() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "PAST", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusFuture() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "FUTURE", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusWaiting() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "WAITING", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusRejected() {
        when(valid.checkUser(ownerId)).thenReturn(owner);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        List<BookingDto> actualList = service.findAllBookingsByItemsOwner(ownerId, "REJECTED", from, size);

        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0).getItem().getId(), item.getId());
    }

    @Test
    void findAllBookingsByItemsOwner_thenReturnBookingsWithStatusNoValid() {
        when(valid.checkUser(ownerId)).thenReturn(owner);

        assertThrows(IllegalStatusException.class,
                () -> service.findAllBookingsByItemsOwner(ownerId, "NoValid", from, size));

        verify(bookingRepository, never()).findAllByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void add_thenReturnAddBooking() {
        when(valid.checkItem(item.getId())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(valid.checkUser(anyLong())).thenReturn(booker);
        BookingDtoFromFrontend bookingDtoFromFrontend = new BookingDtoFromFrontend(item.getId(),
                start, end);

        BookingDto actualBooking = service.add(3L, bookingDtoFromFrontend);

        assertEquals(actualBooking.getStart(), start);
        assertEquals(actualBooking.getBooker().getId(), booker.getId());
    }

    @Test
    void update_thenReturnUpdateBooking() {
        when(valid.checkUser(anyLong())).thenReturn(owner);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto actualBooking = service.update(ownerId, booking.getId(), false);

        assertEquals(actualBooking.getItem().getId(), item.getId());
    }
}