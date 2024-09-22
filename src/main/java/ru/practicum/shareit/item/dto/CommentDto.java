package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст комментария отсутствует.")
    private String text;

    private String authorName;

    private Long itemId;

    private LocalDateTime created;
}