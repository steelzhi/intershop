package ru.yandex.practicum.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import org.springframework.data.r2dbc.repository.Query;

@Repository
public interface ItemRepository extends R2dbcRepository<ItemDto, Integer> {

    Flux<ItemDto> findAllByOrderById(PageRequest page);

    @Query("""
            SELECT id
            FROM items i
            WHERE i.name LIKE concat('%', :key, '%')
                OR i.description LIKE concat('%', :key, '%')
            ORDER by id
            """)
    Flux<Integer> findIdsByNameOrDescriptionOrderById(String key);

    @Query("""
            SELECT id
            FROM items i
            WHERE i.name LIKE concat('%', :key, '%')
                OR i.description LIKE concat('%', :key, '%')
            ORDER by i.name
            """)
    Flux<Integer> findIdsByNameOrDescriptionOrderByName(String key);

    @Query("""
            SELECT id
            FROM items i
            WHERE i.name LIKE concat('%', :key, '%')
                OR i.description LIKE concat('%', :key, '%')
            ORDER by i.price
            """)
    Flux<Integer> findIdsByNameOrDescriptionOrderByPrice(String key);

/*    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(String name, String description);

    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(String name, String description);

    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(String name, String description);*/

    @Query("""
            SELECT COUNT(id)
            FROM items
            """)
    Mono<Integer> getItemListSize();

    @Query("""
            SELECT id
            FROM items
            OFFSET ((:pageNumber - 1) * :itemsOnPage)
            LIMIT :itemsOnPage
            """)
    Flux<Integer> getAllItemIdsOnPage(int pageNumber, int itemsOnPage);
}
