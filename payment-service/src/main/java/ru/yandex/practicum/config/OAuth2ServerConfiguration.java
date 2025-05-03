package ru.yandex.practicum.config;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class OAuth2ServerConfiguration {

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity security) throws Exception {
        return security
                .authorizeExchange(requests -> requests
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(serverSpec -> serverSpec
                        .jwt(jwtSpec -> {
                            ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
                            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
                                System.out.println("jwt.getTokenValue: " + jwt.getTokenValue());
                                LinkedTreeMap<String, Object> claims = (LinkedTreeMap<String, Object>) jwt.getClaims().get("realm_access");
                                List<String> roles = (List<String>) claims.get("roles");

                                return Flux.fromIterable(roles)
                                        .map(SimpleGrantedAuthority::new);
                            });

                            jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        })
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromIssuerLocation("http://localhost:8080/realms/master");
    }
}
