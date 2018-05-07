package tmp;

import java.util.Date;

public class ProbeRequest {
	
	private String mac_addr;
	private int SSID;
	private Date date;
	private int hash;
	private int signal;
	private int ESP_32_id;
	
	public ProbeRequest(String mac_addr, int sSID, Date date, int hash, int signal, int ESP_32_id) {
		super();
		this.mac_addr = mac_addr;
		SSID = sSID;
		this.date = date;
		this.hash = hash;
		this.signal = signal;
		this.ESP_32_id=ESP_32_id;
	}

	public int getESP_32_id() {
		return ESP_32_id;
	}

	public void setESP_32_id(int eSP_32_id) {
		ESP_32_id = eSP_32_id;
	}

	public String getMac_addr() {
		return mac_addr;
	}
	
	public void setMac_addr(String mac_addr) {
		this.mac_addr = mac_addr;
	}
	
	public int getSSID() {
		return SSID;
	}
	
	public void setSSID(int sSID) {
		SSID = sSID;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getHash() {
		return hash;
	}
	
	public void setHash(int hash) {
		this.hash = hash;
	}
	
	public int getSignal() {
		return signal;
	}
	
	public void setSignal(int signal) {
		this.signal = signal;
	}

	@Override
	public String toString() {
		return "ProbeRequest [mac_addr=" + mac_addr + ", SSID=" + SSID + ", date=" + date + ", hash=" + hash
				+ ", signal=" + signal + ", ESP_32_id=" + ESP_32_id + "]";
	}
}
