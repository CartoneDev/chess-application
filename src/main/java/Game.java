import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javax.swing.text.Position;


public class Game extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("CheckMate");
        Pane root = new Pane();
        Scene sin = new Scene(root, 600, 600);
        primaryStage.setScene(sin);
//        root.setStyle("-fx-background-image: url('https://cdn.app.compendium.com/uploads/user/e7c690e8-6ff9-102a-ac6d-e4aebca50425/ed5569e8-c0dd-458c-8450-cde6300093bd/File/a5023b0f0fb67f59176a0499af9021ed/java_horz_clr.png'); -fx-background-repeat: no-repeat; -fx-background-size: 500 500; -fx-background-position: center center;");

        Canvas canvas = new Canvas(600, 400);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image s = new Image("jopa.jpg");
        gc.drawImage(s, 0, 0);

        primaryStage.show();

    }
    public static void main(String[] args){
        launch(args);
    }
}
