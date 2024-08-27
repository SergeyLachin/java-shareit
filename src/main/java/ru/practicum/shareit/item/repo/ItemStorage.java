package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item delete(Integer userId);

    List<Item> getItemsByOwner(Integer ownerId);

    List<Item> getItemsBySearchQuery(String text);

    void deleteItemsByOwner(Integer ownderId);

    Item getItemById(Integer itemId);
}
