package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto doItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ?
                        item.getRequest().stream().map(ItemRequest::getId).collect(Collectors.toList()) : null
        );
    }

    public static Item toItem(ItemDto dto, List<ItemRequest> requests) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setRequest(!requests.isEmpty() ? requests : null);
        return item;
    }
}