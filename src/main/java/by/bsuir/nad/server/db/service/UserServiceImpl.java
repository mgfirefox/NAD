package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.dao.PersonDao;
import by.bsuir.nad.server.db.dao.PersonDaoImpl;
import by.bsuir.nad.server.db.dao.UserDao;
import by.bsuir.nad.server.db.dao.UserDaoImpl;
import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.server.db.exception.UserPersistenceException;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import jakarta.persistence.*;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class UserServiceImpl extends ServiceImpl<User, Long> implements UserService {
    private static final int SEED_SIZE = 32;
    private static final int SALT_SIZE = 16;

    private static final int ORIGIN_CHAR = 33;
    private static final int BOUND_CHAR = 127;

    @NonNull
    private final UserDao userDao;
    @NonNull
    private final PersonDao personDao;

    public UserServiceImpl() {
        this(new UserDaoImpl(), new PersonDaoImpl());
    }

    public UserServiceImpl(@NonNull UserDao userDao, @NonNull PersonDao personDao) {
        super(userDao);
        this.userDao = userDao;
        this.personDao = personDao;
    }

    @Override
    public User add(UnauthorizedUser unauthorizedUser) throws UserPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = userDao.createEntityManager();
            personDao.setEntityManager(entityManager);
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                userDao.findByName(unauthorizedUser.getName());
                transaction.rollback();
                throw new UserPersistenceException("User with this name already exists");
            } catch (NoResultException e) {
                try {
                    personDao.findByNonPrimaryKey(unauthorizedUser.getPerson());
                    throw new UserPersistenceException("User with this person already exists");
                } catch (NoResultException ex) {
                    Person person = unauthorizedUser.getPerson();
                    personDao.insert(person);

                    Long personId = personDao.findByNonPrimaryKey(person);
                    person.setId(personId);

                    User user = new User();
                    user.setName(unauthorizedUser.getName());
                    user.setPasswordSalt(generateSalt());
                    user.setPasswordHash(generateHash(unauthorizedUser.getPassword(), user.getPasswordSalt()));
                    user.setRole(unauthorizedUser.getRole());
                    user.setPerson(person);
                    userDao.insert(user);

                    Long userId = userDao.findByNonPrimaryKey(user);
                    user.setId(userId);
                    transaction.commit();

                    return user;
                }
            }
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            userDao.closeEntityManager();
        }
    }

    @Override
    public User find(UnauthorizedUser unauthorizedUser) throws UserPersistenceException {
        EntityManager entityManager;
        EntityTransaction transaction = null;
        try {
            entityManager = userDao.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            try {
                User user = userDao.findByName(unauthorizedUser.getName());
                transaction.commit();
                if (user.getPasswordHash() != generateHash(unauthorizedUser.getPassword(), user.getPasswordSalt())) {
                    transaction.rollback();
                    throw new UserPersistenceException("Wrong password");
                }
                return user;
            } catch (NoResultException e) {
                transaction.rollback();
                throw new UserPersistenceException("User with this name is not found", e);
            }
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            userDao.closeEntityManager();
            personDao.closeEntityManager();
        }
    }

    public long generateHash(String password, String passwordSalt) {
        HashFunction hashFunction = Hashing.sha512();
        HashCode hashCode = hashFunction.hashString(password + passwordSalt, StandardCharsets.UTF_8);
        return hashCode.asLong();
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        random.setSeed(random.generateSeed(SEED_SIZE));

        char[] salt = new char[SALT_SIZE];
        int[] ints = random.ints(SALT_SIZE, ORIGIN_CHAR, BOUND_CHAR).toArray();
        for (int i = 0; i < SALT_SIZE; i++) {
            salt[i] = (char) ints[i];
        }
        return new String(salt);
    }
}
