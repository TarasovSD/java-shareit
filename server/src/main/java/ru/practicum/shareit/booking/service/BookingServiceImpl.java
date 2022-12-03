package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    final BookingRepository bookingRepository;
    final ItemRepository itemRepository;

    final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public BookingWithItemNameDto createBooking(ItemInfoDto.BookingDto bookingDto, Long bookerID) {
        Item item = itemRepository.findById(bookingDto.getItemId()).get();
        if (!item.getAvailable()) {
            throw new ItemAvailableIsFalseException("Бронь невозможна, так как поле available = false");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new EndBeforeStartException("Значение поля End не может быть раньше значения поля Start");
        }
        if ((long) item.getOwnerId() == bookerID) {
            throw new BookerIsItemOwnerException("Попытка забронировать вещь владельцем");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, userRepository.findById(bookerID).get());
        booking.setStatus(Status.WAITING);
        Booking bookingForSave = bookingRepository.save(booking);
        String itemName = itemRepository.findById(bookingForSave.getItem().getId()).get().getName();
        return BookingMapper.toBookingDtoWithItemName(bookingForSave, itemName);
    }

    @Override
    @Transactional
    public BookingWithItemNameDto approveOrRejectBookingRequest(Long userID, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingAlreadyApprovedException("Бронирование уже подтверждено");
        }
        if ((long) booking.getItem().getOwnerId() != userID) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking bookingForSave = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoWithItemName(bookingForSave, bookingForSave.getItem().getName());
    }

    @Override
    public Optional<BookingWithItemNameDto> getBookingById(Long bookingId, Long userId) {
        Booking foundBooking = bookingRepository.findById(bookingId).get();
        if ((long) foundBooking.getBooker().getId() == userId || (long) foundBooking.getItem().getOwnerId() == userId) {
            return Optional.of(BookingMapper.toBookingDtoWithItemName(foundBooking, foundBooking.getItem().getName()));
        } else {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
    }

    @Override
    public List<BookingWithItemNameDto> getListOfBookingsByUserId(Long bookerId, String state, PageRequest pageRequest) {
        User user = userRepository.findById(bookerId).get();
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<BookingWithItemNameDto> allBookings = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();
        State states = State.valueOf(state);
        switch (states) {
            case ALL:
                for (Booking booking : bookingRepository.findByBooker_IdOrderByStartDesc(bookerId, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
            case CURRENT:
                for (Booking booking : bookingRepository.getByBookerCurrent(bookerId, end, start, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
            case FUTURE:
                for (Booking booking : bookingRepository.getByBookerFuture(bookerId, start, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
            case WAITING:
                for (Booking booking : bookingRepository.findBookingsByBooker_IdAndAndStatusOrderByStartDesc(bookerId, Status.WAITING, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
            case REJECTED:
                for (Booking booking : bookingRepository.findBookingsByBooker_IdAndAndStatusOrderByStartDesc(bookerId, Status.REJECTED, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
            case PAST:
                for (Booking booking : bookingRepository.getLastBookingsByBooker(bookerId, end, pageRequest)) {
                    allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                }
                break;
        }
        return allBookings;
    }

    @Override
    public List<BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(Long userId, String state,
                                                                          PageRequest pageRequest) {
        User user = userRepository.findById(userId).get();
        List<BookingWithItemNameDto> allBookings = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();
        Integer from = 0;
        Integer size = 15;
        int page = from / size;
        PageRequest pageRequestForItem = PageRequest.of(page, size);
        State states = State.valueOf(state);
        switch (states) {
            case ALL: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.findByItem_IdOrderByStartDesc(item.getId(), pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
            case CURRENT: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.getByItemIdCurrent(item.getId(), end, start, pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
            case FUTURE: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.getByItemIdFuture(item.getId(), start, pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
            case WAITING: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.findBookingsByItem_IdAndAndStatusOrderByStartDesc(item.getId(), Status.WAITING, pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
            case REJECTED: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.findBookingsByItem_IdAndAndStatusOrderByStartDesc(item.getId(), Status.REJECTED, pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
            case PAST: {
                List<Item> allUsersItems = itemRepository.findAllByOwnerId(userId, pageRequestForItem);
                for (Item item : allUsersItems) {
                    for (Booking booking : bookingRepository.getLastBookingsByItem(item.getId(), start, pageRequest)) {
                        allBookings.add(BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName()));
                    }
                }
                break;
            }
        }
        return allBookings;
    }
}
