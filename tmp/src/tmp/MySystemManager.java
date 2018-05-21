package tmp;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.net.*;
import java.net.InterfaceAddress;

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
	
	private static int n_device;

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
		n_device = 0;


		// -----------------------------------------------------------------------
		/*
		 * inizializzazione dei dispositivi ESP
		 * si potrebbe cercare di contattare tutti i dispositivi nella rete con un ciclo for  
		 * provando a mandare per ogni ciclo di iterazione un pacchetto particolare da 
		 * noi creato. Solo gli ESP risponderanno al pacchetto cosi possiamo mapparli 
		 */
		// -----------------------------------------------------------------------
		scanAddresses();	
		System.out.println("Found "+n_device+" device(s)");

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

	private static void scanAddresses(){
		InetAddress broadcast=null;

		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

			boolean found=false;
			while (en.hasMoreElements() && !found) {
				NetworkInterface ni = en.nextElement();
				if (ni.isLoopback())
					continue;

				for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) 
				{
					broadcast = interfaceAddress.getBroadcast();
					if (broadcast != null)
					{
						found=true;
						break;
					}
				}
			} 
			System.out.println("Broadcast address: "+ broadcast.getHostAddress());

			int port = 1500;

			byte[] message = "ESP test connected".getBytes();

			// Initialize a datagram packet with data and address
			DatagramPacket packet = new DatagramPacket(message, message.length,	broadcast, port);

			// Create a datagram socket, send the packet through it, close it.
			DatagramSocket dsocket = new DatagramSocket(port);
			dsocket.send(packet);
			dsocket.close();
			
			DatagramPacket p = new DatagramPacket(message, message.length);
			DatagramSocket d = new DatagramSocket(port);	
			
			while (true)
			{		
				/*
				 * setto il timeout della receive a 5 secondi che altrimenti sarebbe bloccante 
				 */
				d.setSoTimeout(5000);
				d.receive(p);
				System.out.println("Device IP: " + p.getAddress().getHostAddress());
				n_device++;
			}

		} catch (SocketTimeoutException e) {
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public List<ESP_32> getDevice() {
		return device;
	}

	public void setDevice(List<ESP_32> device) {
		this.device = device;
	}

	public List<ProbeRequest> getPacketsToSend() {
		return packetsToSend;
	}

	public void setPacketsToSend(List<ProbeRequest> packetsToSend) {
		this.packetsToSend = packetsToSend;
	}

	public List<ProbeRequest> getPacketsToReceive() {
		return packetsToReceive;
	}

	public void setPacketsToReceive(List<ProbeRequest> packetsToReceive) {
		this.packetsToReceive = packetsToReceive;
	}

	public static int getN_device() {
		return n_device;
	}

	public static void setN_device(int n_device) {
		MySystemManager.n_device = n_device;
	}
}
