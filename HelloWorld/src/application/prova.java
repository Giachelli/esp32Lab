package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
	@FXML
	private TextField num_of_ESP;
	@FXML
	private TextField num_of_dispositivi;
	@FXML
	private BubbleChart grafico;
	@FXML
	private Label lab1;
	@FXML
	private GridPane gp_homePage;
	
	int num_ESP=0;;
	protected MySystemManager mysystem;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}      

	@FXML
	private void scan_ESP(ActionEvent event){
		mysystem = new MySystemManager();
	}
	@FXML
	private void addNewESP(ActionEvent event){
		if(num_ESP<mysystem.getN_device()) { //4 o numero di esp rilevati
			try {    
				GridPane temp = (GridPane) FXMLLoader.load(getClass().getResource("Block_new.fxml"));
				GridPane btn_esp = (GridPane) FXMLLoader.load(getClass().getResource("Button_esp.fxml"));
				Label label = new Label();
				label.setText("ESP_"+num_ESP);
				temp.add(label, 0, 0);
				gp.addRow(1+num_ESP);
				gp.add(temp, 1, num_ESP);//x is column index and 0 is row index
				gp.add(btn_esp, 3, num_ESP);
				num_ESP++;
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
		System.out.print("start: ");
		System.out.print(num_ESP);
		
		

	}
	
	@FXML
	private void init(ActionEvent init_event) {
		try { 
			String s = Integer.toString (num_ESP);//me lo passa gala
			num_of_ESP.setText(s); // passare la stringa con valore numero schede
			num_of_dispositivi.setText("var_num_disp"); //passare la stringa con valore numero dispositivi
			System.out.print("nit");
			System.out.print(num_ESP);
			init_grafico();
			
		}catch(Exception e) {
			e.printStackTrace();
		}	

	}
	
	private void init_grafico() {
		System.out.print("set grafico");
	}

}
