package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.model.NoConnectionModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.net.SocketException;

@Getter
public class NoConnectionController extends Controller {
    private final String title = "Нет соединения";

    @NonNull
    private final NoConnectionModel model;

    public NoConnectionController() {
        this(new NoConnectionModel());
    }

    public NoConnectionController(@NonNull NoConnectionModel model) {
        super(model);
        this.model = model;
    }

    @FXML
    private void onReconnectButtonAction(ActionEvent event) throws IOException {
        try {
            model.getClientSocket().openIfClosed();

            StageHistory history = ClientApplication.getStageHistory();
            history.pop();
            history.loadCurrentScene();
        } catch (SocketException _) {
            new ErrorAlert("Не удалось подключиться к Серверу").showAndWait();
        }
    }
}
