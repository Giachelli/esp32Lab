package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class prova2 implements Initializable {
	public void provaBottone(ActionEvent event) {
		Random rand = new Random();
		int myrand = rand.nextInt(50)+1;
		System.out.println(Integer.toString(myrand));
	}

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
	//	@FXML
	//	private BubbleChart grafico;
	@FXML
	private BubbleChart<Number,Number> grafico = new
	BubbleChart<Number,Number>(xAxis,yAxis);
	@FXML
	private Label lab1;
	@FXML
	private GridPane gp_homePage;
	@FXML
	private Button nuovo;
	@FXML
	private Button start;
	@FXML
	private Button scan;
	@FXML
    private Label titolo;
	@FXML
	private ImageView sfondo;
	//@FXML
	//private AreaChart grafico1;
	private AreaChart<Number,Number> grafico1 = new AreaChart<Number,Number>(xAxis,yAxis);

	int num_ESP=0;;
	protected MySystemManager mysystem;
	private List<TextField> list = new ArrayList<>();
	prova classe_prova = new prova();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		grafico.setTitle("DEVICES ARRANGEMENT"); //title nell'onload 2° page
		grafico1.setTitle("Daily Traffic");
		grafico.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 18;");
		grafico1.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 18;");

		grafico1.setId("chart1");
		titolo.setStyle("-fx-font-family: Rajdhani; -fx-font-size: 32; -fx-font-color: white;");

		init();
		
		

	}      

	@FXML
	private void init() {
		try { 
			String s = Integer.toString (5);//me lo passa gala
			//String s = Integer.toString (mysystem.getN_device());
			num_of_ESP.setText(s); // passare la stringa con valore numero schede
			num_of_dispositivi.setText("var_num_disp"); //passare la stringa con valore numero dispositivi
			System.out.println("init");
			System.out.println(num_ESP);
			init_grafico();


		}catch(Exception e) {
			e.printStackTrace();
		}	

	}

	private void init_grafico() {
		int n=0;
		System.out.println("set grafico");
		xAxis.setLabel("Week");
		yAxis.setLabel("Product Budget");
		//grafico.setTitle("Budget Monitoring");
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("ESP");
		//recive_data_db();
		for(int i=0;i<mysystem.getN_device()*2;) {
		//for(int i=0;i<3*2;) {
			System.out.println(classe_prova.arr[i]);
		System.out.println("ciao sono in for");
		series1.getData().add(new XYChart.Data(classe_prova.arr[i],classe_prova.arr[i+1]));
		i=i+2;
		}
		XYChart.Series series2 = new XYChart.Series();

		series2.setName("Product 2");
		series2.getData().add(new XYChart.Data(8, 15));

		grafico.getData().addAll(series1, series2); 
		
//		XYChart.Series seriesApril= new XYChart.Series();
//		System.out.println("Series April");
//
//        seriesApril.setName("April");
//        seriesApril.getData().add(new XYChart.Data(1, 4));
//        seriesApril.getData().add(new XYChart.Data(3, 10));
//        seriesApril.getData().add(new XYChart.Data(6, 15));
//        seriesApril.getData().add(new XYChart.Data(9, 8));
//        seriesApril.getData().add(new XYChart.Data(12, 5));
//        seriesApril.getData().add(new XYChart.Data(15, 18));
//        seriesApril.getData().add(new XYChart.Data(18, 15));
//        seriesApril.getData().add(new XYChart.Data(21, 13));
//        seriesApril.getData().add(new XYChart.Data(24, 19));
//        seriesApril.getData().add(new XYChart.Data(27, 21));
//        seriesApril.getData().add(new XYChart.Data(30, 21));
//        
//        XYChart.Series seriesMay = new XYChart.Series();
//		System.out.println("Series May");
//
//        seriesMay.setName("May");
//        seriesMay.getData().add(new XYChart.Data(1, 20));
//        seriesMay.getData().add(new XYChart.Data(3, 15));
//        seriesMay.getData().add(new XYChart.Data(6, 13));
//        seriesMay.getData().add(new XYChart.Data(9, 12));
//        seriesMay.getData().add(new XYChart.Data(12, 14));
//        seriesMay.getData().add(new XYChart.Data(15, 18));
//        seriesMay.getData().add(new XYChart.Data(18, 25));
//        seriesMay.getData().add(new XYChart.Data(21, 25));
//        seriesMay.getData().add(new XYChart.Data(24, 23));
//        seriesMay.getData().add(new XYChart.Data(27, 26));
//        seriesMay.getData().add(new XYChart.Data(31, 26));
//		System.out.println("Series Finished");
//		grafico1.getData().addAll(seriesApril,seriesMay);
//        System.out.println("Add ALL");
	} 
}

