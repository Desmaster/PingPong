package nl.tdegroot.software.pingpong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;

	private String name;
	private int port;

	private DatagramSocket socket;
	private InetAddress ipAddress;

	private Thread send;

	public Client(String name, String address, int port) {
		this.name = name;
		this.port = port;
		createWindow();
		if (!login(address)) {
			log("Unsuccessful connection!");
			System.out.println("Unsuccessful connection!");
		} else {
			System.out.println("Successfully connected to " + address + ":" + port);
		}
		send(("/c/" + name).getBytes());
	}

	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setResizable(false);
		setTitle("Command Line");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 638, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		textArea.setBounds(0, 0, 632, 350);
		textArea.setBackground(new Color(0, 0, 0));
		contentPane.add(textArea);

		textField = new JTextField();
		textField.setBounds(0, 353, 542, 26);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) send(textField.getText());
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(textField.getText());
			}
		});
		btnSend.setBounds(543, 353, 89, 26);
		contentPane.add(btnSend);
		textField.requestFocusInWindow();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				send("/x/" + name);
			}
		});

		setLocationRelativeTo(null);
	}

	public boolean login(String address) {
		try {
			ipAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		try {
			socket = new DatagramSocket();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		read(packet);
	}

	public void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	public void send(String msg) {
		if (msg.equals("")) return;
		String message = "/b/" + name + "~" + msg;
		send(message.getBytes());
		textField.setText("");
	}

	private void read(DatagramPacket packet) {
		String message = new String(packet.getData());
		log(message);
		System.out.println("Received a message");
	}

	private void log(String msg) {
		textArea.append(msg + "\n\r");
		System.out.println("Log: " + msg);
	}
}
