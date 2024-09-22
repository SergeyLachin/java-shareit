package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank(message = "Наименование элемента отсутствует.")
    private String name;

    @NotBlank(message = "Описание элемента пустое.")
    private String description;

    @NotNull(message = "Доступность вещи не указана.")
    private Boolean available;

    private List<Long> requestId;
}