package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoFromFrontendTest {
    @Autowired
    private JacksonTester<BookingDtoFromFrontend> json;

    @Test
    void testBookingDtoFromFrontend() throws Exception {
        BookingDtoFromFrontend bookingDto = new BookingDtoFromFrontend(null, LocalDateTime.now().plusHours(1L), LocalDateTime.now().plusMinutes(20));

        JsonContent<BookingDtoFromFrontend> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();

    }
}