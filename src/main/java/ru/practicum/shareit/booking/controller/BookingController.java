package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingDtoFromFrontend bookingFrontend) {
        log.info("Поступил запрос @Post на эндпоинт: '/bookings' для создания booking от пользователя с id = {}", userId);
        return bookingService.add(userId, bookingFrontend);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Поступил запрос @Patch на эндпоинт: '/bookings/{bookingId}' для обновления статуса booking от пользователя с id = {}", userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос @Get на эндпоинт: '/bookings/{bookingId}' для получения booking по bookingId = {}" +
                " от пользователя с id = {}", bookingId, userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(defaultValue = "30") @Min(1) int size) {
        log.info("Поступил запрос @Get на эндпоинт: '/bookings/' для полуения bookings c параметром State = {} от пользователя с id = {}", state, userId);
        return bookingService.findAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                                        @RequestParam(defaultValue = "30") @Min(1) int size) {
        log.info("Поступил запрос @Get на эндпоинт: '/bookings/owner' для получения booking по вещам владельца" +
                " с id = {}", userId);
        return bookingService.findAllBookingsByItemsOwner(userId, state, from, size);
    }
}
