package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody BookingDtoFromFrontend bookingFrontend) {
        return bookingService.add(userId, bookingFrontend);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByItemsOwner(userId, state);
    }
}
