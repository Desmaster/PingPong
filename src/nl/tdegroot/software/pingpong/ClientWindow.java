package nl.tdegroot.software.pingpong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;

public class ClientWindow extends JFrame implements Runnable {

    private JTextField txtMessage;
    private JTextArea console;
    private DefaultCaret caret;

    private Client client;

    private Thread run, listen;

    private boolean running = false;

    public ClientWindow(String name, String address, int port) {
        client = new Client(name, address, port);
        createWindow();
        if (!client.login(address)) {
            log("Unsuccessful connection!");
            System.out.println("Unsuccessful connection!");
        } else {
            System.out.println("Successfully connected to " + address + ":" + port);
        }
        client.send(("/c/" + name).getBytes());
        running = true;
        run = new Thread(this, "Running");
        run.start();
    }

    private void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setResizable(false);
        setTitle("Command Line");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 638, 408);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        console = new JTextArea();
        console.setEditable(false);
        console.setForeground(Color.WHITE);
        console.setFont(new Font("Monospaced", Font.PLAIN, 13));
        console.setBounds(0, 0, 632, 350);
        console.setBackground(new Color(0, 0, 0));
        contentPane.add(console);

        caret = (DefaultCaret) console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        txtMessage = new JTextField();
        txtMessage.setBounds(0, 353, 542, 26);
        contentPane.add(txtMessage);
        txtMessage.setColumns(10);
        txtMessage.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) send(txtMessage.getText());
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send(txtMessage.getText());
            }
        });
        btnSend.setBounds(543, 353, 89, 26);
        contentPane.add(btnSend);
        txtMessage.requestFocusInWindow();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.send(("/x/" + client.getID()).getBytes());
            }
        });

        setLocationRelativeTo(null);
    }

    public void run() {
        listen();
    }

    public void listen() {
        listen = new Thread("Listen") {
            public void run() {
                while (running) {
                    String message = client.receive();
                    boolean usable = false;
                    if (message.startsWith("/c/")) {
                        client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
                        log("Successfully connected to server! ID: " + client.getID());
                        usable = true;
                    } else if (message.startsWith("/b/")) {
                        String text = message.split("/b/|/e/")[1];
                        log(text);
                        usable = true;
                    }
                }
            }
        };
        listen.start();
    }

    private void command() {

    }

    public void send(String message) {
        if (message.equals("")) return;
        message = client.getName() + ": " + message;
        message = "/b/" + message + "/e/";
        client.send(message.getBytes());
        txtMessage.setText("");
    }

    private void log(String msg) {
        console.append(msg + "\n\r");
        console.setCaretPosition(console.getDocument().getLength());
        System.out.println("Log: " + msg);
    }
}
