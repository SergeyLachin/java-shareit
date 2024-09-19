package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

//@Component
//@Mapper
//public interface BookingMapper {
//
//    @Mapping(target = "itemId", ignore = true)
//    BookingDto toBookingDto(Booking booking);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "item", ignore = true)
//    @Mapping(target = "booker", ignore = true)
//    Booking toBooking(BookingDto bookingDto);
//}

@Component
public class BookingMapper {

    public  BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

        public  Booking toBooking(BookingDto bookingDto) {
            return new Booking(
                    bookingDto.getId(),
                    bookingDto.getItem(),
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    bookingDto.getItem(),
                    bookingDto.getBooker(),
                    bookingDto.getStatus()
            );
//        private long id;
//        private long itemId;
//        private LocalDateTime start;
//        private LocalDateTime end;
//        private ItemDto item;
//        private UserDto booker;
//        private BookingStatus status;
    }
}
