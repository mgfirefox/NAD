package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.PersonEditorMenuModel;
import by.bsuir.nad.client.model.SupplierEditorMenuModel;
import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
import by.bsuir.nad.server.db.entity.Supplier;
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
public class SupplierEditorMenuController extends TaskController {
    private final String title = "Меню обработки данных поставщиков";

    @NonNull
    private final SupplierEditorMenuModel model;

    @FXML
    private TableView<Supplier> table;
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

    private ListChangeListener<Supplier> listChangeListener;

    public SupplierEditorMenuController() {
        this(new SupplierEditorMenuModel());
    }

    public SupplierEditorMenuController(@NonNull SupplierEditorMenuModel model) {
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
        ObservableList<TableColumn<Supplier, ?>> columns = table.getColumns();
        ((TableColumn<Supplier, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<Supplier, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getName()));
    }

    @FXML
    private void onGetButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setSupplier(null);
                model.sendSupplier(ServerRequestType.GET);

                model.receiveSuppliers();

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
                model.setSupplier(getSupplier());
                model.sendSupplier(ServerRequestType.ADD);

                model.receiveSuppliers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onEditButtonAction(ActionEvent event) {
        Supplier supplier = table.getSelectionModel().getSelectedItem();
        if (supplier == null) {
            new ErrorAlert("Нет выбранных данных поставщика для редактирования").showAndWait();
            return;
        }

        editorMenuTitle.setText("Редактирование");
        confirmButton.setText("Отредактировать");
        confirmButton.setOnAction(this::onEditConfirmButtonAction);

        id.setText(String.valueOf(supplier.getId()));
        name.setText(supplier.getName());

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
                model.setSupplier(getSupplier());
                model.sendSupplier(ServerRequestType.EDIT);

                model.receiveSuppliers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getItems().isEmpty()) {
            new ErrorAlert("Нет данных поставщиков для удаления").showAndWait();
            return;
        }
        Supplier supplier = table.getSelectionModel().getSelectedItem();
        if (supplier == null) {
            new ErrorAlert("Нет выбранных данных поставщика для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setSupplier(supplier);
                model.sendSupplier(ServerRequestType.REMOVE);

                model.receiveSuppliers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<Supplier> suppliers = model.getSuppliers();
            if (suppliers == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                table.setItems(FXCollections.observableList(suppliers));
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

    private Supplier getSupplier() {
        Supplier supplier = new Supplier();
        if (!id.isDisabled()) {
            supplier.setId(Long.parseLong(id.getText()));
        }
        supplier.setName(name.getText());
        return supplier;
    }

    private void clearEditorMenu() {
        id.clear();
        name.clear();
    }
}
