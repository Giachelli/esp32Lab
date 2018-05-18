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


public class prova implements Initializable {
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
	private ImageView sfondo;

	int num_ESP=0;;
	protected MySystemManager mysystem;
	private List<TextField> list = new ArrayList<>();
	private int[] arr=new int[30];

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}      

	@FXML
	private void scan_ESP(ActionEvent event){
			//mysystem = new MySystemManager();

		start.setDisable(false);;
		//nuovo.setDisable(false);
		scan.setDisable(true);
		sfondo.setVisible(false);
		//for(int i=0;i<mysystem.getN_device();i++)
		for(int i=0;i<3;i++){
			addESP();
		}

	}
	private void addESP(){
			//if(num_ESP<mysystem.getN_device()){ //4 o numero di esp rilevati
		if(num_ESP<4) {
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

	private GridPane getBlocco(int num_ESP)
	{
		GridPane result = new GridPane();
		GridPane gp = new GridPane();

		Label esp = new Label();
		esp.setText("ESP_"+num_ESP);
		Label xl = new Label("X"), yl = new Label("Y");
		TextField xf = new TextField();
		xf.setPromptText("Ins X");
		TextField yf = new TextField();
		yf.setPromptText("Ins Y");

		list.add(xf);
		list.add(yf);

		result.add(esp, 0, 0);
		result.add(gp, 1, 0);
		gp.add(xl, 0, 0);
		gp.add(yl, 0, 1);
		gp.add(xf, 1, 0);
		gp.add(yf, 1, 1);

		ColumnConstraints c1 = new ColumnConstraints();
		c1.setMinWidth(10);
		c1.setPrefWidth(22);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setMinWidth(10);
		c2.setPrefWidth(65);
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
		c12.setPrefWidth(100);
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
		//	if(num_ESP<mysystem.getN_device()){ //4 o numero di esp rilevati
		if(num_ESP<4) {
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
		for(TextField tf:list)
		{
			System.out.println(tf.getText());
			int i= Integer.parseInt(tf.getText());
			System.out.println(i);
			arr[a]= i;
					a++;
		}System.out.println(Arrays.toString(arr));
		
		//send_data_db(list);
	
	}
	@FXML
	private void start(ActionEvent new_event) {
		try {    
			if(1==1) { // verificare che i TextField siano pieni
				input_field();}
			else { //mostra i rossi intorno ai textfield vuoti e non andare avanti
			}
			
			Parent home_page_parent = FXMLLoader.load(getClass().getResource("Home_page.fxml"));
			Object eventSource = new_event.getSource();
			Node source_as_node = (Node) eventSource;
			Scene oldScene = source_as_node.getScene();
			Window window = oldScene.getWindow();
			Stage stage = (Stage) window;
			Scene scene = new Scene(home_page_parent); 
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
			String s = Integer.toString (num_ESP);//me lo passa gala
			//String s = Integer.toString (mysystem.getN_device());
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
		int n=0;
		System.out.println("set grafico");
		xAxis.setLabel("Week");
		yAxis.setLabel("Product Budget");
		grafico.setTitle("Budget Monitoring");
		input_field();
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("ESP");
		//recive_data_db();
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

