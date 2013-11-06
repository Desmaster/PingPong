package nl.tdegroot.software.pingpong.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server implements Runnable {

	private HashMap<Integer, ServerClient> clients = new HashMap<Integer, ServerClient>();

	private Thread run, tick, send, receive;
	private DatagramSocket socket;

	private int port;
	private boolean running = false;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this, "Server");
		run.start();
	}


	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		tick();
		receive();
	}

	public void tick() {
		tick = new Thread("Tick") {
			public void run() {

			}
		};
		tick.start();
	}

	public void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
					read(packet);
				}
			}
		};
		receive.start();
	}

	public void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	public void read(DatagramPacket packet) {
		String message = new String(packet.getData());
		if (message.startsWith("/c/")) {
			String name = message.substring(3, message.length());
			addClient(new ServerClient(name, packet.getAddress(), packet.getPort()));
			System.out.println("Added a new client with name: " + name);
		} else {
			System.out.println(message);
		}
	}

	private void addClient(ServerClient client) {
		int ID = clients.size() + 1;
		client.setID(ID);
		clients.put(ID, client);
	}

}
