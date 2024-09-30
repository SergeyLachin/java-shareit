package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(Long ownerId);

    @Query(" select i from Item i " +
            "where lower(i.name) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    List<Item> getItemsBySearchQuery(@Param("search") String text);

}