package by.bsuir.nad.client.gui.scene.loader;

import javafx.stage.Stage;

import java.io.IOException;

public interface SceneLoader {
    void load(Stage stage) throws IOException;
    boolean isRecoverable();
}
