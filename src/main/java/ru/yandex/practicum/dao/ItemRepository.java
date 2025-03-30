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

    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(String name, String description);

    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(String name, String description);

    Flux<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(String name, String description);

    @Query("""
            SELECT COUNT(id)
            FROM items
            """)
    Mono<Integer> getItemListSize();
}
