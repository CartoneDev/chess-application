package core;

import controller.AController;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import java.util.HashMap;

public class StateManager {
    HashMap<String, AController> states;
    GameLoop gameLoop;
    static AController currentController;

    long currentTime = System.nanoTime();

    public static void init(Stage stage) {
        stage.setScene(currentController.getView().getScene());
    }

    private class GameLoop extends AnimationTimer {
        public void handle(long now) {
            double delta = currentTime - now;
            currentTime = now;


        }
    }
}
