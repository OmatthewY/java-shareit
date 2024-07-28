package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE i.available = true " +
            "AND (i.name ILIKE CONCAT('%', :text, '%') " +
            "OR i.description ILIKE CONCAT('%', :text, '%'))")
    List<Item> searchByText(@Param("text") String text);

    void deleteByIdAndOwnerId(long userId, long itemId);
}
