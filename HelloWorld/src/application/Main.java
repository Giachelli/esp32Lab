package application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
    
public class Main extends Application {
    @Override
       public void start(Stage primaryStage) {
        try {
            // Read file fxml and draw interface.
            Parent root = FXMLLoader.load(getClass().getResource("MyLayout.fxml"));
 
            primaryStage.setTitle("My Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(300);
            primaryStage.show();
         
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}