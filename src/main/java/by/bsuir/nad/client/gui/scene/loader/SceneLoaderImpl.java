package by.bsuir.nad.client.gui.scene.loader;

import by.bsuir.nad.client.gui.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
@Getter
public class SceneLoaderImpl implements SceneLoader {
    @NonNull
    private final URL fxml;
    private final boolean recoverable;

    public SceneLoaderImpl(@NonNull final URL fxml) {
        this(fxml, true);
    }

    @Override
    public void load(@NonNull Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        stage.setScene(fxmlLoader.load());

        Controller controller = fxmlLoader.getController();
        stage.setTitle(controller.getTitle());
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}
