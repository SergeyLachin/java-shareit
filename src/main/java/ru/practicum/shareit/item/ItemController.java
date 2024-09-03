package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @Valid @RequestBody ItemDto dto) {
        log.info("Запрос на добавление вещи {} владельцем {}", dto, userId);
        return itemService.createItem(dto, userId);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @RequestBody ItemDto dto,
                              @PathVariable Long itemId) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрос на просмотр вещи с id {}", itemId);
        return itemService.getItemOfId(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("Запрос на просмотр своих вещей пользователем с id {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @RequestParam("text") String text) {
        log.info("Запрос на просмотр {} для аренды", text);
        return itemService.getItemOfText(userId, text);
    }
}