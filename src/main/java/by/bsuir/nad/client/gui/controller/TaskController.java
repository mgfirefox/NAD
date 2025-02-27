package by.bsuir.nad.client.gui.controller;

import by.bsuir.nad.client.gui.alert.ConfirmationAlert;
import by.bsuir.nad.client.model.Model;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;

@Getter
public abstract class TaskController extends Controller {
    @NonNull
    private final Model model;

    private Task<Void> task;

    public TaskController(@NonNull Model model) {
        super(model);
        this.model = model;
    }

    public void startTask(Task<Void> task) {
        this.task = task;
        task.setOnCancelled(_ -> cancelTask());
        task.setOnFailed(_ -> {
            Throwable exception = task.getException();
            stopTask();
            if (exception != null) {
                throw new RuntimeException(exception);
            }
        });

        Thread thread = new Thread(task, getClass().getSimpleName() + " Task-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public void cancelTask() {
        if (isTaskStarted()) {
            task.cancel(false);
            task = null;
        }
    }

    public void stopTask() {
        if (isTaskStarted()) {
            if (!task.isDone()) {
                task.cancel();
            }
            task = null;
        }
    }

    public boolean isTaskStarted() {
        return task != null;
    }

    public boolean isTaskCancelRequested() {
        ConfirmationAlert alert = new ConfirmationAlert("Предыдущее действие все еще в процессе выполнения. Хотите ли вы отменить его и выполнить выбранное действие?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == OK;
    }

    public boolean isRunningTaskCancelRequested() {
        if (isTaskStarted()) {
            if (isTaskCancelRequested()) {
                cancelTask();
                return true;
            }
            return false;
        }
        return true;
    }
}
