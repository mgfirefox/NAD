package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.ManufacturerEditorMenuModel;
import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
public class ManufacturerEditorMenuController extends TaskController {
    private final String title = "Меню обработки данных производителей";

    @NonNull
    private final ManufacturerEditorMenuModel model;

    @FXML
    private TableView<Manufacturer> table;
    @FXML
    private VBox editorMenu;
    @FXML
    private Label editorMenuTitle;
    @FXML
    private Button confirmButton;

    @FXML
    private TextField id;
    @FXML
    private TextField name;

    private ListChangeListener<Manufacturer> listChangeListener;

    public ManufacturerEditorMenuController() {
        this(new ManufacturerEditorMenuModel());
    }

    public ManufacturerEditorMenuController(@NonNull ManufacturerEditorMenuModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeTable(url, resourceBundle);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<Manufacturer, ?>> columns = table.getColumns();
        ((TableColumn<Manufacturer, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<Manufacturer, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getName()));
    }

    @FXML
    private void onGetButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setManufacturer(null);
                model.sendManufacturer(ServerRequestType.GET);

                model.receiveManufacturers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onAddButtonAction(ActionEvent event) {
        table.getSelectionModel().clearSelection();

        editorMenuTitle.setText("Добавление");
        confirmButton.setText("Добавить");
        confirmButton.setOnAction(this::onAddConfirmButtonAction);

        clearEditorMenu();

        id.setDisable(true);
        editorMenu.setDisable(false);
    }

    @FXML
    private void onAddConfirmButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setManufacturer(getManufacturer());
                model.sendManufacturer(ServerRequestType.ADD);

                model.receiveManufacturers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onEditButtonAction(ActionEvent event) {
        Manufacturer manufacturer = table.getSelectionModel().getSelectedItem();
        if (manufacturer == null) {
            new ErrorAlert("Нет выбранных данных производителя для редактирования").showAndWait();
            return;
        }

        editorMenuTitle.setText("Редактирование");
        confirmButton.setText("Отредактировать");
        confirmButton.setOnAction(this::onEditConfirmButtonAction);

        id.setText(String.valueOf(manufacturer.getId()));
        name.setText(manufacturer.getName());

        listChangeListener = change -> {
            table.getSelectionModel().getSelectedItems().removeListener(listChangeListener);

            if (change.getList().isEmpty()) {
                editorMenu.setDisable(true);

                clearEditorMenu();
            } else {
                onEditButtonAction(event);
            }
        };
        table.getSelectionModel().getSelectedItems().addListener(listChangeListener);
        id.setDisable(false);
        editorMenu.setDisable(false);
    }

    private void onEditConfirmButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setManufacturer(getManufacturer());
                model.sendManufacturer(ServerRequestType.EDIT);

                model.receiveManufacturers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getItems().isEmpty()) {
            new ErrorAlert("Нет данных производителей для удаления").showAndWait();
            return;
        }
        Manufacturer manufacturer = table.getSelectionModel().getSelectedItem();
        if (manufacturer == null) {
            new ErrorAlert("Нет выбранных данных производителя для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setManufacturer(manufacturer);
                model.sendManufacturer(ServerRequestType.REMOVE);

                model.receiveManufacturers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<Manufacturer> manufacturers = model.getManufacturers();
            if (manufacturers == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                table.setItems(FXCollections.observableList(manufacturers));
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

    private Manufacturer getManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        if (!id.isDisabled()) {
            manufacturer.setId(Long.parseLong(id.getText()));
        }
        manufacturer.setName(name.getText());
        return manufacturer;
    }

    private void clearEditorMenu() {
        id.clear();
        name.clear();
    }
}
