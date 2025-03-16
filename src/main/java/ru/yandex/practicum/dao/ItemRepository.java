package ru.yandex.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.ItemDto;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemDto, Integer> {
    /*    @Query("""
                FROM ItemDto idt
                WHERE idt name LIKE CONCAT('%',:key,'%')
                ORDER BY :sort
                """)*/
/*    @NativeQuery("""
            SELECT *
            FROM items
            WHERE name LIKE CONCAT('%',:key,'%')
            ORDER BY :sort
            """)
    List<ItemDto> findByKeyLikeAndSort(String key, String sort);*/

    List<ItemDto> findByNameContainingOrderById(String name);

    List<ItemDto> findByNameContainingOrderByName(String name);

    List<ItemDto> findByNameContainingOrderByPrice(String name);

}
