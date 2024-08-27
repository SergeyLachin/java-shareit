package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ItemServise {

    @Service
    @AllArgsConstructor
    public static class ItemService {
        private final ItemStorage itemStorage;
        private final UserStorage userStorage;
        private final ItemMapper mapper;

//        @Autowired
//        public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, ItemMapper itemMapper) {
//            this.itemStorage = itemStorage;
//            this.mapper = itemMapper;
//        }

        public ItemDto create(ItemDto itemDto, Integer ownerId) {
            if (userStorage.getUserById(ownerId) == null) {
                throw new ObjectNotFoundException("Нет пользователя с таким Id");
            }
            return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
        }

        public List<ItemDto> getItemsByOwner(Integer ownderId) {
            return itemStorage.getItemsByOwner(ownderId).stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }

        public ItemDto getItemById(Integer id) {
            return mapper.toItemDto(itemStorage.getItemById(id));
        }

        public ItemDto update(ItemDto itemDto, Integer ownerId, Integer itemId) {
            if (itemDto.getId() == null) {
                itemDto.setId(itemId);
            }
            Item oldItem = itemStorage.getItemById(itemId);
            if (!oldItem.getOwner().equals(ownerId)) {
                throw new ObjectNotFoundException("У пользователя нет такой вещи!");
            }
            return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
        }

        public ItemDto delete(Integer itemId, Integer ownerId) {
            Item item = itemStorage.getItemById(itemId);
            if (!item.getOwner().equals(ownerId)) {
                throw new ObjectNotFoundException("У пользователя нет такой вещи!");
            }
            return mapper.toItemDto(itemStorage.delete(itemId));
        }

        public void deleteItemsByOwner(Integer ownderId) {
            itemStorage.deleteItemsByOwner(ownderId);
        }

        public List<ItemDto> getItemsBySearchQuery(String text) {
            text = text.toLowerCase();
            return itemStorage.getItemsBySearchQuery(text).stream()
                    .map(mapper::toItemDto)
                    .collect(toList());
        }
    }
}
