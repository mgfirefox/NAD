package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ConfirmationAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.UserSceneLoader;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.AdminMenuModel;
import by.bsuir.nad.client.model.UserMenuModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

@Getter
public class UserMenuController extends Controller {
    private final String title = "Меню пользователя";

    @NonNull
    private final UserMenuModel model;

    public UserMenuController() {
        this(new UserMenuModel());
    }

    public UserMenuController(@NonNull UserMenuModel model) {
        super(model);
        this.model = model;
    }

    @FXML
    private void onSupplyEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("supply-editor-menu.fxml")), model.getCurrentUser()));
        history.loadCurrentScene();
    }

    @FXML
    private void onSupplierEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("supplier-editor-menu.fxml")), model.getCurrentUser()));
        history.loadCurrentScene();
    }

    @FXML
    private void onProductEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("product-editor-menu.fxml")), model.getCurrentUser()));
        history.loadCurrentScene();
    }

    @FXML
    private void onManufacturerEditorMenuButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new UserSceneLoader(Objects.requireNonNull(ClientApplication.class.getResource("manufacturer-editor-menu.fxml")), model.getCurrentUser()));
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
