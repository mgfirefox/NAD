package by.bsuir.nad.client.gui.scene.loader;

import by.bsuir.nad.client.gui.controller.Controller;
import by.bsuir.nad.client.model.UserModel;
import by.bsuir.nad.server.db.entity.User;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import lombok.NonNull;

import java.io.IOException;
import java.net.URL;

public class UserSceneLoader extends SceneLoaderImpl {
    @NonNull
    private final User currentUser;

    public UserSceneLoader(@NonNull URL fxml, @NonNull final User currentUser) throws IllegalArgumentException {
        this(fxml, true, currentUser);
    }

    public UserSceneLoader(@NonNull URL fxml, boolean recoverable, @NonNull User currentUser) throws IllegalArgumentException {
        super(fxml, recoverable);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.load();
            Controller controller = fxmlLoader.getController();
            if (!(controller.getModel() instanceof UserModel)) {
                throw new IllegalArgumentException("FXML Controller Model is not a UserModel");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load scene from URL resource", e);
        }

        this.currentUser = currentUser;
    }

    @Override
    public void load(@NonNull Stage stage) throws IOException {
        super.load(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getFxml());
        stage.setScene(fxmlLoader.load());

        Controller controller = fxmlLoader.getController();
        UserModel model = (UserModel) controller.getModel();
        model.setCurrentUser(currentUser);
    }
}
