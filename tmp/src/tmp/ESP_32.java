package tmp;

public class ESP_32 {
	
	private int x;
	private int y;
	private String ip_addr;
	private boolean sniffing;
	
	public ESP_32(int x, int y, String ip_addr) {
		super();
		this.x = x;
		this.y = y;
		this.ip_addr = ip_addr;
		this.sniffing=false;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public String getIp_addr() {
		return ip_addr;
	}
	
	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}
	
	public boolean isSniffing() {
		return sniffing;
	}
	
	public void setSniffing(boolean sniffing) {
		this.sniffing = sniffing;
	}

	@Override
	public String toString() {
		return "ESP_32 [x=" + x + ", y=" + y + ", ip_addr=" + ip_addr + ", sniffing=" + sniffing + "]";
	}
}
