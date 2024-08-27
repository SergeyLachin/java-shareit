package ru.practicum.shareit.item.repo;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private final UserService userService;
    public Map<Integer, Item> items;
    private Integer currentId;

    public InMemoryItemStorage(UserService userService) {
        currentId = 0;
        items = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Item create(Item item) {
        if (userService.getUserById(item.getOwner()) == null) {
            throw new ValidationException("Попытка создания вещи с несуществующим пользователем!");
        }
        if (isValidItem(item)) {
            item.setId(++currentId);
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!items.containsKey(item.getId())) {
            throw new ObjectNotFoundException("Вещь с ID=" + item.getId() + " не найдена!");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        if (isValidItem(item)) {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item delete(Integer itemId) {
        if (itemId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!items.containsKey(itemId)) {
            throw new ObjectNotFoundException("Вещь с ID=" + itemId + " не найден!");
        }
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Integer ownerId) {
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public void deleteItemsByOwner(Integer ownerId) {
        List<Integer> deleteIds = new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .map(item -> item.getId())
                .collect(toList()));
        for (Integer deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }

    @Override
    public Item getItemById(Integer itemId) {
        if (!items.containsKey(itemId)) {
            throw new ObjectNotFoundException("Вещь с ID=" + itemId + " не найдена!");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    private boolean isValidItem(Item item) {
        if ((item.getName().isEmpty()) || (item.getDescription().isEmpty()) || (item.getAvailable() == null)) {
            throw new ValidationException("У вещи некорректные данные");
        }
        return true;
    }
}
