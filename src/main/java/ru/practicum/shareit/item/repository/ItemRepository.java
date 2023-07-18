package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId, PageRequest pageRequest);

    @Query(value = "select i from Item i where lower(i.name) like %?1% or lower(i.description) like %?1% " +
            "and i.available=true")
    List<Item> getItemsByText(String text, PageRequest pageRequest);

    List<Item> findAllByRequestIdIn(List<Long> requestsIds);

    List<Item> findByRequestId(long requestId);
}
