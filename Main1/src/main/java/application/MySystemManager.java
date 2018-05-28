package application;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.mysql.jdbc.PreparedStatement;


import java.io.IOException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.util.stream.Collectors.*;
import static java.util.Comparator.*;


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
	private ServerSocket welcomeSocket; //TCP socket per mandare e ricevere dati all'ESP 
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

		try {
			welcomeSocket = new ServerSocket(1500);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

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
			
			
			connection= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/progetto_esp", "root", "root");
		} 
		catch (SQLException e) {
			System.out.println("errore nell'apertura della connessione");
			e.printStackTrace();
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
				String ssid = rs.getString("SSID");
				int date =rs.getInt("DATE");
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

	private void scanAddresses(){
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
				ESP_32 tmp = new ESP_32(p.getAddress().getHostAddress(), n_device);
				device.add(tmp);
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

	/*
	 * il metod viene invocato nel momento in cui viene premuto il tasto stat sull'interfaccia utente
	 * e si occupa di sganciare un thread per la gestione dei dispositivi ESP (es. pacchetti di sincronizzazione)
	 */
	public void start()
	{
		/*
		 * il thread crea 
		 * - un ciclo infinito durante il quale manda un pacchetto di start agli ESP
		 * - setta un timer per un minuto
		 * - una volta scattato il timer si occupa di gestire i pacchetti che arrivano dagli ESP
		 * - manda i dati verso il database 
		 * (ricomincia il ciclo)
		 */
		class MyThread extends Thread{  
			public void run(){  
				System.out.println("thread is running...synchronized");  
				synchronized(welcomeSocket) {
					System.out.println("thread is running...");  

					int port = 1500;
					byte[] message = "START".getBytes();

					while(true)
					{
						int i; 
						for (i=0;i<device.size();i++)
						{ 
							try {
								Socket connectionSocket = new Socket(InetAddress.getByName(device.get(i).getIp_addr()), 2000); 
								DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
								outToServer.write(message);
								connectionSocket.close();  
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(-1);
							}
						}
						
						System.out.println("thread is running...prima"); 
						try {
							getESPdata();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("thread is running...dopo");  
					}	 
				}
			}
		}

		/*
		 * la funzione thread precedentemente creata viene istanziata e lanciata 
		 */
		MyThread ESPManagementThread = new MyThread();
		ESPManagementThread.start();
		
	}

	public void getESPdata() throws SQLException
	{

		int count = 0;
		int i;

		while (count<n_device) {
			System.out.println("in while inizio");
			Socket connectionSocket = null;
			InputStream in = null;
			int num_pack = 0;

			try {
				System.out.println("in while inizio try 1");
				connectionSocket = welcomeSocket.accept();
				System.out.println("in while inizio try 2");
				in = connectionSocket.getInputStream();
				System.out.println("in while inizio try 3");
				byte[] intData = new byte[4];

				in.read(intData);
				System.out.println(ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt());
				num_pack = ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			try {
				for (i = 0;i < num_pack;i++)
				{		
					byte[] intData = new byte[4]; //buffer temporaneo per salvare tutti gli interi
					byte[] macAddr = new byte[6]; //salva la dimensione dell'indirizzo mac
					
					int pack_size, ssid_size, rssi, time, hash;
					String macAddrString, ssidString = "";
					
					in.read(intData);
					pack_size = ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt();
					
					ssid_size=pack_size-18;
					
					byte[] ssid = new byte[ssid_size]; //salvo eventuale valore di ssid in buffer sovrallocato
					
					in.read(macAddr); //non necessita di conversione
					macAddrString = String.format("%02x:%02x:%02x:%02x:%02x:%02x", macAddr[0], macAddr[1], macAddr[2], macAddr[3], macAddr[4], macAddr[5]);
					
					int ret=in.read(ssid, 0, ssid_size);
					if (ret>0)
					{
						StringBuffer tmp=new StringBuffer();
						for (int j=0;j<ret;j++)
							tmp.append(String.format("%c", ssid[j]));
						ssidString=tmp.toString();
					}								
					
					in.read(intData);
					rssi = ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt();
					
					in.read(intData);
					time = ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt();
					
					in.read(intData);
					hash = ByteBuffer.wrap(intData).order(ByteOrder.LITTLE_ENDIAN).getInt();

					// da modificare
					ProbeRequest tmProbeRequest = new ProbeRequest(macAddrString, ssidString, time, hash, rssi, 0);
					System.out.println(tmProbeRequest);
					packetsToSend.add(tmProbeRequest);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			/*String[] pacchetti_divisi = pacchetti.toString().split("\\n\\r");
			System.out.println(pacchetti.toString());
			for (i=0;i<pacchetti_divisi.length;i++)
			{
				//System.out.println(pacchetti_divisi[i]);
				
				String[] campi = pacchetti_divisi[i].split("\\r\\n");

				
				 * bisogna ancora implementare il numero dell'esp da cui
				 * riceve i pacchetti, per ora inserisco un numero casuale
				 
				System.out.println(campi[0]);
				ProbeRequest pr = new ProbeRequest(campi[0], 
						Integer.parseInt(campi[1]),
						Integer.parseInt(campi[2]),
						Integer.parseInt(campi[3]),
						Integer.parseInt(campi[4]),
						0);
				packetsToSend.add(pr);
			}*/
			count++;		
		}
		for (i=0;i<packetsToSend.size();i++)
		{
			String temp_macadd = packetsToSend.get(i).getMac_addr();
			String temp_ssid = packetsToSend.get(i).getSSID();
			Integer temp_date = packetsToSend.get(i).getDate();
			Integer temp_hash = packetsToSend.get(i).getHash();
			Integer temp_rssi = packetsToSend.get(i).getSignal();
			Integer temp_espid = packetsToSend.get(i).getESP_32_id();
		String query = " insert into dati_applicazione (mac_address, ssid, date, hash, rssi, esp_id)"
                + " values ('"+temp_macadd+"','"+temp_ssid+"',"+temp_date+","+temp_hash+","+temp_rssi+","+temp_espid+")";
   
              java.sql.PreparedStatement st = connection.prepareStatement(query);
		connection.createStatement().executeUpdate(query);
		}
		
		/*
		 * NB: l'ultima entry della query dovrebbe essere terminata sa ; invece della , bisogna testare se
		 *     funziona lo stesso
		 * 	   
		 */
		/*
		StringBuffer query = new StringBuffer("INSERT INTO dati_applicazione VALUES ");
		PreparedStatement st = connection.prepareStatement("INSERT INTO `dati_applicazione`(`MAC_ADDRESS`, `SSID`, `DATE`, `HASH`, `SIGNAL`, `ESP_ID`) VALUES (?,?,?,?,?,?");
		for (i=0;i<packetsToSend.size();i++)
		{			
			query.append("('" + packetsToSend.get(i).getMac_addr() + "','" +
					packetsToSend.get(i).getSSID() +"',"+
					packetsToSend.get(i).getDate() +","+
					packetsToSend.get(i).getHash() +","+
					packetsToSend.get(i).getSignal() +","+
					packetsToSend.get(i).getESP_32_id() + ")");
			if (i!=packetsToSend.size()-1)
				query.append(",");
			else 
				query.append(";");
		}*/
/*
		try {
			System.out.println(query.toString());
			connection.createStatement().executeQuery(query.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}*/
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
