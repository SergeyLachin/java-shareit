package ru.practicum.shareit.request;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    private Integer id;                  // уникальный идентификатор запроса
    private String description;       // текст запроса, содержащий описание требуемой вещи
    private String requestorName;     // пользователь, создавший запрос
    private LocalDateTime created;
}
