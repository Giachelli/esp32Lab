package application;
import javafx.css.converter.PaintConverter;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

public class prova implements Initializable {

	@FXML
	final NumberAxis xAxis = new NumberAxis(1, 53, 4);
	@FXML
	final NumberAxis yAxis = new NumberAxis(0, 80, 10);
	@FXML
	private GridPane gp;
	@FXML
	private TextField num_of_ESP;
	@FXML
	private TextField num_of_dispositivi;
	@FXML
	public TextField x_field;
	@FXML
	public TextField y_field;
	@FXML
	private BubbleChart<Number,Number> grafico = new
	BubbleChart<Number,Number>(xAxis,yAxis);
	@FXML
	private Label lab1;
	@FXML
	private GridPane gp_homePage;
	@FXML
	private Button start;
	@FXML
	private Button scan;
	@FXML
	private ImageView sfondo;
	@FXML
	private ScrollPane scroll;
	@FXML
	private JFXSpinner caricamento;
	@FXML
	private Label titolo;

	int num_ESP=0;;
	protected MySystemManager mysystem;
	private List<TextField> list = new ArrayList<>();
	public static int[] arr=new int[30];

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		titolo.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 32;");
		Tooltip tt_scan = new Tooltip();
		tt_scan.setText("Text on Hover");
		tt_scan.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; "
				+ "-fx-base: #AE3522; "
				+ "-fx-text-fill: orange;");
		scan.setTooltip(tt_scan);
		Tooltip tt_start = new Tooltip();
		tt_start.setText("Text on Hover");
		tt_start.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; "
				+ "-fx-base: #AE3522; "
				+ "-fx-text-fill: orange;");
		start.setTooltip(tt_start);
	}      
	@FXML
	private void scan_ESP(ActionEvent event){
		caricamento.setVisible(true);
		mysystem = new MySystemManager();  //->togliere commento(messo solo per fare prove locale)
		start.setDisable(false);
		scan.setDisable(true);
		sfondo.setVisible(false);
		scroll.setOpacity(1.0);

		for(int i=0;i<mysystem.getN_device();i++) { //->togliere commento(messo solo per fare prove locale)
			//for(int i=0;i<3;i++){ //ri-commentare questa riga (messo solo per fare prove locale)
			addESP();
		}

		caricamento.setVisible(false);

	}
	private void addESP(){
		if(num_ESP<mysystem.getN_device()){ //4 o numero di esp rilevati  // ->togliere commento(messo solo per fare prove locale)
			//if(num_ESP<4) { //ri-commentare questa riga (messo solo per fare prove locale)
			try {    
				GridPane temp = getBlocco(num_ESP);
				GridPane btn_esp = (GridPane) FXMLLoader.load(getClass().getResource("Button_esp.fxml"));
				gp.addRow(1+num_ESP);
				gp.add(temp, 1, num_ESP);
				gp.add(btn_esp, 3, num_ESP);
				num_ESP++;  
			}catch(Exception e) {
				e.printStackTrace();
			}   
		}
	}
	private GridPane getBlocco(int num_ESP)
	{
		GridPane result = new GridPane();
		GridPane gp = new GridPane();
		GridPane gp1 = new GridPane();

		ImageView icona_esp = new ImageView(getClass().getResource("iconaMaln.png").toExternalForm());

		icona_esp.setFitHeight(60);
		icona_esp.setFitWidth(60);

		JFXTextField xf = new JFXTextField();
		xf.setPromptText("X");
		Paint color1 = Color.web("#00c853");
		Paint color = Color.web("#ff3c00");
		xf.setUnFocusColor(color );
		xf.setFocusColor(color1);
		xf.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; -fx-prompt-text-fill:derive(-fx-control-inner-background,-30%)");
		Tooltip tt_xf = new Tooltip();
		tt_xf.setText("Text on Hover");
		tt_xf.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; "
				+ "-fx-base: #AE3522; "
				+ "-fx-text-fill: orange;");
		xf.setTooltip(tt_xf);
		//xf.setText("3"); // ->cancellare riga(messo solo per fare prove locale)
		JFXTextField yf = new JFXTextField();
		yf.setPromptText("Y");
		yf.setUnFocusColor(color );
		yf.setFocusColor(color1);
		yf.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14;-fx-prompt-text-fill:derive(-fx-control-inner-background,-30%)" );
		Tooltip tt_yf = new Tooltip();
		tt_yf.setText("Text on Hover");
		tt_yf.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 14; "
				+ "-fx-base: #AE3522; "
				+ "-fx-text-fill: orange;");
		yf.setTooltip(tt_yf);
		//yf.setText("3"); // ->cancellare riga(messo solo per fare prove locale)
		list.add(xf);
		list.add(yf);
		result.add(gp1, 0, 0);
		result.add(gp, 1, 0);
		gp.add(xf, 1, 0);
		gp.add(yf, 1, 1);
		gp1.add(icona_esp, 0, 0);
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setMinWidth(15);
		c1.setPrefWidth(15);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setMinWidth(10);
		c2.setPrefWidth(90);
		RowConstraints r1 = new RowConstraints();
		r1.setMinHeight(10);
		r1.setPrefHeight(30);
		RowConstraints r2 = new RowConstraints();
		r2.setMinHeight(10);
		r2.setPrefHeight(30);
		gp.getColumnConstraints().add(c1);
		gp.getColumnConstraints().add(c2);
		gp.getRowConstraints().add(r1);
		gp.getRowConstraints().add(r2);
		ColumnConstraints c11 = new ColumnConstraints();
		c11.setMinWidth(10);
		c11.setPrefWidth(100);
		ColumnConstraints c12 = new ColumnConstraints();
		c12.setMinWidth(10);
		c12.setPrefWidth(140);
		RowConstraints r11 = new RowConstraints();
		r11.setMinHeight(65);
		r11.setPrefHeight(65);
		r11.setMaxHeight(65);
		result.getColumnConstraints().add(c11);
		result.getColumnConstraints().add(c12);
		result.getRowConstraints().add(r11);

		return result;
	}
	@FXML
	private void addNewESP(ActionEvent event){
		if(num_ESP<mysystem.getN_device()){ //4 o numero di esp rilevati ->togliere commento(messo solo per fare prove locale)
			//if(num_ESP<4) {
			try {    
				GridPane temp = getBlocco(num_ESP);
				GridPane btn_esp = (GridPane) FXMLLoader.load(getClass().getResource("Button_esp.fxml"));
				gp.addRow(1+num_ESP);
				gp.add(temp, 1, num_ESP);//x is column index and 0 is row index
				gp.add(btn_esp, 3, num_ESP);
				num_ESP++;  
			}catch(Exception e) {
				e.printStackTrace();
			}   
		}
	}
	private void input_field() {
		int a=0;
		int index=0;
		for(TextField tf:list)
		{
			System.out.println(tf.getText()); //->togliere commento(messo solo per fare prove locale)
			int i= Integer.parseInt(tf.getText()); //->togliere commento(messo solo per fare prove locale)
			System.out.println(i); //->togliere commento(messo solo per fare prove locale)
			arr[a]= i;  //per quando in locale arr[a]=a+i
			if((a%2)!=0) {
				mysystem.getDevice().get(index).setX(arr[a-1]); //togliere commento(messo solo per fare prove locale)
				mysystem.getDevice().get(index).setY(arr[a]); //togliere commento(messo solo per fare prove locale)
				index++; //togliere commento(messo solo per fare prove locale)
			}
			a++;
		}
		System.out.println(Arrays.toString(arr));
	}
	private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(owner);
		alert.show();
	}
	@FXML
	private void start(ActionEvent new_event) {
		try {  
			if(list.size()<num_ESP*2) { //da verificare con scheda ma dovrebbe dunzionare
				System.out.println("bisogna inserire tutte le coordinate");
				showAlert(Alert.AlertType.ERROR, gp.getScene().getWindow(), 
						"Form Error!", "Please enter all field");
				return;
			}
			else //if(1==1) { // verificare che i TextField siano pieni
				input_field();//}
			//else { //mostra i rossi intorno ai textfield vuoti e non andare avanti
			//}
			Parent home_page_parent = FXMLLoader.load(getClass().getResource("Home_page.fxml"));
			Object eventSource = new_event.getSource();
			Node source_as_node = (Node) eventSource;
			Scene oldScene = source_as_node.getScene();
			Window window = oldScene.getWindow();
			Stage stage = (Stage) window;
			Scene scene = new Scene(home_page_parent); 
			scene.getStylesheets().add(getClass().getResource("Home_page.css").toExternalForm());
			scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Rajdhani");
			      mysystem.start(); //->togliere commento(messo solo per fare prove locale)
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}   
	}
	@FXML
	private void init(ActionEvent init_event) {
		try { 
			//String s = Integer.toString (num_ESP);//me lo passa gala
		String s = Integer.toString (mysystem.getN_device());
			num_of_ESP.setText(s); // passare la stringa con valore numero schede
			num_of_dispositivi.setText("var_num_disp"); //passare la stringa con valore numero dispositivi
			System.out.println("init");
			System.out.println(num_ESP);
		}catch(Exception e) {
			e.printStackTrace();
		}   
	}
	private void init_grafico() {
		int n=0;
		System.out.println("set grafico");
		xAxis.setLabel("Week");
		yAxis.setLabel("Product Budget");
		grafico.setTitle("Budget Monitoring");
		input_field();
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("ESP");
		//recive_data_db(); ?????????????????
		for(int i=0;i<3*2;) {
			System.out.println(arr[i]);
			System.out.println("ciao sono in for");
			series1.getData().add(new XYChart.Data(arr[i],arr[i+1]));
			i=i+2;
		}
		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Product 2");
		series2.getData().add(new XYChart.Data(8, 15));
		grafico.getData().addAll(series1, series2); 
	} 
}
