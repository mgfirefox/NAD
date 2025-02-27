package by.bsuir.nad.server.db.entity;

import by.bsuir.nad.gson.GsonHelper;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Person implements Serializable {
    public enum Gender {
        UNDEFINED, MALE, FEMALE;

        public static String toString(Gender gender) {
            if (gender == null) {
                gender = UNDEFINED;
            }
            return switch (gender) {
                case MALE -> "Мужской";
                case FEMALE -> "Женский";
                default -> "Неопределенный";
            };
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private static final double GSON_VERSION = 1.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    @Enumerated
    private Gender gender;
    private String phoneNumber;
    private String email;

    public static Person fromJson(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, Person.class);
    }

    public static List<Person> fromJsonList(String json) {
        return new GsonHelper(GSON_VERSION).fromJson(json, new TypeToken<List<Person>>(){});
    }

    public static String toJson(Person person) {
        return new GsonHelper(GSON_VERSION).toJson(person);
    }

    public static String toJsonList(List<Person> persons) {
        return new GsonHelper(GSON_VERSION).toJson(persons, new TypeToken<List<Person>>(){});
    }
}
