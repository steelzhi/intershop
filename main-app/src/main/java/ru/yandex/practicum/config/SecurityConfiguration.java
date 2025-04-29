package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.UserRepository;
import ru.yandex.practicum.service.R2dbcUserDetailsService;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("", "/", "/item", "/items/**", "/main/items", "/image/**").permitAll()
                        .anyExchange().authenticated()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler((exchange, authentication) ->
                                        exchange.getExchange().getSession()
                                                .flatMap(WebSession::invalidate) // удаляем сессию
                                                .then(Mono.fromRunnable(() -> {
                                                    exchange.getExchange().getResponse()
                                                            .setStatusCode(HttpStatus.OK); // отвечаем 200 OK
                                                }))
                                )
                )
                .build();
    }

    @Bean
    public ReactiveUserDetailsService r2dbcUserDetailsService(UserRepository userRepository) {
        return new R2dbcUserDetailsService(userRepository);
    }
}