package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.UserEditorMenuModel;
import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.Role;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.tcp.Error;
import by.bsuir.nad.tcp.ServerRequest.ServerRequestType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
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
public class UserEditorMenuController extends TaskController {
    private final String title = "Меню обработки данных пользователей";

    @NonNull
    private final UserEditorMenuModel model;

    @FXML
    private TableView<User> table;
    @FXML
    private VBox editorMenu;

    @FXML
    private TextField name;
    @FXML
    private PasswordField password;
    private ToggleGroup roleRadioButtonGroup;
    @FXML
    private RadioButton userRoleRadioButton;
    @FXML
    private RadioButton adminRoleRadioButton;

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

    public UserEditorMenuController() {
        this(new UserEditorMenuModel());
    }

    public UserEditorMenuController(@NonNull UserEditorMenuModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeUsersTable(url, resourceBundle);
        initializeRoleRadioButtonGroup(url, resourceBundle);
        initializeGenderRadioButtonGroup(url, resourceBundle);
    }

    @SuppressWarnings("unchecked")
    private void initializeUsersTable(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableColumn<User, ?>> columns = table.getColumns();
        ((TableColumn<User, Long>) columns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getId()));
        ((TableColumn<User, String>) columns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getName()));
        ((TableColumn<User, String>) columns.get(2)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(Role.toString(cellDataFeatures.getValue().getRole())));

        TableColumn<User, ?> personColumn = columns.get(3);
        ObservableList<TableColumn<User, ?>> personColumns = personColumn.getColumns();
        ((TableColumn<User, Long>) personColumns.get(0)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getId()));
        ((TableColumn<User, String>) personColumns.get(1)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getLastName()));
        ((TableColumn<User, String>) personColumns.get(2)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getFirstName()));
        ((TableColumn<User, String>) personColumns.get(3)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getMiddleName()));
        ((TableColumn<User, String>) personColumns.get(4)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(Gender.toString(cellDataFeatures.getValue().getPerson().getGender())));
        ((TableColumn<User, String>) personColumns.get(5)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getPhoneNumber()));
        ((TableColumn<User, String>) personColumns.get(6)).setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(cellDataFeatures.getValue().getPerson().getEmail()));
    }

    private void initializeRoleRadioButtonGroup(URL url, ResourceBundle resourceBundle) {
        roleRadioButtonGroup = new ToggleGroup();
        ObservableList<Toggle> roleToggles = roleRadioButtonGroup.getToggles();
        roleToggles.add(userRoleRadioButton);
        roleToggles.add(adminRoleRadioButton);
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
                model.setUnauthorizedUser(null);
                model.setUser(null);
                model.sendUser(ServerRequestType.GET);

                model.receiveUsers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onAddConfirmButtonAction(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setUnauthorizedUser(getUnauthorizedUser());
                model.setUser(null);
                model.sendUnauthorizedUser(ServerRequestType.ADD);

                model.receiveUsers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    @FXML
    private void onRemoveButtonAction(ActionEvent event) {
        if (table.getItems().isEmpty()) {
            new ErrorAlert("Нет данных пользователей для удаления").showAndWait();
            return;
        }
        User user = table.getSelectionModel().getSelectedItem();
        if (user == null) {
            new ErrorAlert("Нет выбранных данных пользователя для удаления").showAndWait();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setUnauthorizedUser(null);
                model.setUser(user);
                model.sendUser(ServerRequestType.REMOVE);

                model.receiveUsers();

                return null;
            }
        };
        onProcessButtonAction(task);
    }

    private void onProcessButtonAction(Task<Void> task) {
        task.setOnSucceeded(_ -> {
            List<User> users = model.getUsers();
            if (users == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                table.setItems(FXCollections.observableList(users));
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
        unauthorizedUser.setRole(getRole());
        unauthorizedUser.setPerson(getPerson());
        return unauthorizedUser;
    }

    private Role getRole() {
        Toggle roleSelectedToggle = roleRadioButtonGroup.getSelectedToggle();
        if (roleSelectedToggle == userRoleRadioButton) {
            return Role.USER;
        }
        if (roleSelectedToggle == adminRoleRadioButton) {
            return Role.ADMIN;
        }
        return Role.UNDEFINED;
    }

    private Person getPerson() {
        Person person = new Person();
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
        name.clear();
        password.clear();
        roleRadioButtonGroup.getToggles().forEach(toggle -> toggle.setSelected(false));

        lastName.clear();
        firstName.clear();
        middleName.clear();
        genderRadioButtonGroup.getToggles().forEach(toggle -> toggle.setSelected(false));
        phoneNumber.clear();
        email.clear();
    }
}
