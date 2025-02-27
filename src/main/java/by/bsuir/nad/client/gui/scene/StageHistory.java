package by.bsuir.nad.client.gui.scene;

import by.bsuir.nad.client.gui.scene.loader.SceneLoader;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Stack;

@RequiredArgsConstructor
public class StageHistory {
    @NonNull
    private final Stage stage;
    @NonNull
    private final Stack<@NonNull SceneLoader> history = new Stack<>();

    public void push(@NonNull SceneLoader loader) {
        history.push(loader);
    }

    public void pop() {
        history.pop();

        while (!history.isEmpty()) {
            if (history.peek().isRecoverable()) {
                break;
            }
            history.pop();
        }
    }

    public void loadCurrentScene() {
        if (history.isEmpty()) {
            throw new RuntimeException("Trying to load scene while StageHistory stack is empty");
        }
        try {
            history.peek().load(stage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene from URL resource", e);
        }
    }
}
