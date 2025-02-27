package by.bsuir.nad.server.db.dao;

import by.bsuir.nad.server.db.entity.User;
import lombok.NonNull;

public interface UserDao extends Dao<User, Long> {
    User findByName(@NonNull String name);
}
