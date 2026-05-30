//import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;



//import javax.swing.*;
import javax.swing.SwingUtilities;



//import java.awt.event.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



//import java.awt.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;
import java.awt.FileDialog;



//import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;



//import java.util.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.Date;
import java.util.Timer;



//import java.text.*;
import java.text.SimpleDateFormat;




import Music.FgbMusicEp61_Fr.FgbMusicEp61;



public class FaceGameBat_3DJ_1_5 extends JFrame {
	
	
	
	// Methods
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				
				new FaceGameBat_3DJ_1_5().techno();
				
			}
			
		});
		
	}
	
	
	
	String FrameName = "FaceGameBat 3DJ 1.5";
	
	String FrameName1 = "FaceG";
	String FrameName2 = "ameB";
	String FrameName3 = "at 3DJ";
	String FrameName4 = " 1.5";
	
	int x = 200;
	int y = 150;
	int width = 1130;
	int height = 640;
	
	
	

	
	static Point compCoords;
	


	
	public void techno() {
		
		
		// Frame Title
		JFrame f1 = new JFrame(FrameName);
		
		// Frame Bounds
		f1.setBounds(x,y,width,height);
		
		// Frame Layout
		f1.setLayout(null);
		
		// Container
		Container c1 = f1.getContentPane();
		
		// Color Hex Background
		c1.setBackground(new Color(0xf1b04c));
		
		// Frame Logo
		ImageIcon i1 = new ImageIcon("Logo/logo-1.png");
		f1.setIconImage(i1.getImage());
		
		
		
		
		
		
		
		
		
		
		// Undecorated
		f1.setUndecorated(true);
		
		
		
		// Panel
		JPanel p1 = new JPanel();
		
		// Panel Bounds
		p1.setBounds(0,0,1130,45);
		
		// Layout
		p1.setLayout(null);
		
		// Color Hex Panel
		p1.setBackground(new Color(0x008000));
		
		// Panel ActionListener
		compCoords = null;
		p1.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				
				compCoords = null;
				
			}
			
			public void mousePressed(MouseEvent e) {
				
				compCoords = e.getPoint();
				
			}
			
			public void mouseExited(MouseEvent e) {
				
			}
			
			public void mouseEntered(MouseEvent e) {
				
			}
			
			public void mouseClicked(MouseEvent e) {
				
			}
			
		});
		
		p1.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {
				
			}
			
			public void mouseDragged(MouseEvent e) {
				
				Point currCoords = e.getLocationOnScreen();
				f1.setLocation(currCoords.x - compCoords.x, currCoords.y - compCoords.y);
				
			}
			
		});
		f1.add(p1);
		
		
		
		// Label Title
		JLabel logo = new JLabel();
		
		// Logo Billede 
		logo.setIcon(new ImageIcon("Logo/logo-2.png"));
		
		// Logo Bounds
		logo.setBounds(5,5,35,35);
		
		p1.add(logo);
		
		
		
		// Label Title
		JLabel t1 = new JLabel(FrameName1);
		
		// Label Bounds
	    t1.setBounds(45,5,45,35);
		
		// Color Hex Label Foreground
		t1.setForeground(new Color(0xFFFFFF));
		
		p1.add(t1);
		
		// Label Title
		JLabel t2 = new JLabel(FrameName2);
		
		// Label Bounds
	    t2.setBounds(80,5,45,35);
		
		// Color Hex Label Foreground
		t2.setForeground(new Color(0xFFFFFF));
		
		p1.add(t2);
		
		// Label Title
		JLabel t3 = new JLabel(FrameName3);
		
		// Label Bounds
	    t3.setBounds(113,5,45,35);
		
		// Color Hex Label Foreground
		t3.setForeground(new Color(0xFFFFFF));
		
		p1.add(t3);
		
		// Label Title
		JLabel t4 = new JLabel(FrameName4);
		
		// Label Bounds
	    t4.setBounds(149,5,45,35);
		
		// Color Hex Label Foreground
		t4.setForeground(new Color(0xFFFFFF));
		
		p1.add(t4);
		
		
		
		// Button Title
		JButton close = new JButton("X");
		close.setFocusable(false);
		
		// Button Bounds
		close.setBounds(1080,5,45,35);
		
		// Color Hex Button Foreground
		close.setForeground(new Color(0xFFFFFF));
		
		// Color Hex Button Background
		close.setBackground(new Color(0x0000FF));
		
		// Button ActionListener
		close.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource()==close) {
					
					f1.dispose();
					
				}
				
			}
			
		});
		p1.add(close);
		
		
		
		// Button Title
		JButton minimize = new JButton("--");
		minimize.setFocusable(false);
		
		// Button Bounds
		minimize.setBounds(1030,5,45,35);
		
		// Color Hex Button Foreground
		minimize.setForeground(new Color(0xFFFFFF));
		
		// Color Hex Button Background
		minimize.setBackground(new Color(0x0000FF));
		
		// Button ActionListener
		minimize.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource()==minimize) {
					
					f1.setExtendedState(JFrame.ICONIFIED);
					
				}
				
			}
			
		});
		p1.add(minimize);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Panel
		JPanel p4 = new JPanel();
		
		// Panel Bounds
		p4.setBounds(0,45,140,485);
		
		// Layout
		p4.setLayout(null);
		
		// Color Hex Panel
		p4.setBackground(new Color(0xd24e01));
		
		f1.add(p4);
		
		
		
		
		// Button Title
		JButton techno = new JButton("Techno");
		techno.setFocusable(false);
		
		// Button ImageIcon
		techno.setIcon(new ImageIcon("File_Logo/techno.png"));
		techno.setVerticalTextPosition(JButton.BOTTOM);
		techno.setHorizontalTextPosition(JButton.CENTER);
		
		// Button BorderFactory
		techno.setBorder(BorderFactory.createEmptyBorder());
		
		// Button Bounds
		techno.setBounds(10,10,120,60);
		
		// Color Hex Button Foreground
		techno.setForeground(new Color(0xFFFFFF));
		
		// Color Hex Button Background
		techno.setBackground(new Color(0xe27602));
		
		// Button ActionListener
		techno.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource()==techno) {
					
					f1.dispose();
					
					techno();
					
				}
				
			}
			
		});
		p4.add(techno);
		
		
		
		
		
		
		

		
		
		
		
		
		
		
		
		// Panel
		JPanel p5 = new JPanel();
		
		// Panel Bounds
		p5.setBounds(140,45,20,485);
		
		// Layout
		p5.setLayout(null);
		
		// Color Hex Panel
		p5.setBackground(new Color(0xec9006));
		
		f1.add(p5);
		
		
		
		
		
		
		
		
		
		// Button Title
		JButton fgbmusicep61 = new JButton("Fgb Music Ep 61");
		fgbmusicep61.setFocusable(false);
		
		// Button ImageIcon
		fgbmusicep61.setIcon(new ImageIcon("File_Logo/music_file.png"));
		fgbmusicep61.setVerticalTextPosition(JButton.BOTTOM);
		fgbmusicep61.setHorizontalTextPosition(JButton.CENTER);
		
		// Button BorderFactory
		fgbmusicep61.setBorder(BorderFactory.createEmptyBorder());
		
		// Button Bounds
		fgbmusicep61.setBounds(190,70,120,60);
		
		// Color Hex Button Foreground
		fgbmusicep61.setForeground(new Color(0x000000));
		
		// Color Hex Button Background
		fgbmusicep61.setBackground(new Color(0xf1b04c));
		
		// Button ActionListener
		fgbmusicep61.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource()==fgbmusicep61) {
					
					FgbMusicEp61 music = new FgbMusicEp61();
					
					JFrame musicFrame = new JFrame("Fgb Music Ep 61");
                    musicFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    musicFrame.add(music);
                    musicFrame.pack();
                    musicFrame.setLocationRelativeTo(null);
                    musicFrame.setVisible(true);
					
				}
				
			}
			
		});
		f1.add(fgbmusicep61);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Panel
		JPanel p2 = new JPanel();
		
		// Panel Bounds
		p2.setBounds(0,530,1130,25);
		
		// Layout
		p2.setLayout(null);
		
		// Color Hex Panel
		p2.setBackground(new Color(0x008000));
		
		f1.add(p2);
		
		
		
		// Panel
		JPanel p3 = new JPanel();
		
		// Panel Bounds
		p3.setBounds(0,555,1130,85);
		
		// Layout
		p3.setLayout(null);
		
		// Color Hex Panel
		p3.setBackground(new Color(0x000080));
		
		f1.add(p3);
	
	
	
	
	
	
	
		
		// Start Frame Visible
		f1.setVisible(true);
		
		
		
	}
	
	
}