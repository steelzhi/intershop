package ru.yandex.practicum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;

import java.util.List;

@Repository
public interface ItemRepository extends R2dbcRepository<ItemDto, Integer> {

    Flux<ItemDto> findAll();

    Flux<ItemDto> findAllByOrderById(PageRequest page);

/*    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(String name, String description);

    List<ItemDto> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(String name, String description);*/

    @org.springframework.data.r2dbc.repository.Query("""
            SELECT COUNT(id)
            FROM items
            """)
    Mono<Integer> getItemListSize();
}
