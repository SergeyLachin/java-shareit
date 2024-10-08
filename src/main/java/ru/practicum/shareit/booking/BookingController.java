package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exception.AccessDenied;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private static final String OWNER = "X-Sharer-User-Id";

    @PostMapping
    public BookingOutputDto createBooking(@RequestHeader(OWNER) Long userId,
                                          @Valid @RequestBody BookingDto dto) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto confirmBookingByOwner(@RequestHeader(OWNER) Long userId,
                                                  @PathVariable Long bookingId, @RequestParam Boolean approved) throws AccessDenied {
        return bookingService.confirmBookingByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto findBookingById(@RequestHeader(OWNER) Long userId,
                                            @PathVariable Long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> findAllUsersBooking(@RequestHeader(OWNER) Long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.findAllUsersBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> findAllBookingsForItems(@RequestHeader(OWNER) Long userId,
                                                          @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.findAllBookingsForItems(userId, state);
    }
}