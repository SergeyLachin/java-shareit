package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    Integer id;
    @NotBlank
    String name;
    String description;
    Boolean available;
    Integer owner;
    Integer request;
}
