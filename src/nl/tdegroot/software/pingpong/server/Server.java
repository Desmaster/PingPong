package nl.tdegroot.software.pingpong.server;

import nl.tdegroot.software.pingpong.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server implements Runnable {

    private List<ServerClient> clients = new ArrayList<ServerClient>();

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
            return;
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
                while (running) {

                }
            }
        };
        tick.start();
    }

    private void receive() {
        receive = new Thread("Receive") {
            public void run() {
                while (running) {
                    System.out.println(clients.size());
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    read(packet);
                }
            }
        };
        receive.start();
    }

    private void send(final byte[] data, final InetAddress address, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    private void send(String message, InetAddress address, int port) {
        String msg = "/b/" + message + "/e/";
        send(msg.getBytes(), address, port);
    }

    private void command(String message, InetAddress address, int port) {
        send(message.getBytes(), address, port);
    }

    private void broadcast(String message) {
        for (int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            send(message, client.address, client.port);
        }
    }

    private void broadcastExcept(String message, int ID) {
        for (int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            if (client.getID() == ID) continue;
            send(message, client.address, client.port);
        }
    }

    private void read(DatagramPacket packet) {
        String message = new String(packet.getData());
        if (message.startsWith("/c/")) {
            int id = UID.getIdentifier();
            System.out.println("Identifier: " + id);
            ServerClient client = new ServerClient(message.substring(3, message.length()).trim(), packet.getAddress(), packet.getPort(), id);
            connect(client);
        } else if (message.startsWith("/b/")) {
            String msg = message.split("/b/|/e/")[1];
            System.out.println("Got a broadcast request");
            broadcast(msg);
        } else if (message.startsWith("/x/")) {
            System.out.println("Got disconnect request: " + message.substring(3, message.length()));
            disconnect(Integer.parseInt(message.substring(3, message.length()).trim()));
        } else {
            System.out.println(message);
        }
    }

    private void connect(ServerClient client) {
        String ID = "/c/" + client.getID() + "/e/";
        command(ID, client.address, client.port);
        String users = "";
        for (int i = 0; i < clients.size(); i++) {
            users += clients.get(i).name;
            if (i == clients.size() - 1) break;
            users += ", ";
        }
        clients.add(client);
        send("Welcome to the chat!", client.address, client.port);
        send("Users online: " + users, client.address, client.port);
        String announcement = client.name + " connected to the chat!";
        broadcastExcept(announcement, client.getID());
    }

    private void disconnect(int id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == id) {
                clients.remove(i);
                System.out.println("Successfully disconnected client with ID: " + id);
                return;
            }
        }
        System.out.println("Failed to disconnect client with ID: " + id);
    }

}
