package tmp;

import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * la funzione di questa classe e' quella di gestire contemporaneamente sia la 
 * lista dei dispositivi connessi, sia la lista dei pacchetti che riceve da questi.
 * Si occupa quindi d ricevere i dati, inviarli al database e successivamente ricavare da quest'ultimo
 * i dati che necessita per la visualizzazione grafica
 * 
 * Funzioni realizzate:
 * - apertura connessione verso database
 * - semplice query al database (da completare con i parametri ricevuti da interfaccia grafica) e inserimento 
 *   dei dati in una struttura a lista 
 * 
 * Funzioni da realizzare:
 * - ricevere ed elaborare i pacchetti ricevuti dall'esp
 */


public class MySystemManager {

	private List<ESP_32> device;
	private List<ProbeRequest> packetsToSend;
	private List<ProbeRequest> packetsToReceive;
	private Connection connection;
	
	/*
	 * crea le due liste, carica i driver e apre la connessione con il database
	 */
	public MySystemManager() {
		/*
		 * inizializzazione delle variabili della classe
		 */
		device = new LinkedList<ESP_32>();
		packetsToSend = new LinkedList<ProbeRequest>();
		packetsToReceive = new LinkedList<ProbeRequest>();
		
		
		// -----------------------------------------------------------------------
		/*
		 * inizializzazione dei dispositivi ESP
		 * si potrebbe cercare di contattare tutti i dispositivi nella rete con un ciclo for  
		 * provando a mandare per ogni ciclo di iterazione un pacchetto particolare da 
		 * noi creato. Solo gli ESP risponderanno al pacchetto cosi possiamo mapparli 
		 */
		// -----------------------------------------------------------------------
				
		
		/*
		 * inizializzazione dei driver di sistema 
		 */
		try {
			   Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException ex) {
			   System.out.println("Error: unable to load driver class!");
			   System.exit(-1);
		}
		
		/*
		 * connessione verso il database su localhost, da modificare nel caso sia
		 * necessario contattare un database su un altro dispositivo
		 */
		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/progetto_esp", "root", "root");
		} 
		catch (SQLException e) {
			System.out.println("errore nell'apertura della connessione");
		}
	}
	
	public void getProbeRequests()
	{
		/*
		 * query di prova, successivamente dovremo poi modificarla dinamicamente 
		 * in base alle richiesete ricevute da finestra grafica
		 */
		String query = "SELECT * FROM dati_applicazione";
		
		try {
			
			ResultSet rs = connection.createStatement().executeQuery(query);
			
			while(rs.next())
			{
				/*
				 * estrazione dei vari campi dai risultati della query
				 */
				String mac = rs.getString("MAC_ADDRESS");
				int ssid = rs.getInt("SSID");
				Date date =rs.getDate("DATE");
				int hash = rs.getInt("HASH");
				int signal = rs.getInt("SIGNAL");
				int esp_id = rs.getInt("ESP_ID");
				
				ProbeRequest tmp = new ProbeRequest(mac, ssid, date, hash, signal, esp_id);		
				//System.out.println(tmp);
				packetsToReceive.add(tmp);
			}
		}
		catch (SQLException e) {
			System.out.println(e);
			System.exit(-1);
		}
	}
}
