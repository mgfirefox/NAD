package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.ProductEditorMenuModel;
import by.bsuir.nad.server.db.entity.Manufacturer;
import by.bsuir.nad.server.db.entity.Product;
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
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static by.bsuir.nad.server.db.entity.Product.*;

@Getter
public class ProductEditorMenuController extends TaskController {
    private final String title = "Меню обработки данных продуктов";

    @NonNull
    private final ProductEditorMenuModel model;

    @FXML
    private TableView<Product> table;
    @FXML
    private VBox editorMenu;
    @FXML
    private Label editorMenuTitle;
    @FXML
    private Button confirmButton;
    @FXML
    private Button addManufacturerButton;

    @FXML
    private TextField id;
    @FXML
    private TextField name;
    @FXML
    private ComboBox<Manufacturer> manufacturer;
    private ObservableList<Manufacturer> manufacturers;
    @FXML
    private TableView<Manufacturer> manufacturersTable;
    @FXML
    private ComboBox<Unit> unit;

    private ListChangeListener<Product> listChangeListener;

    public ProductEditorMenuController() {
        this(new ProductEditorMenuModel());
    }

    public ProductEditorMenuController(@NonNull ProductEditorMenuModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeTable(url, resourceBundle);
        initializeManufacturer(url, resourceBundle);
        initializeManufacturersTable(url, resourceBundle);
        initializeUnit(url, resourceBundle);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<Product, ?>> columns = table.getColumns();
        ((TableColumn<Product, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<Product, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getName()));

        TableColumn<Product, ?> manufacturerColumn = columns.get(2);
        ObservableList<TableColumn<Product, ?>> manufacturerColumns = manufacturerColumn.getColumns();
        ((TableColumn<Product, Long>) manufacturerColumns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getManufacturer().getId()));
        ((TableColumn<Product, String>) manufacturerColumns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getManufacturer().getName()));

        ((TableColumn<Product, String>) columns.get(3)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(Unit.toString(cellDataFeatures.getValue().getUnit())));
    }

    public void initializeManufacturer(URL url, ResourceBundle resourceBundle) {
        model.receiveManufacturers();
        manufacturers = FXCollections.observableArrayList(model.getManufacturers());

        manufacturer.setItems(manufacturers);
        manufacturer.setConverter(new StringConverter<>() {
            @Override
            public String toString(Manufacturer manufacturer) {
                if (manufacturer == null) {
                    return "";
                }
                return manufacturer.getName();
            }

            @Override
            public Manufacturer fromString(String s) {
                if (s == null || s.isEmpty()) {
                    return null;
                }
                for (Manufacturer manufacturer : manufacturers) {
                    if (manufacturer.getName().equals(s)) {
                        return manufacturer;
                    }
                }
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeManufacturersTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<Manufacturer, ?>> columns = manufacturersTable.getColumns();
        ((TableColumn<Manufacturer, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<Manufacturer, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getName()));
    }

    private void initializeUnit(URL url, ResourceBundle resourceBundle) {
        Unit[] units = Unit.values();
        unit.setItems(FXCollections.observableArrayList(Arrays.copyOfRange(units, 1, units.length)));
        unit.setConverter(new StringConverter<>() {
            @Override
            public String toString(Unit unit) {
                if (unit == null) {
                    return "";
                }
                return Unit.toString(unit);
            }

            @Override
            public Unit fromString(String s) {
                return Unit.fromString(s);
            }
        });
    }

    @FXML
    private void onGetButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setProduct(null);
                model.sendProduct(ServerRequestType.GET);

                model.receiveProducts();

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
                model.setProduct(getProduct());
                model.sendProduct(ServerRequestType.ADD);

                model.receiveProducts();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onEditButtonAction(ActionEvent event) {
        Product product = table.getSelectionModel().getSelectedItem();
        if (product == null) {
            new ErrorAlert("Нет выбранных данных продукта для редактирования").showAndWait();
            return;
        }

        editorMenuTitle.setText("Редактирование");
        confirmButton.setText("Отредактировать");
        confirmButton.setOnAction(this::onEditConfirmButtonAction);

        id.setText(String.valueOf(product.getId()));
        name.setText(product.getName());

        model.receiveManufacturers();
        manufacturers = FXCollections.observableArrayList(model.getManufacturers());
        manufacturers.remove(product.getManufacturer());
        manufacturer.getSelectionModel().clearSelection();
        manufacturer.setItems(manufacturers);
        manufacturersTable.setItems(FXCollections.observableArrayList(product.getManufacturer()));
        unit.setValue(product.getUnit());
        addManufacturerButton.setDisable(true);

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
                model.setProduct(getProduct());
                model.sendProduct(ServerRequestType.EDIT);

                model.receiveProducts();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getItems().isEmpty()) {
            new ErrorAlert("Нет данных продуктов для удаления").showAndWait();
            return;
        }
        Product product = table.getSelectionModel().getSelectedItem();
        if (product == null) {
            new ErrorAlert("Нет выбранных данных продукта для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setProduct(product);
                model.sendProduct(ServerRequestType.REMOVE);

                model.receiveProducts();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<Product> products = model.getProducts();
            if (products == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                table.setItems(FXCollections.observableList(products));
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

    @FXML
    private void onAddManufacturerButtonAction(ActionEvent event) {
        Manufacturer manufacturer = this.manufacturer.getValue();
        if (manufacturer == null) {
            new ErrorAlert("Выберите производителя").showAndWait();
            return;
        }

        this.manufacturer.getSelectionModel().clearSelection();
        manufacturersTable.getItems().add(manufacturer);
        manufacturers.remove(manufacturer);
        addManufacturerButton.setDisable(true);
    }

    @FXML
    private void onRemoveManufacturerButtonAction(ActionEvent event) {
        if (manufacturersTable.getItems().isEmpty()) {
            new ErrorAlert("Нет данных производителей для удаления").showAndWait();
            return;
        }
        Manufacturer manufacturer = manufacturersTable.getSelectionModel().getSelectedItem();
        if (manufacturer == null) {
            new ErrorAlert("Нет выбранных данных производителя для удаления").showAndWait();
            return;
        }

        manufacturersTable.getItems().remove(manufacturer);
        manufacturers.add(manufacturer);
        addManufacturerButton.setDisable(false);
    }

    private Product getProduct() {
        Product product = new Product();
        if (!id.isDisabled()) {
            product.setId(Long.parseLong(id.getText()));
        }
        product.setName(name.getText());
        product.setManufacturer(manufacturersTable.getItems().getFirst());
        product.setUnit(unit.getValue());
        return product;
    }

    private void clearEditorMenu() {
        id.clear();
        name.clear();

        manufacturer.getSelectionModel().clearSelection();
        model.receiveManufacturers();
        manufacturers = FXCollections.observableArrayList(model.getManufacturers());
        manufacturer.setItems(manufacturers);

        manufacturersTable.setItems(FXCollections.observableArrayList());
        addManufacturerButton.setDisable(false);
        unit.getSelectionModel().clearSelection();
    }
}
