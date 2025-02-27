package by.bsuir.nad.server.db.exception;

public class EntityPersistenceException extends Exception {
    public EntityPersistenceException() {
        super();
    }

    public EntityPersistenceException(String message) {
        super(message);
    }

    public EntityPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityPersistenceException(Throwable cause) {
        super(cause);
    }

    public EntityPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
