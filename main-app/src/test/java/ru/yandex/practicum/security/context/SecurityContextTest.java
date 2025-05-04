/*
package ru.yandex.practicum.security.context;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SecurityContextTest {

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldHaveCorrectPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("testuser", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
*/
