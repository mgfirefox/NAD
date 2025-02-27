package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ConfirmationAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.UserSceneLoader;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.AdminMenuModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

@Getter
public class AdminMenuController extends Controller {
    private final String title = "Меню администратора";

    @NonNull
    private final AdminMenuModel model;

    public AdminMenuController() {
        this(new AdminMenuModel());
    }

    public AdminMenuController(@NonNull AdminMenuModel model) {
        super(model);
        this.model = model;
    }

    @FXML
    private void onUserEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("user-editor-menu.fxml")), model.getCurrentUser()));
        history.loadCurrentScene();
    }

    @FXML
    private void onPersonEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("person-editor-menu.fxml")), model.getCurrentUser()));
        history.loadCurrentScene();
    }

    @FXML
    private void onBackButtonAction(ActionEvent event) {
        ConfirmationAlert alert = new ConfirmationAlert("Вы уверены, что хотите покинуть учетную запись?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == OK) {
            StageHistory history = ClientApplication.getStageHistory();
            history.pop();
            history.loadCurrentScene();
        }
    }
}
