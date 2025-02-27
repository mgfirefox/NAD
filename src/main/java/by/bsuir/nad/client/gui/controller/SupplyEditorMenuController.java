package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.SupplyEditorMenuModel;
import by.bsuir.nad.server.db.entity.*;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static by.bsuir.nad.server.db.entity.Product.Unit;

@Getter
public class SupplyEditorMenuController extends TaskController {
    private final String title = "Меню обработки данных поставок";

    @NonNull
    private final SupplyEditorMenuModel model;

    @FXML
    private TreeTableView<Supply> table;
    @FXML
    private VBox editorMenu;
    @FXML
    private Label editorMenuTitle;
    @FXML
    private Button confirmButton;
    @FXML
    private Button addSupplyProductButton;

    @FXML
    private TextField id;
    @FXML
    private DatePicker deliveryDate;
    @FXML
    private DatePicker paymentDate;
    @FXML
    private ComboBox<Supplier> supplier;
    private ObservableList<Supplier> suppliers;
    @FXML
    private ComboBox<Manufacturer> manufacturer;
    private ObservableList<Manufacturer> manufacturers;
    @FXML
    private ComboBox<Product> manufacturerProduct;
    private ObservableList<Product> manufacturerProducts;
    @FXML
    private TableView<SupplyProduct> supplyProductsTable;
    @FXML
    private TextField cost;

    private ListChangeListener<TreeItem<Supply>> listChangeListener;

    public SupplyEditorMenuController() {
        this(new SupplyEditorMenuModel());
    }

    public SupplyEditorMenuController(@NonNull SupplyEditorMenuModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeTable(url, resourceBundle);
        initializeSupplier(url, resourceBundle);
        initializeManufacturer(url, resourceBundle);
        initializeManufacturerProduct(url, resourceBundle);
        initializeSupplyProductsTable(url, resourceBundle);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable(URL url, ResourceBundle resourceBundle) {
        table.setShowRoot(false);
        table.setRoot(new TreeItem<>());

        ObservableList<TreeTableColumn<Supply, ?>> columns = table.getColumns();
        ((TreeTableColumn<Supply, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getId()));

        TreeTableColumn<Supply, ?> supplierColumn = columns.get(1);
        ObservableList<TreeTableColumn<Supply, ?>> supplierColumns = supplierColumn.getColumns();
        ((TreeTableColumn<Supply, Long>) supplierColumns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getSupplier().getId()));
        ((TreeTableColumn<Supply, String>) supplierColumns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getSupplier().getName()));

        ((TreeTableColumn<Supply, Date>) columns.get(2)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getDeliveryDate()));
        ((TreeTableColumn<Supply, Date>) columns.get(3)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getPaymentDate()));
        ((TreeTableColumn<Supply, BigDecimal>) columns.get(4)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getValue().getCost()));
    }

    public void initializeSupplier(URL url, ResourceBundle resourceBundle) {
        model.receiveSuppliers();
        suppliers = FXCollections.observableArrayList(model.getSuppliers());

        supplier.setItems(suppliers);
        supplier.setConverter(new StringConverter<>() {
            @Override
            public String toString(Supplier supplier) {
                if (supplier == null) {
                    return "";
                }
                return supplier.getName();
            }

            @Override
            public Supplier fromString(String s) {
                if (s == null || s.isEmpty()) {
                    return null;
                }
                for (Supplier supplier : suppliers) {
                    if (supplier.getName().equals(s)) {
                        return supplier;
                    }
                }
                return null;
            }
        });
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

    public void initializeManufacturerProduct(URL url, ResourceBundle resourceBundle) {
        manufacturerProduct.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                if (product == null) {
                    return "";
                }
                return product.getName();
            }

            @Override
            public Product fromString(String s) {
                if (s == null || s.isEmpty()) {
                    return null;
                }
                for (Product product : manufacturerProducts) {
                    if (product.getName().equals(s)) {
                        return product;
                    }
                }
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initializeSupplyProductsTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<SupplyProduct, ?>> columns = supplyProductsTable.getColumns();
        ((TableColumn<SupplyProduct, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId().getProduct().getId()));
        ((TableColumn<SupplyProduct, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId().getProduct().getName()));

        TableColumn<SupplyProduct, ?> manufacturerColumn = columns.get(2);
        ObservableList<TableColumn<SupplyProduct, ?>> manufacturerColumns = manufacturerColumn.getColumns();
        ((TableColumn<SupplyProduct, Long>) manufacturerColumns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId().getProduct().getManufacturer().getId()));
        ((TableColumn<SupplyProduct, String>) manufacturerColumns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId().getProduct().getManufacturer().getName()));

        ((TableColumn<SupplyProduct, String>) columns.get(3)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(Unit.toString(cellDataFeatures.getValue().getId().getProduct().getUnit())));
        ((TableColumn<SupplyProduct, String>) columns.get(4)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(String.valueOf(cellDataFeatures.getValue().getAmount())));
        ((TableColumn<SupplyProduct, String>) columns.get(4)).setCellFactory(TextFieldTableCell.forTableColumn());
        ((TableColumn<SupplyProduct, String>) columns.get(4)).setOnEditCommit(event -> {
            try {
                Integer oldAmount = Integer.parseUnsignedInt(event.getOldValue());
                Integer newAmount = Integer.parseUnsignedInt(event.getNewValue());

                SupplyProduct supplyProduct = event.getRowValue();
                supplyProduct.setAmount(newAmount);
                BigDecimal pricePerUnit = supplyProduct.getPricePerUnit();

                BigDecimal oldCost = new BigDecimal(cost.getText());
                BigDecimal newCost = oldCost.add(pricePerUnit.multiply(new BigDecimal(newAmount - oldAmount)));

                cost.setText(newCost.toPlainString());
            } catch (NumberFormatException e) {
                new ErrorAlert("Введите корректное количество").showAndWait();
                event.consume();
                return;
            }
        });
        ((TableColumn<SupplyProduct, String>) columns.get(5)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(String.valueOf(cellDataFeatures.getValue().getPricePerUnit())));
        ((TableColumn<SupplyProduct, String>) columns.get(5)).setCellFactory(TextFieldTableCell.forTableColumn());
        ((TableColumn<SupplyProduct, String>) columns.get(5)).setOnEditCommit(event -> {
            BigDecimal oldPricePerUnit = new BigDecimal(event.getOldValue());
            BigDecimal newPricePerUnit = new BigDecimal(event.getNewValue());
            if (newPricePerUnit.compareTo(BigDecimal.ZERO) < 0) {
                new ErrorAlert("Введите корректную цену за единицу измерения").showAndWait();
                event.consume();
                return;
            }

            SupplyProduct supplyProduct = event.getRowValue();
            supplyProduct.setPricePerUnit(newPricePerUnit);
            Integer amount = supplyProduct.getAmount();

            BigDecimal oldCost = new BigDecimal(cost.getText());
            BigDecimal newCost = oldCost.add(newPricePerUnit.subtract(oldPricePerUnit).multiply(BigDecimal.valueOf(amount)));

            cost.setText(newCost.toPlainString());
        });
    }

    @FXML
    private void onGetButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setSupply(null);
                model.sendSupply(ServerRequestType.GET);

                model.receiveSupplies();

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
                model.setSupply(getSupply());
                model.sendSupply(ServerRequestType.ADD);

                model.receiveSupplies();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onEditButtonAction(ActionEvent event) {
        TreeItem<Supply> supplyTreeItem = table.getSelectionModel().getSelectedItem();
        if (supplyTreeItem == null) {
            new ErrorAlert("Нет выбранных данных поставки для редактирования").showAndWait();
            return;
        }
        Supply supply = supplyTreeItem.getValue();

        editorMenuTitle.setText("Редактирование");
        confirmButton.setText("Отредактировать");
        confirmButton.setOnAction(this::onEditConfirmButtonAction);

        id.setText(String.valueOf(supply.getId()));

        deliveryDate.setValue(supply.getDeliveryDate().toLocalDate());
        paymentDate.setValue(supply.getPaymentDate().toLocalDate());

        model.receiveSuppliers();
        suppliers = FXCollections.observableArrayList(model.getSuppliers());
        supplier.setValue(supply.getSupplier());
        supplier.setItems(suppliers);

        model.receiveManufacturers();
        manufacturers = FXCollections.observableArrayList(model.getManufacturers());
        manufacturer.getSelectionModel().clearSelection();
        manufacturer.setItems(manufacturers);

        supplyProductsTable.setItems(FXCollections.observableArrayList(supply.getSupplyProducts()));
        manufacturer.setValue(manufacturers.getFirst());

        cost.setText(supply.getCost().toPlainString());

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
                model.setSupply(getSupply());
                model.sendSupply(ServerRequestType.EDIT);

                model.receiveSupplies();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getRoot().getChildren().isEmpty()) {
            new ErrorAlert("Нет данных поставки для удаления").showAndWait();
            return;
        }
        Supply supply = table.getSelectionModel().getSelectedItem().getValue();
        if (supply == null) {
            new ErrorAlert("Нет выбранных данных поставки для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setSupply(supply);
                model.sendSupply(ServerRequestType.REMOVE);

                model.receiveSupplies();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<Supply> supplies = model.getSupplies();
            if (supplies == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                ObservableList<TreeItem<Supply>> tableChildren = table.getRoot().getChildren();
                tableChildren.clear();
                suppliers.forEach(_ -> supplies.forEach(supply -> tableChildren.add(new TreeItem<>(supply))));
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
    private void onAddSupplyProductButtonAction(ActionEvent event) {
        Manufacturer manufacturer = this.manufacturer.getValue();
        if (manufacturer == null) {
            new ErrorAlert("Выберите производителя").showAndWait();
            return;
        }
        Product manufacturerProduct = this.manufacturerProduct.getValue();
        if (manufacturerProduct == null) {
            new ErrorAlert("Выберите продукт производителя").showAndWait();
            return;
        }

        SupplyProduct.Id id = new SupplyProduct.Id();
        id.setProduct(manufacturerProduct);

        SupplyProduct supplyProduct = new SupplyProduct();
        supplyProduct.setId(id);
        supplyProduct.setAmount(0);
        supplyProduct.setPricePerUnit(new BigDecimal("0.00"));

        supplyProductsTable.getItems().add(supplyProduct);

        this.manufacturerProduct.getSelectionModel().clearSelection();
        manufacturerProducts.remove(manufacturerProduct);
        if (manufacturerProducts.isEmpty()) {
            addSupplyProductButton.setDisable(true);
        }
    }

    @FXML
    private void onRemoveSupplyProductButtonAction(ActionEvent event) {
        if (supplyProductsTable.getItems().isEmpty()) {
            new ErrorAlert("Нет данных поставок для удаления").showAndWait();
            return;
        }
        SupplyProduct supplyProduct = supplyProductsTable.getSelectionModel().getSelectedItem();
        if (supplyProduct == null) {
            new ErrorAlert("Нет выбранных данных поставок для удаления").showAndWait();
            return;
        }
        BigDecimal newCost = new BigDecimal(cost.getText()).subtract(new BigDecimal(supplyProduct.getAmount()).multiply(supplyProduct.getPricePerUnit()));

        supplyProductsTable.getItems().remove(supplyProduct);
        manufacturerProducts.add(supplyProduct.getId().getProduct());
        cost.setText(newCost.toPlainString());
        addSupplyProductButton.setDisable(false);
    }

    @FXML
    private void onManufacturerAction(ActionEvent event) {
        Manufacturer manufacturer = this.manufacturer.getValue();
        if (manufacturer == null) {
            manufacturerProduct.setDisable(true);
            return;
        }

        manufacturerProduct.getSelectionModel().clearSelection();

        model.receiveManufacturerProducts();
        List<Product> manufacturerProducts = model.getManufacturerProducts();
        int i = 0;
        while (i < manufacturerProducts.size()) {
            if (manufacturer.equals(manufacturerProducts.get(i).getManufacturer())) {
                i++;
                continue;
            }
            manufacturerProducts.remove(i);
        }
        ObservableList<SupplyProduct> supplyProducts = supplyProductsTable.getItems();
        supplyProducts.forEach(supplyProduct -> manufacturerProducts.remove(supplyProduct.getId().getProduct()));

        this.manufacturerProduct.getSelectionModel().clearSelection();
        this.manufacturerProducts = FXCollections.observableList(manufacturerProducts);
        manufacturerProduct.setItems(this.manufacturerProducts);
        manufacturerProduct.setDisable(false);
        if (!manufacturerProducts.isEmpty()) {
            addSupplyProductButton.setDisable(false);
        }
    }

    private Supply getSupply() {
        Supply supply = new Supply();
        if (!id.isDisabled()) {
            supply.setId(Long.parseLong(id.getText()));
        }
        supply.setSupplier(supplier.getValue());
        supply.setDeliveryDate(Date.valueOf(deliveryDate.getValue()));
        supply.setPaymentDate(Date.valueOf(paymentDate.getValue()));
        supply.setSupplyProducts(supplyProductsTable.getItems());
        supply.setCost(new BigDecimal(cost.getText()));
        return supply;
    }

    private void clearEditorMenu() {
        id.clear();

        deliveryDate.setValue(null);
        paymentDate.setValue(null);

        supplier.getSelectionModel().clearSelection();
        model.receiveSuppliers();
        suppliers = FXCollections.observableArrayList(model.getSuppliers());
        supplier.setItems(suppliers);

        manufacturer.getSelectionModel().clearSelection();
        model.receiveManufacturers();
        manufacturers = FXCollections.observableArrayList(model.getManufacturers());
        manufacturer.setItems(manufacturers);

        manufacturerProduct.getSelectionModel().clearSelection();
        manufacturerProducts = FXCollections.observableArrayList();
        manufacturerProduct.setItems(manufacturerProducts);

        cost.setText("0.00");

        supplyProductsTable.setItems(FXCollections.observableArrayList());
        addSupplyProductButton.setDisable(false);
    }
}
