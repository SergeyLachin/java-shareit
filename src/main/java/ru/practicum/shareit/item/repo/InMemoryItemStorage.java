package ru.practicum.shareit.item.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage{

    Map<Integer, Item> itemMap = new HashMap<>();

    List<Item>items = new ArrayList<>();

    @Override
    public Item createItem(Item item) {
        itemMap.put(item.getId(), item);
        log.info("Создана вещь {} c id {}", item.getName(),item.getId());
        return  item;
    }

    @Override
    public void updatetem(Integer id, Item item) {
        itemMap.put(id, item);
        log.info("Вещь {} c id {} обновлена", item.getName(),id);
    }

    @Override
    public Item getItemById(Integer id) {
        return itemMap.get(id);
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public void deleteItems() {
        itemMap.clear();
    }
}
