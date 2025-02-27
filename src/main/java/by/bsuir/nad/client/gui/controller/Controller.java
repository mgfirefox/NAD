package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Getter
public abstract class Controller implements Initializable {
    private final String title = "";

    @NonNull
    private final Model model;

    @FXML
    private Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
