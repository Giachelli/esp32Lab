package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.Button;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class prova implements Initializable {
	public void provaBottone(ActionEvent event) {
		Random rand = new Random();
		int myrand = rand.nextInt(50)+1;
		System.out.println(Integer.toString(myrand));
	}

	@FXML
	private GridPane gp;
	
	int x=0;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}      

	@FXML
	private void addNewESP(ActionEvent event){
		if(x<8) { //4 o numero di esp rilevati
			try {    
				GridPane temp = (GridPane) FXMLLoader.load(getClass().getResource("Block_new.fxml"));
				GridPane btn_esp = (GridPane) FXMLLoader.load(getClass().getResource("Button_esp.fxml"));
				gp.addRow(1+x);
				gp.add(temp, 1, x);//x is column index and 0 is row index
				gp.add(btn_esp, 3, x);
				x++;			
			}catch(Exception e) {
				e.printStackTrace();
			}	
		}
	}

	@FXML
	private void start(ActionEvent new_event) {
		try {    
			Parent home_page_parent = FXMLLoader.load(getClass().getResource("Home_page.fxml"));
			Object eventSource = new_event.getSource();
			Node source_as_node = (Node) eventSource;
			Scene oldScene = source_as_node.getScene();
			Window window = oldScene.getWindow();
			Stage stage = (Stage) window;
			Scene scene = new Scene(home_page_parent);
			stage.setScene(scene);
			stage.setMaximized(true);
			//stage.setFullScreen(true);
			stage.show();
			
		}catch(Exception e) {
			e.printStackTrace();
		}	

	}

}
