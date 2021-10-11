package com.asml.work;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Robot;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.SystemColor;
import javax.swing.JScrollPane;

public class GUI {

	private JFrame frmAsml;
	private ConnectionDB db = new ConnectionDB();
	private Connection con = db.openConnection();
	private String degrees[] = { "DN_0Degree", "DN_90Degree", "DN_180Degree", "DN_270Degree", "UP_0Degree",
			"UP_90Degree", "UP_180Degree", "UP_270Degree" };
	private String file_path = null;
	private String fileName;
	private JFileChooser fileChooser;
	private JLabel lblSelectFromSettings = new JLabel("Browse from Settings");
    private JLabel lblNewLabel_3 = new JLabel("Stopped");
    
    private JFrame consoleFrame = new JFrame();
	private JTextArea consoleText = new JTextArea();
    
    String h_2_folder_directory;
	Process process;
	String currentDirectory = new File("").getAbsolutePath();
	Path asmlRestApiPath = Paths.get(currentDirectory).getParent();
//	Path asmlRestApiPath = Paths.get(currentDirectory);
//	String restPath = asmlRestApiPath.toString() + "/Resources/ASML-REST-SERVICE.jar";
	String restPath = asmlRestApiPath.toString() + "/ASML-REST-SERVICE.jar";
	String configPath = "--spring.config.location=file:" + asmlRestApiPath.toString() + "/application.properties";
	ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", restPath, configPath);
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmAsml.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public GUI() {
		
		initialize();
		
	}
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		
		frmAsml = new JFrame();
		frmAsml.setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/com/asml/work/scissor.png")));
		frmAsml.getContentPane().setBackground(UIManager.getColor("Panel.background"));
		frmAsml.setFont(new Font("DejaVu Sans", Font.BOLD, 17));
		frmAsml.setTitle("ASML AR WT Support Tool");
		frmAsml.setBackground(Color.BLUE);
		frmAsml.setBounds(100, 100, 503, 329);
		frmAsml.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAsml.getContentPane().setLayout(null);
		
//	    LABEL1
		JLabel lblSaveImageAs = new JLabel("Save screenshot as:");
		lblSaveImageAs.setHorizontalAlignment(SwingConstants.CENTER);
		lblSaveImageAs.setBounds(145, 54, 116, 23);
		frmAsml.getContentPane().add(lblSaveImageAs);
		
//		LABEL 2
		JLabel lblFileSavedSuccesful = new JLabel("Recent Activity :");
		lblFileSavedSuccesful.setFont(new Font("Dialog", Font.BOLD, 12));
		lblFileSavedSuccesful.setBounds(12, 114, 104, 22);
		frmAsml.getContentPane().add(lblFileSavedSuccesful);
		
//		LABEL 3
		JLabel fileNameLabel = new JLabel("Nil");
		fileNameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		fileNameLabel.setForeground(new Color(51, 204, 102));
		fileNameLabel.setBounds(135, 113, 341, 23);
		frmAsml.getContentPane().add(fileNameLabel);
		
//		SETTINGS MENU
		lblSelectFromSettings.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		lblSelectFromSettings.setBounds(176, 12, 267, 15);
		frmAsml.getContentPane().add(lblSelectFromSettings);

		try {

			String query = "select * from asml_file";
			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				lblSelectFromSettings.setText(rs.getString("folder_directory"));
				System.out.println(file_path);
				file_path = rs.getString("folder_directory") + "\\";
				System.out.println(file_path);
				System.out.println("Fetched data" + rs.getString("folder_directory"));
				h_2_folder_directory=  rs.getString("folder_directory");
				System.out.println("h2 = "+h_2_folder_directory);
				

			} else {
				System.out.println("NOTHING FOUND");
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		
//		SAVED NAMES FORMAT
		JComboBox comboBox = new JComboBox(degrees);
		comboBox.setBackground(UIManager.getColor("ComboBox.buttonBackground"));
		comboBox.setBounds(266, 54, 210, 23);
		frmAsml.getContentPane().add(comboBox);
//---------------------------------------------------------------------------------------------------------		
		
//		MENU BAR
		JMenuBar menuBar = new JMenuBar();
		frmAsml.setJMenuBar(menuBar);
		
//		SETTINGS ON MENU
		JMenu mnNewMenu = new JMenu("Settings");
		mnNewMenu.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.setIcon(new ImageIcon(GUI.class.getResource("/com/asml/work/settings.ico")));
		menuBar.add(mnNewMenu);
		JMenuItem mntmNewMenuItem = new JMenuItem("Storage Location");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				File file = null;
				int option = fileChooser.showOpenDialog(frmAsml);
				if (option == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile().getAbsoluteFile();

					System.out.println("Folder Selected: " + file.getName());
					System.out.println(file);
//	               label.setText("Folder Selected: " + file.getName());
				} else {
//	               label.setText("Open command canceled");
					System.out.println("Open command canceled");
				}

//	               DATABASE PORCESS
				try {
					String fileToDatabase = file.toString();
					System.out.println(fileToDatabase + "FILE TO DB");
					String updateQuery = "update asml_file set folder_directory = ? where id=1";
					PreparedStatement ps = con.prepareStatement(updateQuery);
					ps.setString(1, fileToDatabase);
//					ps.setInt(2, 1);
					ps.executeUpdate();
					System.out.println("File updatd to DB");
				} catch (SQLException e1) {
					System.out.println("ERROR\n" + e1);
					e1.printStackTrace();
				}
				file_path = file.toString() + "\\";
				System.out.println("Database "+file_path);
				lblSelectFromSettings.setText(file_path);

			}
		});

		mntmNewMenuItem.setSelected(true);
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnNewMenu.add(mntmNewMenuItem);
		
//------------------------------------------------------------------------------------------------		
//		SCREENSHOT 
		
		JButton btnNewButton = new JButton("Capture");
		btnNewButton.setBackground(SystemColor.activeCaptionBorder);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (h_2_folder_directory ==null){
					System.out.println("please select location");
					JOptionPane.showMessageDialog(frmAsml, "Please select storage location (ctrl+s) ", "WARNING",
							JOptionPane.WARNING_MESSAGE);
				     }

				else {

					try {
						System.out.println("INSIDE "+file_path);
						String result = comboBox.getItemAt(comboBox.getSelectedIndex()).toString();
						frmAsml.setState(JFrame.ICONIFIED);
						Thread.sleep(1000);
						Robot robot = new Robot();
						String format = "PNG";
						fileName = result + "." + format;

						Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
						BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
						System.out.println("Bufered image loaded");
						System.out.println(file_path);
						ImageIO.write(screenFullImage, "PNG", new File(file_path + fileName));
						System.out.println("Screenshot taken " + fileName);
//					lblFileSavedSuccesful.setForeground(null););
						fileNameLabel.setForeground(new Color(255, 0, 51));
						fileNameLabel.setText("Saved as " + fileName);
//					l3.setText("click capture for new image");
					}
					catch (Exception exception) {
						exception.printStackTrace();
						// TODO: handle exception
					}
				}
			}
		});
		btnNewButton.setBounds(372, 82, 104, 23);
		frmAsml.getContentPane().add(btnNewButton);

//		----------------------------------------------------------------------------------------
		JLabel lblNewLabel_1 = new JLabel("Stotage Location :");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(12, 12, 123, 15);
		frmAsml.getContentPane().add(lblNewLabel_1);
		
		
//		SPRING BOOT REST APPLICATION JAR START / STOP
		
//		START
		JButton start = new JButton("START");
		start.setBackground(SystemColor.info);
		start.setForeground(Color.BLUE);
		start.setFont(new Font("Tahoma", Font.BOLD, 11));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					process = processBuilder.start();
					System.out.println("RUNNING " + restPath);
					JOptionPane.showMessageDialog(frmAsml, "Service has been started");
					lblNewLabel_3.setText("Started");
					  start.setEnabled(false);

//				    INITIALIZING THREAD FOR START
					new Thread(new Runnable() {
						public void run() {
							try {
								String line;
								BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
								PrintStream printStream = new PrintStream(new CustomOutputStream(consoleText));
//								System.setOut(printStream);
//								System.setErr(printStream);
								while ((line = is.readLine()) != null) {
									System.out.println(line);
//									System.setOut(line);
//									System.setErr(line);
//									consoleText.setText(line);
									if (line.contains("Web server failed to start")) {
										JOptionPane.showMessageDialog(frmAsml,
												"PORT IS ALREADY IN USE, Please terminate the running process");
									}
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						}
					}).start();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		start.setBounds(294, 198, 89, 23);
		frmAsml.getContentPane().add(start);
		
//		STOP
		JButton stop = new JButton("STOP");
		stop.setBackground(SystemColor.info);
		stop.setForeground(Color.RED);
		stop.setFont(new Font("Tahoma", Font.BOLD, 11));
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String PID = Long.toString(process.pid());
				System.out.println(PID);
				process.destroyForcibly();
				System.out.println("STOPPED");
				JOptionPane.showMessageDialog(frmAsml, "Service has been stopped");
				lblNewLabel_3.setText("Stopped");
				start.setEnabled(true);
			}
		});
		stop.setBounds(393, 198, 89, 23);
		frmAsml.getContentPane().add(stop);
		
//		ASML REST LABEL
		JLabel lblNewLabel = new JLabel("ASML-REST-SERVICE");
		lblNewLabel.setForeground(SystemColor.infoText);
		lblNewLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
		lblNewLabel.setBounds(145, 197, 139, 23);
		frmAsml.getContentPane().add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(22, 185, 460, 48);
		frmAsml.getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(22, 43, 452, 2);
		frmAsml.getContentPane().add(separator_1);
		
		JLabel lblNewLabel_2 = new JLabel("Service Status:");
		lblNewLabel_2.setBounds(266, 244, 104, 14);
		frmAsml.getContentPane().add(lblNewLabel_2);
		
		
		lblNewLabel_3.setBounds(393, 244, 87, 14);
		frmAsml.getContentPane().add(lblNewLabel_3);
		
		JButton btnNewButton_1 = new JButton("Console");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consoleFrame.setBounds(100, 100, 825, 375);
				consoleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JScrollPane scrollPane = new JScrollPane();
				consoleFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
				
				scrollPane.setViewportView(consoleText);
				consoleText.setColumns(10);
				consoleFrame.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(165, 240, 89, 23);
		frmAsml.getContentPane().add(btnNewButton_1);
     }
	}
