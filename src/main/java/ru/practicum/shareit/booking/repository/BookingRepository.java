package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByItem_OwnerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(long userId, Status status);

    Booking findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime now, Status status);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime now, Status status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(long itemId, long userId, LocalDateTime now, Status status);
}
