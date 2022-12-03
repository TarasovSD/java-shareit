package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * from items where owner_id = ?1 order by id", nativeQuery = true)
    List<Item> findAllByOwnerId(Long ownerId, PageRequest pageRequest);

    List<Item> findAllByRequestId(Long requestId);

    @Query(value = "select * from items", nativeQuery = true)
    List<Item> getAll(PageRequest pageRequest);
}
