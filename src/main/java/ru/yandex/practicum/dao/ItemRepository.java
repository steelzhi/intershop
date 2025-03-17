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

    Page<ItemDto> findAllByOrderById(PageRequest page);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(String name, String description);

    @Query("""
            SELECT COUNT(id)
            FROM ItemDto
            """)
    int getItemListSize();

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
