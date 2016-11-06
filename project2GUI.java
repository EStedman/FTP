import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class project2GUI {

	private JFrame frame;
	private JTextField ServerTextField;
	private JTextField portTextField;
	private JTextField usernameBox;
	private JTextField textField;
	private JTextField textField_1;
	private JTable table;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					project2GUI window = new project2GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public project2GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
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
		ServerTextField.setText("127.0.0.1");
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
		
		JButton btnConnect = new JButton("Connect");
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
		
		textField = new JTextField();
		textField.setBounds(302, 33, 104, 19);
		ConnectionInformationBox.add(textField);
		textField.setColumns(10);
		
		JLabel lblSpeed = new JLabel("Speed: ");
		lblSpeed.setBounds(426, 35, 54, 15);
		ConnectionInformationBox.add(lblSpeed);
		lblSpeed.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JComboBox speedComboBox = new JComboBox();
		speedComboBox.setBounds(501, 30, 98, 24);
		ConnectionInformationBox.add(speedComboBox);
		speedComboBox.addItem("Ethernet1");
		speedComboBox.addItem("Ethernet2");
		speedComboBox.addItem("Ethernet3");
		speedComboBox.addItem("Ethernet4");
		speedComboBox.addItem("Ethernet5");
		
		JPanel searchPanel = new JPanel();
		searchPanel.setBounds(0, 100, 611, 405);
		frame.getContentPane().add(searchPanel);
		searchPanel.setLayout(null);
		
		JLabel lblKeyword = new JLabel("Keyword: ");
		lblKeyword.setVerticalAlignment(SwingConstants.TOP);
		lblKeyword.setBounds(12, 12, 71, 15);
		searchPanel.add(lblKeyword);
		
		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.LEFT);
		textField_1.setColumns(10);
		textField_1.setBounds(87, 10, 114, 19);
		searchPanel.add(textField_1);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(206, 7, 92, 25);
		searchPanel.add(btnSearch);
		
		table = new JTable();
		table.setBounds(12, 48, 587, 128);
		searchPanel.add(table);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 204, 611, 201);
		searchPanel.add(panel);
		panel.setLayout(null);
		
		JLabel lblEnterCommand = new JLabel("Enter Command: ");
		lblEnterCommand.setVerticalAlignment(SwingConstants.TOP);
		lblEnterCommand.setBounds(12, 12, 127, 15);
		panel.add(lblEnterCommand);
		
		textField_2 = new JTextField();
		textField_2.setHorizontalAlignment(SwingConstants.LEFT);
		textField_2.setColumns(10);
		textField_2.setBounds(133, 10, 378, 19);
		panel.add(textField_2);
		
		JButton btnGo = new JButton("Go");
		btnGo.setBounds(519, 7, 84, 25);
		panel.add(btnGo);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(12, 39, 591, 162);
		panel.add(textArea);
		
	}
}
