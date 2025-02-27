package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.Exclude;
import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "\"User\"")
public class User implements Serializable {
    public enum Role {
        UNDEFINED, USER, ADMIN,
        ;

        public static String toString(Role role) {
            if (role == null) {
                role = UNDEFINED;
            }
            return switch (role) {
                case USER -> "Пользователь";
                case ADMIN -> "Администратор";
                default -> "Неопределенная";
            };
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class UnauthorizedUser implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private static final double GSON_VERSION = 1.0;

        @NonNull
        private String name;
        @NonNull
        private String password;
        private Role role;
        private Person person;

        public static UnauthorizedUser fromJson(String json) {
            return new GsonHelper(GSON_VERSION).fromJson(json, UnauthorizedUser.class);
        }

        public static String toJson(UnauthorizedUser user) {
            return new GsonHelper(GSON_VERSION).toJson(user);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String name;
    @Exclude
    private Long passwordHash;
    @Exclude
    private String passwordSalt;
    @Enumerated
    private Role role;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private Person person;

    public static User fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, User.class);
    }

    public static List<User> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<User>>(){});
    }

    public static String toJson(User user) {
        return new GsonHelper(GSON_VERSION).toJson(user);
    }

    public static String toJsonList(List<User> users) {
        return new GsonHelper(GSON_VERSION).toJson(users, new TypeToken<List<User>>(){});
    }
}
