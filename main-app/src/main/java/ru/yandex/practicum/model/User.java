package ru.yandex.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.practicum.enums.Roles;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("users")
public class User {
    @Id
    int id;
    String username;
    String password;

/*    @Column("role_id")
    int roleId;*/
    Roles role;

/*    public User(String username, String password, Roles role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }*/

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /*    public User(String username, String password, int roleId) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }*/

    /*public User(int id, String username, String password, int roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }*/
}
