package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
public class controller_button implements Initializable {
	@FXML
	private GridPane gp;
	@FXML
	private Label lab1;
	@FXML
	private Button button;
	@FXML
	private ImageView sfondo;

	int num_ESP=0;;
	protected MySystemManager mysystem;
	
	
	//prova2 classe_prova = new prova2();
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Tooltip tt = new Tooltip();
		tt.setText("Text on Hover");
		tt.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; "
		    + "-fx-base: #AE3522; "
		    + "-fx-text-fill: orange;");
		button.setTooltip(tt);

	}      
	@FXML
	private void trova_esp(ActionEvent trova_espActionEvent){
		// lampeggiare led esp

	}
 
}


