package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDenied;
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

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingDao;
    private final CommentRepository commentDao;
    private final ItemRequestRepository itemRequestDao;

    public ItemDto createItem(ItemDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        Item item = ItemMapper.toItem(dto, doRequests(dto));
        item.setOwner(user);

        Item savedItem = itemRepository.save(item);
        log.info("Added item {} with id {}", savedItem.getName(), savedItem.getId());
        return ItemMapper.doItemDto(item);
    }

    public ItemDto updateItem(ItemDto dto, long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));

        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена."));

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

        Item newItem = itemRepository.save(item);
        log.info("Updated item {} with id {}", newItem.getName(), newItem.getId());
        return ItemMapper.doItemDto(newItem);
    }

    @Transactional(readOnly = true)
    public ItemDtoByOwner findItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена."));
        List<Comment> comments = commentDao.findByItemId(itemId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsBeforeAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);
        List<Booking> nextBookings = bookingDao.findByItemIdAndItemOwnerIdAndStartIsAfterAndStatusIsNot(itemId, userId,
                now, BookingStatus.REJECTED);

        log.info("Found a thing with id {}", itemId);
        return doItemDtoByOwner(item, lastBookings, nextBookings, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDtoByOwner> findAll(long userId) {
        List<Item> userItems = itemRepository.findItemsByOwnerId(userId);
        List<Comment> comments = commentDao.findByItemIdIn(userItems.stream()
                .map(Item::getId)
                .collect(toList()));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> last = bookingDao.findAllByItemIdAndStartBeforeOrderByStartDesc(userId, now);
        List<Booking> next = bookingDao.findAllByItemIdAndStartAfterOrderByStartDesc(userId, now);

        log.info("Found a list of the user's belongings with id {}", userId);

        return doItemDtoByOwnerList(userItems,last, next, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> findItemByDescription(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            return itemRepository.getItemsBySearchQuery(text).stream()
                    .map(ItemMapper::doItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AlreadyExistException("Пользователь не найден."));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotValidParameterException("Вещь не найдена."));
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

    public void removeItemById(long userId, long itemId) throws AccessDenied {
        itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Вещь с не найдена."));
        checkItemAccess(itemRepository, userId, itemId);
        itemRepository.deleteById(itemId);
        log.info("The item was deleted from the ID {}", itemId);
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
            throw new ObjectNotFoundException("Вещь с id" + itemId + "не найдена.");
        }
    }

    public static void checkItemAccess(ItemRepository itemDao, long userId, long itemId) throws AccessDenied {
        Item item = itemDao.getReferenceById(itemId);
        Long ownerId = item.getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new AccessDenied("Пользователю с id" + userId + " запрещен доступ к вещи с id " + itemId);
        }
    }

    public static ItemDtoByOwner doItemDtoByOwner(Item item, List<Booking> lastBookings, List<Booking> nextBookings,
                                                  List<Comment> comments) {
        return new ItemDtoByOwner(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ?
                        item.getRequest().stream().map(ItemRequest::getId).collect(Collectors.toList()) : null,
                bookingLast(lastBookings) != null ? BookingMapper.doBookingDto(bookingLast(lastBookings)) : null,
                bookingNext(nextBookings) != null ? BookingMapper.doBookingDto(bookingNext(nextBookings)) : null,
                commentDto(comments)
        );
    }

    private List<ItemDtoByOwner> doItemDtoByOwnerList(List<Item> its, List<Booking> lastBookings, List<Booking> nextBookings,
                                                      List<Comment> comments) {
        List<ItemDtoByOwner> list = new ArrayList<>();
        for (Item item : its) {
            list.add(doItemDtoByOwner(item, lastBookings, nextBookings, comments));
        }
        return list;
    }

    public static Booking bookingLast(List<Booking> lastBookings) {
        return lastBookings.stream().max(Comparator.comparing(Booking::getStart)).orElse(null);
    }

    public static Booking bookingNext(List<Booking> nextBookings) {
        return nextBookings.stream().min(Comparator.comparing(Booking::getStart)).orElse(null);
    }

    public static List<CommentDto> commentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }
}