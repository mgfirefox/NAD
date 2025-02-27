package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.server.db.exception.EntityPersistenceException;
import by.bsuir.nad.server.main.ServerApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @BeforeAll
    static void configure() {
        if (ServerApplication.getContext() == null) {
            ServerApplication.setContext(new ClassPathXmlApplicationContext("server-context.xml"));
        }
    }

    @Test
    void addTwoSameUsers() {
        Person person = new Person();
        person.setLastName("test");
        person.setFirstName("test");
        person.setMiddleName("test");
        person.setGender(Gender.MALE);
        person.setPhoneNumber("+375291435945");
        person.setEmail("test@test.com");

        UnauthorizedUser user = new UnauthorizedUser();
        user.setName("test");
        user.setPassword("test");
        user.setRole(User.Role.ADMIN);
        user.setPerson(person);

        UserService userService = new UserServiceImpl();
        assertThrows(EntityPersistenceException.class, () -> {
            userService.add(user);
            userService.add(user);
        });
    }

    @Test
    void addTwoUsersWithSamePerson() {
        Person person = new Person();
        person.setLastName("test1");
        person.setFirstName("test1");
        person.setMiddleName("test1");
        person.setGender(Gender.MALE);
        person.setPhoneNumber("+375291435945");
        person.setEmail("test@test.com");

        UnauthorizedUser user = new UnauthorizedUser();
        user.setName("test1");
        user.setPassword("test1");
        user.setRole(User.Role.ADMIN);
        user.setPerson(person);

        UserService userService = new UserServiceImpl();
        assertThrows(EntityPersistenceException.class, () -> {
            userService.add(user);
            user.setName("test2");
            userService.add(user);
        });
    }
}