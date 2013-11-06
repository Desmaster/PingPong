package nl.tdegroot.software.pingpong.server;

import java.net.InetAddress;

public class ServerClient {

	public String name;
	public InetAddress address;
	public int port;
	private int ID;

	public ServerClient(String name, InetAddress address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}

}
