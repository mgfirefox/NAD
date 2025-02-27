package by.bsuir.nad.server.db.service;

import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.server.db.exception.UserPersistenceException;

public interface UserService extends Service<User, Long> {
    User add(UnauthorizedUser unauthorizedUser) throws UserPersistenceException;
    User find(UnauthorizedUser unauthorizedUser) throws UserPersistenceException;
}
