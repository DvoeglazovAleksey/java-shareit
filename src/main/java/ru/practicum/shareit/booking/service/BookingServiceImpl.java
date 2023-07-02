package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingInItemForOwner;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final Valid valid;

    @Override
    public BookingDto findById(long bookingId, long userId) {
        valid.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id = %S not registered", bookingId));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (userId != ownerId && userId != bookerId) {
            throw new NotFoundException("Пользователь с id = %S не имеет прав просматривать бронированние", userId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByUserId(long userId, String state) {
        valid.checkUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllBookingsByItemsOwner(long userId, String state) {
        valid.checkUser(userId);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto add(long userId, BookingDtoFromFrontend bookingDtoFromFrontend) {
        Item item = valid.checkItem(bookingDtoFromFrontend.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь не доступна к бронированнию");
        }
        User booker = valid.checkUser(userId);
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Пользователь с id = %s владелец вещи и не может бронировать сам у себя", userId);
        }
        checkDate(bookingDtoFromFrontend);
        Booking booking = BookingMapper.toBooking(bookingDtoFromFrontend);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(long userId, long bookingId, boolean approved) {
        valid.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id = %S not registered", bookingId));
        long ownerId = booking.getItem().getOwner().getId();
        if (userId == ownerId) {
            if (approved) {
                if (booking.getStatus().equals(Status.APPROVED)) {
                    throw new ValidationException("Пользователь уже одобрил бронированние");
                } else {
                    booking.setStatus(Status.APPROVED);
                }
            } else {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    throw new ValidationException("Пользователь уже отклонил бронирование");
                }
                booking.setStatus(Status.REJECTED);
            }
        } else {
            throw new NotFoundException("Пользователь с id = $S не имеет прав согласовывать бронированние", userId);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingInItemForOwner getLastBooking(long itemId) {
        return BookingMapper.toBookingInItemForOwner(bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId,
                LocalDateTime.now(), Status.APPROVED));
    }

    @Override
    public BookingInItemForOwner getNextBooking(Long itemId) {
        return BookingMapper.toBookingInItemForOwner(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId,
                LocalDateTime.now(), Status.APPROVED));
    }

    private void checkDate(BookingDtoFromFrontend bookingDtoFromFrontend) {
        if (bookingDtoFromFrontend.getEnd().isBefore(bookingDtoFromFrontend.getStart())) {
            throw new ValidationException("Дата начала бронирования не может быть раньше даты конца бронированния");
        }
        if (bookingDtoFromFrontend.getEnd().isEqual(bookingDtoFromFrontend.getStart())) {
            throw new ValidationException("Время окончания броннирования не может быть равно времени начала.");
        }
    }
}
