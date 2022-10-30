package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start < :start and b.end > :end order by b.start desc")
    List<Booking> getByBookerCurrent(Long bookerId, LocalDateTime end, LocalDateTime start);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start > :start order by b.start desc")
    List<Booking> getByBookerFuture(Long bookerId, LocalDateTime start);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.status = :status order by b.start desc")
    List<Booking> getByItemIdAndStatus(Long bookerId, Status status);

    List<Booking> findByItem_IdOrderByStartDesc(Long id);

    @Query("select b from Booking b where b.item.id = :id and b.start < :start and b.end > :end order by b.start desc")
    List<Booking> getByItemIdCurrent(Long id, LocalDateTime end, LocalDateTime start);

    @Query("select b from Booking b where b.item.id = :id and b.start > :start order by b.start desc")
    List<Booking> getByItemIdFuture(Long id, LocalDateTime start);

    @Query("select b from Booking b where b.item.id = :id and b.status = :status order by b.start desc")
    List<Booking> getByItemIdEndStatus(Long id, Status status);

    @Query("select b from Booking b where b.item.id = :id and b.end < :end order by b.end desc")
    List<Booking> getLastByItemId(@Param("id") Long id, @Param("end") LocalDateTime end);

    @Query(value = "select * from bookings where item_id = ?1 and start_date_time > ?2 order by start_date_time", nativeQuery = true)
    List<Booking> getNextByItemId(Long id, LocalDateTime start);

    @Query(value = "select * from bookings where item_id = ?1 and booker_id = ?2 and end_date_time < ?3", nativeQuery = true)
    List<Booking> getLastBookings(Long itemId, Long userId, LocalDateTime created);

    @Query(value = "select * from bookings where booker_id = ?1 and end_date_time < ?2", nativeQuery = true)
    List<Booking> getLastBookingsByBooker(Long bookerId, LocalDateTime now);

    @Query(value = "select * from bookings where item_id = ?1 and end_date_time < ?2", nativeQuery = true)
    List<Booking> getLastBookingsByItem(Long itemId, LocalDateTime now);
}
