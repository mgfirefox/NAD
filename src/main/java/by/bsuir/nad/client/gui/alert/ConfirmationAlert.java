package by.bsuir.nad.client.gui.alert;

import javafx.scene.control.Alert;

public class ConfirmationAlert extends Alert {
    public ConfirmationAlert(String message) {
        super(AlertType.CONFIRMATION);
        setTitle("Подтверждение действия");
        setHeaderText(null);
        setContentText(message);
    }
}
