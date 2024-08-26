package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemServise itemServise;

    @Validated
    @GetMapping
    public List<Item> getAllItem() {
        return itemServise.getAllItem();
    }

    @Validated
    @PostMapping
    public Item addItem(@RequestBody Item item) {
        log.info("addItem attempt {}", item);
        itemServise.addItem(item);
        log.info("addItem {} success", item);
        return item;
    }

    @Validated
    @PatchMapping("/{id}")
    public Item updateItem(@PathVariable @Positive Integer id, @RequestBody Item item) {
        if (item.getId() != null && id != item.getId()){
            throw  new ObjectNotFoundException("");
        }
        itemServise.updateItem(id, item);
        log.info("updateItem attempt {}", id);
        return itemServise.getItemById(id);
    }

    @Validated
    @GetMapping("/{id}")
    public Item getItemById(@PathVariable @Positive Integer id) {
        log.info("attempt to get item by id {}", id);
        return itemServise.getItemById(id);
    }

    @Validated
    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable @Positive Integer id) {
        itemServise.deleteItemById(id);
    }
}
