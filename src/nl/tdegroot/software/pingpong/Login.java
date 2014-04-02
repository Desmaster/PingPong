package nl.tdegroot.software.pingpong;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;
	private JTextField txtAddress;
	private JTextField txtPort;
	private JLabel lblNewLabel_1;
	private JButton btnLogin;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
        setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtUsername = new JTextField();
		txtUsername.setBounds(179, 76, 86, 20);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);

		txtAddress = new JTextField();
		txtAddress.setBounds(179, 107, 86, 20);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);

		txtPort = new JTextField();
		txtPort.setBounds(179, 138, 86, 20);
		contentPane.add(txtPort);
		txtPort.setColumns(10);

		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setBounds(115, 79, 54, 14);
		contentPane.add(lblNewLabel);

		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(125, 110, 44, 14);
		contentPane.add(lblAddress);

		lblNewLabel_1 = new JLabel("Port:");
		lblNewLabel_1.setBounds(145, 141, 24, 14);
		contentPane.add(lblNewLabel_1);

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		btnLogin.setBounds(176, 196, 89, 23);
		contentPane.add(btnLogin);
		setLocationRelativeTo(null);
	}

	private void login() {
		String username = txtUsername.getText();
		String address = txtAddress.getText();
		int port = Integer.parseInt(txtPort.getText());
		System.out.println("Going to login as " + username + " on " + address + ":" + port);
		ClientWindow client = new ClientWindow(username, address, port);
		client.setVisible(true);
		client.requestFocus();
		dispose();
	}
}

