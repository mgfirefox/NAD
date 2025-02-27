package by.bsuir.nad.client.gui.alert;

import by.bsuir.nad.tcp.Error;
import javafx.scene.control.Alert;
import lombok.NonNull;

public class ErrorAlert extends Alert {
    public ErrorAlert(@NonNull Error error) {
        this(error.getMessage());
    }

    public ErrorAlert(String message) {
        super(AlertType.ERROR);
        setTitle("Ошибка");
        setHeaderText(null);
        setContentText(message);
    }
}
