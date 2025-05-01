package ru.yandex.practicum.dao;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {

    @Query("""
            SELECT id,
                username,
                password,
                role
            FROM users
            WHERE username = :username
            LIMIT 1
            """)
    Mono<User> findByUsername(String username);

}
