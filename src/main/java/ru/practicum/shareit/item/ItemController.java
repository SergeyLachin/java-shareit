package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER = "X-Sharer-User-Id";
    private ItemServise.ItemService itemService;
    private UserService userService;


    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение вещи с ID={}", itemId);
        return itemService.getItemById(itemId);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Integer ownerId) {
        log.info("Получен POST-запрос к эндпоинту: '/items' на добавление вещи владельцем с ID={}", ownerId);
        ItemDto newItemDto = null;
//        if (isExistUser(ownerId)) {
//            newItemDto = itemService.create(itemDto, ownerId);
//        }
//        return newItemDto;

        if (!isExistUser(ownerId)) {
            throw new ObjectNotFoundException("Получен POST-запрос к эндпоинту: '/items' на добавление вещи c несуществующим владельцем");
        }
        newItemDto = itemService.create(itemDto, ownerId);
        return newItemDto;
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Integer ownerId) {
        log.info("Получен GET-запрос к эндпоинту: '/items' на получение всех вещей владельца с ID={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Integer itemId,
                          @RequestHeader(OWNER) Integer ownerId) {
        log.info("Получен PATCH-запрос к эндпоинту: '/items' на обновление вещи с ID={}", itemId);
        ItemDto newItemDto = null;
        if (isExistUser(ownerId)) {
            newItemDto = itemService.update(itemDto, ownerId, itemId);
        }
        return newItemDto;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Integer itemId, @RequestHeader(OWNER) Integer ownerId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/items' на удаление вещи с ID={}", itemId);
        return itemService.delete(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Получен GET-запрос к эндпоинту: '/items/search' на поиск вещи с текстом={}", text);
        return itemService.getItemsBySearchQuery(text);
    }

    public boolean isExistUser(Integer userId) {
        boolean exist = false;
        if (userService.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }

    public void deleteItemsByUser(Integer userId) {
        itemService.deleteItemsByOwner(userId);
    }
}