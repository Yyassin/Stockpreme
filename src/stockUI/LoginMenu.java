/*
 * Login Menu
 * by Shrish Mohapatra
 * 
 * 	- users can login or create new accounts
 */

package stockUI;

import stockLogic.Process;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing;

public class LoginMenu {

	private JFrame frame;
	
	// global UI references
	private JTextField userField;
	private JPasswordField passField;
		
		// error labels
		private JLabel lbl_userError;
		private JLabel lbl_passError;
		private JLabel lbl_mainError;
	
	// used for making window draggable
	int xMouse;
	int yMouse;
	
	// COLORS
	Color COL_TEXT = Color.WHITE;
	Color COL_HOVER = Color.LIGHT_GRAY;
	Color COL_ERROR = Color.LIGHT_GRAY;		

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginMenu window = new LoginMenu();
					window.frame.setUndecorated(true);
					window.frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LoginMenu() {
		Process.Initialize();
		initialize();
	}
	
	// UI Actions
	private void _ToDashboard() {
		frame.dispose();
        Dashboard dash = new Dashboard();
        dash.setUndecorated(true);
        dash.setVisible(true);
	}
	
	private void _LoginAction() {
		String userInput = userField.getText();
		@SuppressWarnings("deprecation")
		String passInput = passField.getText();		
				
		lbl_userError.setText(Process.CheckInput(userInput, "username"));
		lbl_passError.setText(Process.CheckInput(passInput, "password"));
		
		if (lbl_userError.getText() == "" && lbl_passError.getText() == "") {
			if(Process.FindAccount(userInput, passInput) == null) {
				lbl_mainError.setText("ERROR: Username or password is incorrect.");
				return;
			}
		} else {
			return;
		}
		
		Process.curAccount = (Process.FindAccount(userInput, passInput));
		
		_ToDashboard();
	}
	
	private void _SignupAction() {
		String userInput = userField.getText();
		@SuppressWarnings("deprecation")
		String passInput = passField.getText();
		
		lbl_userError.setText(Process.CheckInput(userInput, "username"));
		lbl_passError.setText(Process.CheckInput(passInput, "password"));				
		
        if (lbl_userError.getText() == "" && lbl_passError.getText() == "") {
        	
        	if(Process.FindAccount(userInput) != null) {
        		lbl_mainError.setText("ERROR: User already exists.");
        		return;
        	}        	
            
        } else {
        	return;
        }
        
        Process.CreateAccount(userInput, passInput);
        Process.curAccount = (Process.FindAccount(userInput, passInput));
        
        _ToDashboard();
	}	
	
	// UI Componenets
	private void _titleCredits(JPanel contentPane) {
		JLabel lbl_title = new JLabel("Stockpreme");
		lbl_title.setForeground(COL_TEXT);
		lbl_title.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 50));
		lbl_title.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_title.setBounds(200, 50, 400, 85);
		contentPane.add(lbl_title);			
		
		JLabel lbl_credits = new JLabel("Kim, Yousef, Shrish");
		lbl_credits.setBounds(200, 125, 400, 28);
		contentPane.add(lbl_credits);
		lbl_credits.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_credits.setForeground(Color.WHITE);
		lbl_credits.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 13));
	}
	
	private void _loginInfoArea(JPanel contentPane) {
		JPanel infoPanel = new JPanel();
		infoPanel.setBounds(50, 175, 325, 250);
		infoPanel.setOpaque(false);
		contentPane.add(infoPanel);
		infoPanel.setLayout(null);
		
		JLabel lbl_userTitle = new JLabel("U S E R N A M E");
		lbl_userTitle.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 12));
		lbl_userTitle.setForeground(COL_TEXT);
		lbl_userTitle.setBounds(10, 30, 158, 14);
		infoPanel.add(lbl_userTitle);
		
		userField = new JTextField() {
			@Override public void setBorder(Border border) {
		        // remove border
		    }
		};
		userField.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		userField.setOpaque(false);
		userField.setForeground(COL_TEXT);
		userField.setCaretColor(COL_TEXT);
		userField.setBounds(10, 50, 305, 40);
		infoPanel.add(userField);
		userField.setColumns(10);
		
		JSeparator userSeparate = new JSeparator();
		userSeparate.setBounds(10, 86, 305, 2);
		infoPanel.add(userSeparate);
		
		lbl_userError = new JLabel("");
		lbl_userError.setForeground(COL_ERROR);
		lbl_userError.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_userError.setBounds(20, 101, 295, 14);
		infoPanel.add(lbl_userError);
		
		JLabel lbl_passTitle = new JLabel("P A S S W O R D");
		lbl_passTitle.setForeground(COL_TEXT);
		lbl_passTitle.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 12));
		lbl_passTitle.setBounds(10, 153, 158, 14);
		infoPanel.add(lbl_passTitle);
		
		passField = new JPasswordField() {
			public void setBorder(Border border) {
			}
		};
		passField.setOpaque(false);
		passField.setForeground(COL_TEXT);
		passField.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 14));
		passField.setColumns(10);
		passField.setCaretColor(COL_TEXT);
		passField.setBounds(10, 174, 305, 40);
		infoPanel.add(passField);
		
		JSeparator passSeparator = new JSeparator();
		passSeparator.setBounds(10, 210, 305, 2);
		infoPanel.add(passSeparator);
		
		lbl_passError = new JLabel("");
		lbl_passError.setForeground(COL_ERROR);
		lbl_passError.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 11));
		lbl_passError.setBounds(20, 225, 295, 14);
		infoPanel.add(lbl_passError);
	}

	private void _loginActionArea(JPanel contentPane) {
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(null);
		loginPanel.setOpaque(false);
		loginPanel.setBounds(423, 185, 325, 250);
		contentPane.add(loginPanel);
		
		JLabel lbl_Login = new JLabel("LOG IN");
		lbl_Login.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				_LoginAction();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_Login.setForeground(COL_HOVER);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_Login.setForeground(COL_TEXT);
			}
		});
		lbl_Login.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_Login.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 20));
		lbl_Login.setForeground(COL_TEXT);
		lbl_Login.setBounds(10, 96, 135, 50);
		loginPanel.add(lbl_Login);
		
		JLabel lbl_or = new JLabel("or");
		lbl_or.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_or.setForeground(COL_ERROR);
		lbl_or.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 12));
		lbl_or.setBounds(142, 109, 40, 30);
		loginPanel.add(lbl_or);
		
		JLabel lbl_Signup = new JLabel("SIGN UP");
		lbl_Signup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				_SignupAction();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_Signup.setForeground(COL_HOVER);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_Signup.setForeground(COL_TEXT);
			}
		});
		lbl_Signup.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_Signup.setForeground(COL_TEXT);
		lbl_Signup.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 20));
		lbl_Signup.setBounds(180, 96, 135, 50);
		loginPanel.add(lbl_Signup);
		
		lbl_mainError = new JLabel("");
		lbl_mainError.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_mainError.setForeground(COL_ERROR);
		lbl_mainError.setFont(new Font("Microsoft JhengHei Light", Font.PLAIN, 12));
		lbl_mainError.setBounds(10, 139, 305, 30);
		loginPanel.add(lbl_mainError);
	}
	
	private void _draggable(JPanel contentPane) {		
		JLabel lbl_draggable = new JLabel();		
		lbl_draggable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				xMouse = arg0.getX();
				yMouse = arg0.getY();
			}
		});
		lbl_draggable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				
				frame.setLocation(x - xMouse,y - yMouse);
			}
		});
		lbl_draggable.setBounds(0, 0, 748, 28);
		contentPane.add(lbl_draggable);
	}
	
	private void _quitButton(JPanel contentPane) {
		JLabel lbl_Quit = new JLabel("X");
		lbl_Quit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.exit(0);
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lbl_Quit.setForeground(COL_HOVER);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				lbl_Quit.setForeground(COL_TEXT);
			}
		});
		lbl_Quit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbl_Quit.setVerticalAlignment(SwingConstants.TOP);
		lbl_Quit.setHorizontalAlignment(SwingConstants.TRAILING);
		lbl_Quit.setForeground(COL_TEXT);
		lbl_Quit.setBounds(761, 11, 23, 28);
		contentPane.add(lbl_Quit);
	}

	private void initialize() {
		ImageIcon icon = new ImageIcon(LoginMenu.class.getResource("/img/forest.jpg"));
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		frame.getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);
		
		_titleCredits(contentPane);
		
		_loginInfoArea(contentPane);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(398, 185, 2, 250);
		contentPane.add(separator);
		
		_loginActionArea(contentPane);
		
		_draggable(contentPane);
		
		_quitButton(contentPane);
		
		JLabel lbl_bg = new JLabel("", icon, JLabel.CENTER);
		lbl_bg.setBounds(-110, -69, 964, 587);
		contentPane.add(lbl_bg);
	}
}
