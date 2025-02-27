package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.SceneLoaderImpl;
import by.bsuir.nad.client.model.WelcomeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.util.Objects;

@Getter
public class WelcomeController extends Controller {
    private final String title = "Добро пожаловать";

    @NonNull
    private final WelcomeModel model;

    public WelcomeController() {
        this(new WelcomeModel());
    }

    public WelcomeController(@NonNull WelcomeModel model) {
        super(model);
        this.model = model;
    }

    @FXML
    private void onSignInButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new SceneLoaderImpl(Objects.requireNonNull(ClientApplication.class.getResource("sign-in.fxml")), false));
        history.loadCurrentScene();
    }

    @FXML
    private void onSignUpButtonAction(ActionEvent event) {
        StageHistory history = ClientApplication.getStageHistory();
        history.push(new SceneLoaderImpl(Objects.requireNonNull(ClientApplication.class.getResource("sign-up.fxml")), false));
        history.loadCurrentScene();
    }
}
