package by.bsuir.nad.client.main;

import by.bsuir.nad.client.ClientSocket;
import by.bsuir.nad.client.gui.scene.StageHistory;
import by.bsuir.nad.client.gui.scene.loader.SceneLoaderImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.SocketException;
import java.util.Objects;

public class ClientApplication extends Application {
    @Getter
    private static ApplicationContext context = null;
    @Getter
    private static StageHistory stageHistory = null;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        context = new ClassPathXmlApplicationContext("client-context.xml");
        ClientSocket clientSocket = context.getBean("clientSocket", ClientSocket.class);

        stageHistory = new StageHistory(primaryStage);
        stageHistory.push(new SceneLoaderImpl(Objects.requireNonNull(getClass().getResource("welcome.fxml"))));
        //stageHistory.push(new SceneLoaderImpl(Objects.requireNonNull(getClass().getResource("person-editor-menu.fxml"))));
        stageHistory.loadCurrentScene();

        Thread.setDefaultUncaughtExceptionHandler((_, e) -> {
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof SocketException) {
                    clientSocket.closeIfOpened();

                    stageHistory.push(new SceneLoaderImpl(Objects.requireNonNull(ClientApplication.this.getClass().getResource("no-connection.fxml")), false));
                    stageHistory.loadCurrentScene();
                    return;
                }
                cause = cause.getCause();
            }

            e.printStackTrace();
        });

        primaryStage.setOnCloseRequest(_ -> clientSocket.closeIfOpened());
        primaryStage.show();
    }
}