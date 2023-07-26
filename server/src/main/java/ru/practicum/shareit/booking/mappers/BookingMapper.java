package ru.practicum.shareit.booking.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingInItemForOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mappers.UserMapper;

@UtilityClass
public class BookingMapper {
    public static BookingInItemForOwner toBookingInItemForOwner(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingInItemForOwner(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
    }

    public static Booking toBooking(BookingDtoFromFrontend bookingFrontend) {
        Booking booking = new Booking();
        booking.setStart(bookingFrontend.getStart());
        booking.setEnd(bookingFrontend.getEnd());
        Item item = new Item();
        item.setId(bookingFrontend.getItemId());
        booking.setItem(item);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()), booking.getStart(), booking.getEnd(), booking.getStatus());
    }
}
