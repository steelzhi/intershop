package ru.yandex.practicum.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.UserRepository;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.UserPrincipal;

public class R2dbcUserDetailsService implements ReactiveUserDetailsService {

    private UserRepository userRepository;

    public R2dbcUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<User> user = userRepository.findByUsername(username);
        return user
                .doOnNext(user1 -> System.out.println("User: " + user1))
                .flatMap(user1 -> {
                    if (user1 == null) {
                        throw new UsernameNotFoundException(username);
                    }
                    return Mono.just(new UserPrincipal(user1));
                });
    }
}