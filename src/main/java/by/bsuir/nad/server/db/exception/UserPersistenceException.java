package by.bsuir.nad.server.db.exception;

public class UserPersistenceException extends EntityPersistenceException {
    public UserPersistenceException() {
        super();
    }

    public UserPersistenceException(String message) {
        super(message);
    }

    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPersistenceException(Throwable cause) {
        super(cause);
    }

    public UserPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
