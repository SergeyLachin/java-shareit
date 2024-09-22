package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotValidParameterException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoByOwner;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ItemService {
    private final ItemRepository itemDao;
    private final UserRepository userDao;
    private final BookingRepository bookingDao;
    private final CommentRepository commentDao;
    private final ItemRequestRepository itemRequestDao;

    public ItemDto createItem(ItemDto dto, Long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        Item item = ItemMapper.toItem(dto, doRequests(dto));
        item.setOwner(user);

        Item savedItem = itemDao.save(item);
        log.info("Добавлена вещь {}", savedItem);
        return ItemMapper.doItemDto(item);
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        Item oldItem = itemDao.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

        Item item = ItemMapper.toItem(dto, doRequests(dto));
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }

        item.setId(itemId);
        item.setOwner(user);

        Item newItem = itemDao.save(item);
        log.info("Обновлена вещь {}", newItem);
        return ItemMapper.doItemDto(newItem);
    }

    @Transactional(readOnly = true)
    public ItemDtoByOwner findItemById(long userId, long itemId) {
        Item item = itemDao.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена."));
        List<Comment> comments = commentDao.findByItemId(itemId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsBeforeAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);
        List<Booking> nextBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsAfterAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);

        log.info("Найдена вещь с айди {}", itemId);
        return ItemMapper.doItemDtoByOwner(item, lastBookings, nextBookings, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDtoByOwner> findAll(long userId) {
        List<Item> userItems = itemDao.findItemsByOwnerId(userId);
        List<Comment> comments = commentDao.findByItemIdIn(userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        LocalDateTime now = LocalDateTime.now();

        log.info("Найден список вещей пользователя с айди {}", userId);
        return userItems.stream()
                .map(item -> ItemMapper.doItemDtoByOwner(item,
                        bookingDao.findByItemIdAndItemOwnerIdAndStartIsBeforeAndStatusIsNot(item.getId(), userId, now,
                                BookingStatus.REJECTED),
                        bookingDao.findByItemIdAndItemOwnerIdAndStartIsAfterAndStatusIsNot(item.getId(), userId, now,
                                BookingStatus.REJECTED),
                        comments))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemDto> findItemByDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Найден список вещей по текстовому запросу {}", text);
        return itemDao.findByAvailableTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text, text)
                .stream()
                .map(ItemMapper::doItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userDao.findById(userId).orElseThrow(() -> new AlreadyExistException("Пользователь не найден."));
        Item item = itemDao.findById(itemId).orElseThrow(() -> new NotValidParameterException("Вещь не найдена."));
        Booking booking = bookingDao
                .findTopByStatusNotLikeAndBookerIdAndItemIdOrderByEndAsc(BookingStatus.REJECTED, userId, itemId);
        Comment comment = CommentMapper.toComment(commentDto, user, item);

        if (booking == null) {
            throw new NotValidParameterException(String
                    .format("Пользователь %s не пользовался вещью %s.", user.getName(), item.getName()));
        }
        if (comment.getCreated().isBefore(booking.getEnd())) {
            throw new NotValidParameterException("Необходимо завершить аренду вещи для написания комментария.");
        }
        return CommentMapper.toCommentDto(commentDao.save(comment));
    }

    public void removeItemById(long userId, long itemId) {
        itemDao.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Вещь с не найдена."));
        checkItemAccess(itemDao, userId, itemId);
        itemDao.deleteById(itemId);
        log.info("Удалена вещь с айди {}", itemId);
    }

    private List<ItemRequest> doRequests(ItemDto dto) {
        List<ItemRequest> requests = new ArrayList<>();
        if (dto.getRequestId() != null) {
            for (Long requestId: dto.getRequestId()) {
                requests.add(itemRequestDao.findById(requestId)
                        .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден.")));
            }
        }
        return requests;
    }

    public static void checkItemAvailability(ItemRepository itemDao, long itemId) {
        if (!itemDao.existsById(itemId)) {
            throw new ObjectNotFoundException("Вещь с указанным айди не найдена.");
        }
    }

    public static void checkItemAccess(ItemRepository itemDao, long userId, long itemId) {
        Item item = itemDao.getReferenceById(itemId);
        Long ownerId = item.getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new ObjectNotFoundException("Редактирование вещи доступно только владельцу.");
        }
    }
}