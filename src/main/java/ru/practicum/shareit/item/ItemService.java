package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemStorage;
import ru.practicum.shareit.user.repo.UserStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) {
        if (userId.isPresent() && userId.get() > 0) {
            if (userStorage.getUserById(userId.get()) == null) {
                throw new NoSuchElementException("пользователь не существует");
            }
            if (checkName(itemDto.getName()) && checkDescription(itemDto.getDescription())
                    && itemDto.getAvailable() != null) {
                Item item = ItemMapper.toItem(itemDto);
                item.setUserId(userId.get());
                return ItemMapper.toItemDto(itemStorage.createItem(item));
            }
            throw new ObjectNotFoundException("У вещи неправильно заданы параметры:" + itemDto);
        }
        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
//
//        if (userId.isPresent() && userId.get() > 0) {
//            if (userRepository.findById(userId.get()).isEmpty()) {
//                throw new NoSuchElementException("пользователь не существует");
//            }
//            Item item = itemMapper.toItem(itemDto);
//            item.setOwner(userRepository.findById(userId.get()).get());
//            item.setComments(new ArrayList<>());
//            return ItemMapper.toItemDto(itemRepository.save(item), null, null, new ArrayList<>());
//        }
//        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
    }

    public Boolean checkName(String name) {
        if (name != null && !name.isEmpty()) {
            return true;
        }
        throw new ObjectNotFoundException("имя не должно быть пустым");
    }

    public Boolean checkDescription(String desc) {
        if (desc != null) {
            return true;
        }
        throw new ObjectNotFoundException("описание не должно быть пустым");
    }

    public ItemDto updateItem(Optional<Long> userId, Long itemId, ItemDto itemDto) {
        if (userId.isPresent() && userId.get() > 0) {
            Item item = ItemMapper.toItem(itemDto);
            log.info("вещь для редактирования:" + item);
            if (itemStorage.getItemOfId(itemId).getUserId().equals(userId.get())) {
                return ItemMapper.toItemDto(itemStorage.updateItem(itemId, item));
            }
            throw new NoSuchElementException("нельзя редактировать чужие вещи!");
        }
        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
    }

    public List<ItemDto> getItems(Optional<Long> userId) {
        if (userId.isPresent() && userId.get() > 0) {
            List<Item> its = itemStorage.getItems(userId.get());
            log.info("вещи пользователя:" + userId + its);
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(ItemMapper.toItemDto(item));
            }
            return list;
        }
        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
    }

    public ItemDto getItemOfId(Long userId, Long itemId) {
        if (userId > 0 && itemId > 0) {
            return ItemMapper.toItemDto(itemStorage.getItemOfId(itemId));
        }
        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
    }

    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) {
        if (userId.isPresent() && userId.get() > 0) {
            if (text == null || text.isEmpty()) return new ArrayList<>();
            List<Item> its = itemStorage.getItemOfText(text);
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(ItemMapper.toItemDto(item));
            }
            return list;
        }
        throw new ObjectNotFoundException("идентификатор пользователя отрицательный или отсутствует");
    }
}