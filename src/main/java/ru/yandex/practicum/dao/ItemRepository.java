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

    List<ItemDto> findAllByOrderById();

    List<ItemDto> findByNameContainingOrDescriptionContainingOrderById(String name, String description);

    List<ItemDto> findByNameContainingOrDescriptionOrderByName(String name, String description);

    List<ItemDto> findByNameContainingOrDescriptionOrderByPrice(String name, String description);

/*    @NativeQuery("""
            UPDATE items
            SET amount = :newAmount
            WHERE id = :id
            """)*/
/*    @Query("""
            UPDATE ItemDto
            SET amount = :newAmount
            WHERE id = :id
            """)
    void changeCount(int id, int newAmount);*/

}
