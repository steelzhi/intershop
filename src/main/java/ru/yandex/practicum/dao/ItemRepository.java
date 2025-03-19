package ru.yandex.practicum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.ItemDto;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemDto, Integer> {

    Page<ItemDto> findAllByOrderById(PageRequest page);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(String name, String description);

    @Query("""
            SELECT COUNT(id)
            FROM ItemDto
            """)
    int getItemListSize();
}
