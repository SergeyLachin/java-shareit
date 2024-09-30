package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;


import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.State;
import ru.practicum.shareit.exception.AccessDenied;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotValidParameterException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.shareit.item.ItemService.checkItemAccess;
import static ru.practicum.shareit.item.ItemService.checkItemAvailability;
import static ru.practicum.shareit.user.UserService.checkUserAvailability;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingDao;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingOutputDto createBooking(BookingDto dto, Long userId) {
        Long itemId = dto.getItemId();
        validationBookingPeriod(dto);
        checkUserAvailability(userRepository, userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException("Вещь с указанным айди не найдена."));
        if (!item.getAvailable()) {
            throw new NotValidParameterException("Вещь уже забронирована.");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NoSuchElementException("Владелец вещи не может её забронировать.");
        }
        dto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(dto, item, userRepository.getReferenceById(userId));
        return BookingMapper.doBookingOutputDto(bookingDao.save(booking));
    }

    @Transactional
    public BookingOutputDto confirmBookingByOwner(Long userId, Long bookingId, boolean approved) throws AccessDenied {
        checkUserAvailability(userRepository, userId);
        Booking booking = bookingDao.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException("Бронирование с указанным айди не найдено."));
        Long itemId = booking.getItem().getId();
        checkItemAvailability(itemRepository, itemId);
        checkItemAccess(itemRepository, userId, itemId);

        if (approved && booking.getStatus() == BookingStatus.APPROVED) {
            throw new NotValidParameterException("Бронирование уже подтверждено.");
        }
        if (!approved && booking.getStatus() == BookingStatus.REJECTED) {
            throw new NotValidParameterException("Бронирование уже отклонено.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.doBookingOutputDto(bookingDao.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingOutputDto findBookingById(Long userId, Long bookingId) {
        Booking booking = bookingDao.findById(bookingId).orElseThrow(() ->
                new ObjectNotFoundException("Бронирование с указанным айди не найдено."));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        boolean checkOwnerOrBooker = ownerId.equals(userId) || bookerId.equals(userId);
        if (!checkOwnerOrBooker) {
            throw new ObjectNotFoundException("Получение данных доступно либо автору бронирования, либо владельцу вещи");
        }
        return BookingMapper.doBookingOutputDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingOutputDto> findAllUsersBooking(Long userId, String state) {
        checkUserAvailability(userRepository, userId);
        LocalDateTime start = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        checkEnumExist(state);
        State bookingStatus = State.valueOf(state.toUpperCase());

        switch (bookingStatus) {
            case ALL:
                bookings = bookingDao.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingDao.findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, start, start);
                break;
            case PAST:
                bookings = bookingDao.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, start);
                break;
            case FUTURE:
                bookings = bookingDao.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, start);
                break;
            case WAITING:
                bookings = bookingDao.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingDao.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
        }
        return BookingMapper.makeBookingsOutputList(bookings);
    }

    public List<BookingOutputDto> findAllBookingsForItems(Long userId, String state) {
        checkUserAvailability(userRepository, userId);
        if (itemRepository.findItemsByOwnerId(userId).isEmpty()) {
            throw new ObjectNotFoundException("У пользователя нет вещей.");
        }
        LocalDateTime start = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        checkEnumExist(state);
        State bookingStatus = State.valueOf(state.toUpperCase());

        switch (bookingStatus) {
            case ALL:
                bookings = bookingDao.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingDao.findByItemOwnerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, start, start);
                break;
            case PAST:
                bookings = bookingDao.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, start);
                break;
            case FUTURE:
                bookings = bookingDao.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, start);
                break;
            case WAITING:
                bookings = bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
        }
        return BookingMapper.makeBookingsOutputList(bookings);
    }

    private void validationBookingPeriod(BookingDto booking) {
        LocalDateTime end = booking.getEnd();
        LocalDateTime start = booking.getStart();
        if (!end.isAfter(start) || end.equals(start)) {
            throw new NotValidParameterException("Дата окончания бронирования раньше даты начала или равней ей.");
        }
    }

    private void checkEnumExist(String state) {
        for (State available : State.values()) {
            if (available.name().equals(state)) {
                return;
            }
        }
        throw new AlreadyExistException("Unknown state: UNSUPPORTED_STATUS");
    }
}