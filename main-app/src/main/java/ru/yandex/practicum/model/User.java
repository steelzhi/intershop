package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Column;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;
    String username;
    String password;

    @Column("role_id")
    int roleId;

/*    public User(String username, String password, int roleId) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }*/

    public User(int id, String username, String password, int roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }
}
