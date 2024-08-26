package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem (Item item);

    void updatetem (Integer id, Item item);

    Item getItemById (Integer id);

    List<Item>getItems ();

    void deleteItems ();
}
