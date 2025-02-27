package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ErrorAlert;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.UserSceneLoader;
import by.bsuir.nad.client.main.ClientApplication;
import by.bsuir.nad.client.model.SignUpModel;
import by.bsuir.nad.server.db.entity.Person;
import by.bsuir.nad.server.db.entity.Person.Gender;
import by.bsuir.nad.server.db.entity.User;
import by.bsuir.nad.server.db.entity.User.Role;
import by.bsuir.nad.server.db.entity.User.UnauthorizedUser;
import by.bsuir.nad.tcp.Error;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.NonNull;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Getter
public class SignUpController extends TaskController {
    private final String title = "Регистрация учетной записи";

    @NonNull
    private final SignUpModel model;

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

    public SignUpController() {
        this(new SignUpModel());
    }

    public SignUpController(@NonNull SignUpModel model) {
        super(model);
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        initializeRoleRadioButtonGroup(url, resourceBundle);
        initializeGenderRadioButtonGroup(url, resourceBundle);
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
    private void onSignUpButtonAction(ActionEvent event) {
        if (!isRunningTaskCancelRequested()) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                model.setUnauthorizedUser(getUnauthorizedUser());
                model.sendUnauthorizedUser();

                model.receiveUser();

                return null;
            }
        };
        task.setOnSucceeded(_ -> {
            User user = model.getUser();
            if (user == null) {
                Error error = model.getError();
                new ErrorAlert(error).showAndWait();
            } else {
                URL fxml;
                switch (user.getRole()) {
                    case USER -> fxml = ClientApplication.class.getResource("user-menu.fxml");
                    case ADMIN -> fxml = ClientApplication.class.getResource("admin-menu.fxml");
                    default -> fxml = null;
                }

                StageHistory history = ClientApplication.getStageHistory();
                history.push(new UserSceneLoader(Objects.requireNonNull(fxml), user));
                history.loadCurrentScene();
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
}
