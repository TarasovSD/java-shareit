package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    final BookingRepository bookingRepository;

    final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userID) {
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isPresent()) {
            Item item = ItemMapper.toItem(itemDto, userOptional.get());
            validateItem(item);
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new UserNotFoundException("Невозможно создать вещь, так как owner не найден!");
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userID) {
        Optional<User> userOptional = userRepository.findById(userID);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден!");
        }
        Item item = ItemMapper.toItem(itemDto, userOptional.get());
        Optional<Item> foundItemOptional = itemRepository.findById(itemId);
        if (foundItemOptional.isEmpty()) {
            throw new ItemNotFoundException("Вещь не найдена!");
        }
        validateItem(foundItemOptional.get());
        if (!Objects.equals(item.getOwnerId(), foundItemOptional.get().getOwnerId())) {
            throw new ItemNotFoundException("Вещь не найдена!");
        }
        if (item.getName() != null) {
            foundItemOptional.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            foundItemOptional.get().setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            foundItemOptional.get().setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(foundItemOptional.get()));
    }

    @Override
    public Optional<ItemInfoDto> getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).get();
        if (item != null) {
            LocalDateTime nowMoment = LocalDateTime.now();
            List<Booking> lastBookings = bookingRepository.getLastByItemId(item.getId(), nowMoment);
            List<Booking> nextBookings = bookingRepository.getNextByItemId(item.getId(), nowMoment);
            List<Comment> foundComments = commentRepository.findAll();
            List<ItemInfoDto.CommentDto> foundCommentDto = new ArrayList<>();
            for (Comment comment : foundComments) {
                foundCommentDto.add(CommentMapper.toCommentDto(comment));
            }
            Booking lastBooking = null;
            Booking nextBooking = null;
            if (lastBookings.size() != 0) {
                lastBooking = lastBookings.get(0);
            }
            if (nextBookings.size() != 0) {
                nextBooking = nextBookings.get(0);
            }
            if ((long) item.getOwnerId() == userId) {
                return Optional.of(ItemMapper.toItemInfoDto(item, lastBooking, nextBooking, foundCommentDto));
            } else {
                return Optional.of(ItemMapper.toItemInfoDto(item, null, null, foundCommentDto));
            }
        } else {
            log.info("Вещь с id {} не найдена!", itemId);
            return Optional.empty();
        }
    }

    @Override
    public List<ItemInfoDto> getItemsListByUserId(Long userId, PageRequest pageRequest) {
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<ItemInfoDto> userItemsDto = new ArrayList<>();
        LocalDateTime nowMoment = LocalDateTime.now();
        for (Item item : userItems) {
            List<Booking> lastBookings = bookingRepository.getLastByItemId(item.getId(), nowMoment);
            List<Booking> nextBookings = bookingRepository.getNextByItemId(item.getId(), nowMoment);
            List<Comment> foundComments = commentRepository.findAll();
            List<ItemInfoDto.CommentDto> foundCommentDto = new ArrayList<>();
            for (Comment comment : foundComments) {
                foundCommentDto.add(CommentMapper.toCommentDto(comment));
            }
            Booking lastBooking = null;
            Booking nextBooking = null;
            if (lastBookings.size() != 0) {
                lastBooking = lastBookings.get(0);
            }
            if (nextBookings.size() != 0) {
                nextBooking = nextBookings.get(0);
            }
            userItemsDto.add(ItemMapper.toItemInfoDto(item,
                    lastBooking,
                    nextBooking, foundCommentDto));
        }
        return userItemsDto;
    }

    @Override
    public List<ShortItemDto> getItemsListBySearch(String text, PageRequest pageRequest) {
        List<ShortItemDto> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        List<Item> foundItemsInRepository = itemRepository.getAll(pageRequest);
        for (Item item : foundItemsInRepository) {
            String searchText = text.toLowerCase();
            String nameToLowerCase = item.getName().toLowerCase();
            String descriptionToLowerCase = item.getDescription().toLowerCase();
            if (nameToLowerCase.contains(searchText) || descriptionToLowerCase.contains(searchText)) {
                if (item.getAvailable()) {
                    foundItems.add(ItemMapper.toShortItemDto(item));
                }
            }
        }
        return foundItems;
    }

    @Override
    @Transactional
    public ItemInfoDto.CommentDto createComment(ItemInfoDto.CommentDto commentDto, Long userID, Long itemId) {
        LocalDateTime created = LocalDateTime.now();
        User author = userRepository.findById(userID).get();
        Item itemForComment = itemRepository.findById(itemId).get();
        List<Booking> bookings = bookingRepository.getLastBookings(itemId, userID, created);
        if (bookings.size() == 0) {
            throw new CommentCreateException("Создание комментария невозможно, так как не найдены автор, вещь или бронирования");
        }
        boolean approveBooking = false;
        for (Booking booking : bookings) {
            if (booking.getStatus() == Status.APPROVED) {
                approveBooking = true;
            }
        }
        if (!approveBooking) {
            throw new CommentCreateException("Создание комментария невозможно, так как не найдены автор, вещь или бронирования");
        }
        if (author == null || itemForComment == null) {
            throw new CommentCreateException("Создание комментария невозможно, так как не найдены автор, вещь или бронирования");
        }
        Comment commentForSave = CommentMapper.toComment(commentDto, author, itemForComment, created);
        return CommentMapper.toCommentDto(commentRepository.save(commentForSave));
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null) {
            throw new ItemAvailableIsNullException("Поле available = null");
        }
    }
}
