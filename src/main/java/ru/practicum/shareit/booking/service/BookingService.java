package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingInItemForOwner;

import java.util.List;

public interface BookingService {
    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAllBookingsByUserId(long userId, String state, int from, int size);

    List<BookingDto> findAllBookingsByItemsOwner(long userId, String state, int from, int size);

    BookingDto add(long userId, BookingDtoFromFrontend bookingFrontend);

    BookingDto update(long userId, long bookingId, boolean approved);

    BookingInItemForOwner getLastBooking(long itemId);

    BookingInItemForOwner getNextBooking(Long itemId);
}
