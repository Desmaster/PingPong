package nl.tdegroot.software.pingpong;

import java.io.IOException;
import java.net.*;

public class Client {

	private String name;
    private String address;
	private int port;

	private DatagramSocket socket;
	private InetAddress ip;

	private Thread send;

    private int ID;

    public Client(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public boolean login(String address) {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        return true;
	}

    public String receive() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(packet.getData());
        return message;
    }

    public void send(final byte[] data) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

	private void read(DatagramPacket packet) {
		String message = new String(packet.getData());
//		log(message);
		System.out.println("Received a message");
	}

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
}
