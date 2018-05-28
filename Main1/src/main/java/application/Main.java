package application;
import com.jfoenix.controls.JFXSpinner;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


    
public class Main extends Application {

	 @Override
       public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyLayout.fxml"));
            root.getStylesheets().add(getClass().getResource("Home_page.css").toExternalForm());
			root.getStylesheets().add("https://fonts.googleapis.com/css?family=Rajdhani");
            primaryStage.setTitle("My Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(300);
            primaryStage.setHeight(500);;
            primaryStage.setWidth(400);
            
            primaryStage.show();
            
         
         
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}