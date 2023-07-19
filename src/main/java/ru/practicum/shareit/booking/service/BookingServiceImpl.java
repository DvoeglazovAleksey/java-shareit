package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final Valid valid;
    private final Sort sortDesc = Sort.by(Sort.Direction.DESC, "start");
    private final Sort sortAsc = Sort.by(Sort.Direction.ASC, "start");

    @Override
    public BookingDto findById(long bookingId, long userId) {
        valid.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Booking with id = %S not registered", bookingId)));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (userId != ownerId && userId != bookerId) {
            log.warn("Пользователь с id = {} не имеет прав просматривать бронированние", userId);
            throw new NotFoundException(String.format("Пользователь с id = %S не имеет прав просматривать бронированние", userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByUserId(long userId, String state, int from, int size) {
        valid.checkUser(userId);
        PageRequest page = PageRequest.of(from / size, size, sortDesc);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, page);
                break;
            default:
                log.warn("Unknown state: UNSUPPORTED_STATUS");
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllBookingsByItemsOwner(long userId, String state, int from, int size) {
        valid.checkUser(userId);
        PageRequest page = PageRequest.of(from, size, sortDesc);
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_OwnerId(userId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStartIsAfter(userId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(userId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(userId, Status.REJECTED, page);
                break;
            default:
                log.warn("Unknown state: UNSUPPORTED_STATUS");
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto add(long userId, BookingDtoFromFrontend bookingDtoFromFrontend) {
        Item item = valid.checkItem(bookingDtoFromFrontend.getItemId());
        if (!item.getAvailable()) {
            log.warn("Данная вещь не доступна к бронированнию");
            throw new ValidationException("Данная вещь не доступна к бронированнию");
        }
        User booker = valid.checkUser(userId);
        if (userId == item.getOwner().getId()) {
            log.warn("Пользователь с id = {} владелец вещи и не может бронировать сам у себя", userId);
            throw new NotFoundException(String.format("Пользователь с id = %s владелец вещи и не может бронировать сам у себя", userId));
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
                new NotFoundException(String.format("Booking with id = %S not registered", bookingId)));
        long ownerId = booking.getItem().getOwner().getId();
        if (userId == ownerId) {
            if (approved) {
                if (booking.getStatus().equals(Status.APPROVED)) {
                    log.warn("Пользователь уже одобрил бронированние");
                    throw new ValidationException("Пользователь уже одобрил бронированние");
                } else {
                    booking.setStatus(Status.APPROVED);
                }
            } else {
                if (booking.getStatus().equals(Status.REJECTED)) {
                    log.warn("Пользователь уже отклонил бронирование");
                    throw new ValidationException("Пользователь уже отклонил бронирование");
                }
                booking.setStatus(Status.REJECTED);
            }
        } else {
            log.warn("Пользователь с id = {} не имеет прав согласовывать бронированние", userId);
            throw new NotFoundException(String.format("Пользователь с id = $S не имеет прав согласовывать бронированние", userId));
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingInItemForOwner getLastBooking(long itemId) {
        Booking booking = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatus(itemId,
                LocalDateTime.now(), Status.APPROVED, sortDesc);
        return BookingMapper.toBookingInItemForOwner(booking);
    }

    @Override
    public BookingInItemForOwner getNextBooking(Long itemId) {
        return BookingMapper.toBookingInItemForOwner(bookingRepository.findFirstByItem_IdAndStartAfterAndStatus(itemId,
                LocalDateTime.now(), Status.APPROVED, sortAsc));
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
