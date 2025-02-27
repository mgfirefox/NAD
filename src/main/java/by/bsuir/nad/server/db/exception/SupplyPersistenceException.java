package by.bsuir.nad.server.db.exception;

public class SupplyPersistenceException extends EntityPersistenceException {
    public SupplyPersistenceException() {
    }

    public SupplyPersistenceException(String message) {
        super(message);
    }

    public SupplyPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SupplyPersistenceException(Throwable cause) {
        super(cause);
    }

    public SupplyPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
