package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemServise {

    ItemStorage itemStorage;

    public List<Item> getAllItem() {
        return itemStorage.getItems();
    }

    public void addItem(Item item) {
        itemStorage.createItem(item);
    }

    public Item getItemById(Integer id) {
        return itemStorage.getItemById(id);
    }

    public void updateItem(Integer id, Item item) {
        itemStorage.updatetem(id, item);
    }

    public void deleteItemById(Integer id) {
        itemStorage.getItemById(id);
    }
}

