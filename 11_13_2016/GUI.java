import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class GUI {
	FTPClient client;
	private JFrame frame;
	private JTextField ServerTextField;
	private JTextField portTextField;
	private JTextField myPortTextField;
	private JTextField usernameBox;
	private JTextField hostNameTextField;
	private JTextField keyWordTextField;
	private JTextArea CommandTextBox;
	private JTextField commandTextField;
	private JTextArea table;
	private JComboBox speedComboBox;
	private JButton btnConnect;
	private JButton btnSearch;
	private JButton btnGo;
	private JButton DCButton;
	private ButtonListener bl;
	private String informationDisplay;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 * 
	 * @throws IOException
	 */
	public GUI() throws IOException {

		bl = new ButtonListener();
		initialize();
	}

	WindowAdapter exitListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			int confirm = JOptionPane.showOptionDialog(null, "Are You Sure to Close Application?", "Exit Confirmation",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (confirm == 0) {
				System.exit(0);
			}
		}
	};
	private JScrollPane scrollPane_1;

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {

		InetAddress ip = InetAddress.getLocalHost();
		frame = new JFrame();
		frame.setBounds(300, 300, 623, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel ConnectionInformationBox = new JPanel();
		ConnectionInformationBox.setBounds(0, 0, 611, 100);
		frame.getContentPane().add(ConnectionInformationBox);
		ConnectionInformationBox.setLayout(null);

		JLabel lblServerHostname = new JLabel("Server Hostname: ");
		lblServerHostname.setBounds(5, 5, 131, 15);
		lblServerHostname.setVerticalAlignment(SwingConstants.TOP);
		ConnectionInformationBox.add(lblServerHostname);

		ServerTextField = new JTextField();
		ServerTextField.setBounds(133, 3, 114, 19);
		ServerTextField.setHorizontalAlignment(SwingConstants.LEFT);

		ServerTextField.setText(ip.getHostAddress());
		ConnectionInformationBox.add(ServerTextField);
		ServerTextField.setColumns(10);

		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(250, 5, 39, 15);
		lblPort.setVerticalAlignment(SwingConstants.TOP);
		ConnectionInformationBox.add(lblPort);

		portTextField = new JTextField();
		portTextField.setBounds(292, 3, 114, 19);
		portTextField.setText("2597");
		portTextField.setColumns(10);
		ConnectionInformationBox.add(portTextField);

		btnConnect = new JButton("Connect");
		btnConnect.setBounds(416, 0, 183, 25);
		ConnectionInformationBox.add(btnConnect);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(0, 35, 81, 15);
		ConnectionInformationBox.add(lblUsername);
		lblUsername.setVerticalAlignment(SwingConstants.BOTTOM);

		usernameBox = new JTextField();
		usernameBox.setBounds(89, 33, 114, 19);
		ConnectionInformationBox.add(usernameBox);
		usernameBox.setText("zomerlej");
		usernameBox.setColumns(10);

		JLabel lblHostname = new JLabel("Hostname: ");
		lblHostname.setBounds(207, 35, 81, 15);
		ConnectionInformationBox.add(lblHostname);
		lblHostname.setVerticalAlignment(SwingConstants.BOTTOM);

		hostNameTextField = new JTextField();
		hostNameTextField.setBounds(302, 33, 104, 19);
		ConnectionInformationBox.add(hostNameTextField);
		hostNameTextField.setText(ip.getHostAddress());
		hostNameTextField.setColumns(10);

		JLabel lblSpeed = new JLabel("Speed: ");
		lblSpeed.setBounds(426, 35, 54, 15);
		ConnectionInformationBox.add(lblSpeed);
		lblSpeed.setVerticalAlignment(SwingConstants.BOTTOM);

		speedComboBox = new JComboBox();
		speedComboBox.setBounds(501, 30, 98, 24);
		ConnectionInformationBox.add(speedComboBox);
		speedComboBox.addItem("Ethernet");
		speedComboBox.addItem("Modem");
		speedComboBox.addItem("T1");
		speedComboBox.addItem("T3");

		JLabel portLabel = new JLabel("Port: ");
		portLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		portLabel.setBounds(207, 59, 81, 15);
		ConnectionInformationBox.add(portLabel);

		myPortTextField = new JTextField();
		myPortTextField.setText("");
		myPortTextField.setColumns(10);
		myPortTextField.setBounds(250, 57, 114, 19);
		ConnectionInformationBox.add(myPortTextField);

		DCButton = new JButton("Disconnect");
		DCButton.setBounds(416, 66, 183, 25);
		ConnectionInformationBox.add(DCButton);

		JPanel searchPanel = new JPanel();
		searchPanel.setBounds(0, 100, 611, 405);
		frame.getContentPane().add(searchPanel);
		searchPanel.setLayout(null);

		JLabel lblKeyword = new JLabel("Keyword: ");
		lblKeyword.setVerticalAlignment(SwingConstants.TOP);
		lblKeyword.setBounds(12, 12, 71, 15);
		searchPanel.add(lblKeyword);

		keyWordTextField = new JTextField();
		keyWordTextField.setHorizontalAlignment(SwingConstants.LEFT);
		keyWordTextField.setColumns(10);
		keyWordTextField.setBounds(87, 10, 114, 19);
		searchPanel.add(keyWordTextField);

		btnSearch = new JButton("Search");
		btnSearch.setBounds(206, 7, 92, 25);
		searchPanel.add(btnSearch);
		JPanel panel = new JPanel();
		panel.setBounds(0, 204, 611, 201);
		searchPanel.add(panel);
		panel.setLayout(null);

		JLabel lblEnterCommand = new JLabel("Enter Command: ");
		lblEnterCommand.setVerticalAlignment(SwingConstants.TOP);
		lblEnterCommand.setBounds(12, 12, 127, 15);
		panel.add(lblEnterCommand);

		commandTextField = new JTextField();
		commandTextField.setHorizontalAlignment(SwingConstants.LEFT);
		commandTextField.setColumns(10);
		commandTextField.setBounds(133, 10, 378, 19);
		panel.add(commandTextField);

		btnGo = new JButton("Go");
		btnGo.setBounds(519, 7, 84, 25);
		panel.add(btnGo);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 39, 587, 162);
		panel.add(scrollPane_1);

		CommandTextBox = new JTextArea();
		scrollPane_1.setColumnHeaderView(CommandTextBox);
		CommandTextBox.setEditable(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 33, 587, 159);
		searchPanel.add(scrollPane);

		table = new JTextArea();
		scrollPane.setColumnHeaderView(table);
		table.setEditable(false);
		// CommandTextBox.setEditable(false);

		btnConnect.addActionListener(bl);
		btnSearch.addActionListener(bl);
		btnGo.addActionListener(bl);
		DCButton.addActionListener(bl);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				DCButton.doClick();
			}
		}, "Shutdown-thread"));

	}

	private class ButtonListener implements ActionListener {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(btnConnect)) {
				DCButton.doClick();
				try {
					client = new FTPClient(Integer.parseInt(myPortTextField.getText()));
					informationDisplay = client.connect(ServerTextField.getText(),
							Integer.parseInt(portTextField.getText()), "USER" + usernameBox.getText(),
							hostNameTextField.getText(), speedComboBox.getSelectedItem().toString());
					CommandTextBox.setText(informationDisplay);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (ae.getSource().equals(btnSearch)) {
				client.search(keyWordTextField.getText());
				table.setText("");
				for (userInformation results : client.getFileInfo()) {
					table.setText(table.getText() + results.toString() + "\n");
				}
			}
			if (ae.getSource().equals(btnGo)) {
				try {
					String printToClient = client.ClientCommands(commandTextField.getText());
					CommandTextBox.append("\n" + printToClient);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (ae.getSource().equals(DCButton)) {
				try {
					if (client != null)
						client.Disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}