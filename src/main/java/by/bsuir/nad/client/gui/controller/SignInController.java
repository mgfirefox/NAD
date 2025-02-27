package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.UserSceneLoader;
import by.bsuir.nad.client.model.SignInModel;
import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.tcp.Error;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.NonNull;

import java.net.URL;
import java.util.Objects;

@Getter
public class SignInController extends TaskController {
    private final String title = "Вход в учетную запись";

    @NonNull
    private final SignInModel model;

    @FXML
    private TextField name;
    @FXML
    private PasswordField password;

    public SignInController() {
        this(new SignInModel());
    }

    public SignInController(@NonNull SignInModel model) {
        super(model);
        this.model = model;
    }

    @FXML
    private void onSignInButtonAction(ActionEvent event) {
        if (!isRunningTaskCancelRequested()) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setUnauthorizedUser(getUnauthorizedUser());
                model.sendUnauthorizedUser();

                model.receiveUser();

                return null;
            }
        };
        task.setOnSucceeded(_ -> {
            User user = model.getUser();
            if (user == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                URL fxml;
                switch (user.getRole()) {
                    case USER -> fxml = ClientApplication.class.getResource("user-menu.fxml");
                    case ADMIN -> fxml = ClientApplication.class.getResource("admin-menu.fxml");
                    default -> fxml = null;
                }

                StageHistory history = ClientApplication.getStageHistory();
                history.push(new UserSceneLoader(Objects.requireNonNull(fxml), user));
                history.loadCurrentScene();
            }
            stopTask();
        });
        startTask(task);
    }

    @FXML
    private void onBackButtonAction(ActionEvent event) {
        if (!isRunningTaskCancelRequested()) {
            return;
        }

        StageHistory history = ClientApplication.getStageHistory();
        history.pop();
        history.loadCurrentScene();
    }

    private UnauthorizedUser getUnauthorizedUser() {
        UnauthorizedUser unauthorizedUser = new UnauthorizedUser();
        unauthorizedUser.setName(name.getText());
        unauthorizedUser.setPassword(password.getText());
        return unauthorizedUser;
    }
}
