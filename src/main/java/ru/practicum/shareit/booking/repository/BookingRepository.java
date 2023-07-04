package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long userId, Sort sort);

    List<Booking> findAllByItem_OwnerId(long userId, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime now, LocalDateTime now2, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime now, LocalDateTime now2, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long userId, Status status, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStatus(long userId, Status status, Sort sort);

    Booking findFirstByItem_IdAndStartBeforeAndStatus(long itemId, LocalDateTime now, Status status, Sort sort);

    Booking findFirstByItem_IdAndStartAfterAndStatus(long itemId, LocalDateTime now, Status status, Sort sort);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(long itemId, long userId, LocalDateTime now, Status status);
}
