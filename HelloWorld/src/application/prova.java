package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
	private VBox scroll;
	private Node btn_esp;
	int x=1;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}      

	@FXML
	private void addNewESP(ActionEvent event){
		if(x<8) { //4 o numero di esp rilevati
			try {    
				GridPane temp = (GridPane) FXMLLoader.load(getClass().getResource("Block_new.fxml"));
				gp.addRow(1+x);
				gp.add(temp, 1, x);//x is column index and 0 is row index
				gp.add(btn_esp,2,x);
				scroll.getChildren().add(x, temp);
				x++;
			}catch(Exception e) {
				e.printStackTrace();
			}	
		}
	}

}
