package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.PersonEditorMenuModel;
import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
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
public class PersonEditorMenuController extends TaskController {
    private final String title = "Меню обработки личных данных";

    @NonNull
    private final PersonEditorMenuModel model;

    @FXML
    private TableView<Person> table;
    @FXML
    private VBox editorMenu;

    @FXML
    private TextField id;
    @FXML
    private TextField lastName;
    @FXML
    private TextField firstName;
    @FXML
    private TextField middleName;
    private ToggleGroup genderRadioButtonGroup;
    @FXML
    private RadioButton maleGenderRadioButton;
    @FXML
    private RadioButton femaleGenderRadioButton;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField email;

    private ListChangeListener<Person> listChangeListener;

    public PersonEditorMenuController() {
        this(new PersonEditorMenuModel());
    }

    public PersonEditorMenuController(@NonNull PersonEditorMenuModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeTable(url, resourceBundle);
        initializeGenderRadioButtonGroup(url, resourceBundle);
    }

    @SuppressWarnings("unchecked")
    private void initializeTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<Person, ?>> columns = table.getColumns();
        ((TableColumn<Person, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<Person, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getLastName()));
        ((TableColumn<Person, String>) columns.get(2)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getFirstName()));
        ((TableColumn<Person, String>) columns.get(3)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getMiddleName()));
        ((TableColumn<Person, String>) columns.get(4)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(Gender.toString(cellDataFeatures.getValue().getGender())));
        ((TableColumn<Person, String>) columns.get(5)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPhoneNumber()));
        ((TableColumn<Person, String>) columns.get(6)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getEmail()));
    }

    private void initializeGenderRadioButtonGroup(URL url, ResourceBundle resourceBundle) {
        genderRadioButtonGroup = new ToggleGroup();
        ObservableList<Toggle> genderToggles = genderRadioButtonGroup.getToggles();
        genderToggles.add(maleGenderRadioButton);
        genderToggles.add(femaleGenderRadioButton);
    }

    @FXML
    private void onGetButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setPerson(null);
                model.sendPerson(ServerRequestType.GET);

                model.receivePersons();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onEditButtonAction(ActionEvent event) {
        Person person = table.getSelectionModel().getSelectedItem();
        if (person == null) {
            new ErrorAlert("Нет выбранных личных данных для редактирования").showAndWait();
            return;
        }

        id.setText(String.valueOf(person.getId()));
        lastName.setText(person.getLastName());
        firstName.setText(person.getFirstName());
        middleName.setText(person.getMiddleName());
        switch (person.getGender()) {
            case MALE -> maleGenderRadioButton.setSelected(true);
            case FEMALE -> femaleGenderRadioButton.setSelected(true);
        }
        phoneNumber.setText(person.getPhoneNumber());
        email.setText(person.getEmail());

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
        editorMenu.setDisable(false);
    }

    @FXML
    private void onEditConfirmButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setPerson(getPerson());
                model.sendPerson(ServerRequestType.EDIT);

                model.receivePersons();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getItems().isEmpty()) {
            new ErrorAlert("Нет личных данных для удаления").showAndWait();
            return;
        }
        Person person = table.getSelectionModel().getSelectedItem();
        if (person == null) {
            new ErrorAlert("Нет выбранных личных данных для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setPerson(person);
                model.sendPerson(ServerRequestType.REMOVE);

                model.receivePersons();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<Person> persons = model.getPersons();
            if (persons == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                table.setItems(FXCollections.observableList(persons));
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

    private Person getPerson() {
        Person person = new Person();
        person.setId(Long.parseLong(id.getText()));
        person.setLastName(lastName.getText());
        person.setFirstName(firstName.getText());
        person.setMiddleName(middleName.getText());
        person.setGender(getGender());
        person.setPhoneNumber(phoneNumber.getText());
        person.setEmail(email.getText());
        return person;
    }

    private Gender getGender() {
        Toggle genderSelectedToggle = genderRadioButtonGroup.getSelectedToggle();
        if (genderSelectedToggle == maleGenderRadioButton) {
            return Gender.MALE;
        }
        if (genderSelectedToggle == femaleGenderRadioButton) {
            return Gender.FEMALE;
        }
        return Gender.UNDEFINED;
    }

    private void clearEditorMenu() {
        id.clear();
        lastName.clear();
        firstName.clear();
        middleName.clear();
        genderRadioButtonGroup.getToggles().forEach(toggle -> toggle.setSelected(false));
        phoneNumber.clear();
        email.clear();
    }
}
