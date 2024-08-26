package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    //@NonNull
    Integer id;
    @NotBlank
    //@NotEmpty
    String name;
    //@NotBlank
    //@NotEmpty
    String description;
    Boolean available;
    //@NotBlank
    //@NotEmpty
    Integer owner;
    //@NotBlank
    //@NotEmpty
    Integer request;
}
