/*
 *  The Stock graph application - in development by non-Mumps infected Lewis University student,
 *  
 *  Joseph Drozek - lead project handler
 *  
 *  		*---
 *   		* The original plan for this application was to be based on the stock market
 *   		* but has since changed to become a more GENERAl application that
 *   		* all users may used
 *   		*---
 *  
 *  This application a simply graphing utility.  It takes in a set of points and displays them
 *  in a simple and easy to visualize manner.  The program is able to serialize data into binary and Text formats.
 *  The goal for this program is to help model information from stocks to analyze trends that are currently in the
 *  market.  This program, while only in its early stages may seem underwhelming, but the end goal for this project
 *  is to contain a wide range of graphing functions which the user may employ at their own will.
 *  
 *  Currently this project allows for:
 *  
 *  * Input of any types of data containing numbers
 *  * The ability to label each of your data points
 *  * Plots data points onto a graph
 *  * Allows for multiple different data trends/sets
 *  
 *  Serialization Techniques available:
 *  1. Text files
 *  2. Binary files
 *  
 *  Some future goals (but currently out of my skillset) are:
 *  1. Accurate Regressions of Trends
 *  	* Line of Best Fit
 *  	* Slope/Tangent Line
 *  	* 
 *  2. Data Analysis and Statistics
 *  
 *  3. Enhanced customization of input data
 *  
 *  The Model class is GraphPoint
 *  The View class is GraphFrame
 *  The Controller class is GraphPanelController
 *  PointIO is the Serialization Controller class
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

class GraphPoint {
	private int x;
	private int y;
	private String name;
	private String color;
	private GraphPoint prevPoint;
	private boolean hasPrevious = true;
	public boolean isConnected() {
		if(hasPrevious) {
			prevPoint = null; //set the prevPoint to null so that we can start a new sequence of points
			return true; //return false to draw points in a new sequence.
		} else {
			return true;
		}
	}
	public void setHasPrevious(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public GraphPoint(String name, int x, int y, String color) {
		setName(name);
		setX(x);
		setY(y);
		setColor(color);
	}
	public String toString() {
		return String.format("%s %d %d %s", name,x,y,color);
	}

}
//PointIO - PointInputOutput - provides functions to read and write to files.
//It is used within GraphFrame to populate the Graph with data or save the data to a file.
class PointIO {
	private ArrayList<GraphPoint> graphpoints;
	public boolean writePointsToTextFile(File f) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(f)));
			for (GraphPoint p : graphpoints) {
				pw.println(p);
			}
			pw.close();
			return true;
		} catch(Exception ex) {
			return false;
		}
	}
	public void readPointsFromTextFile(File f) {
		ArrayList<GraphPoint> result = new ArrayList<GraphPoint>();
		try {
			Scanner sc = new Scanner(f);
			String line;
			String[] parts;
			graphpoints.clear();
			GraphPoint p;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				parts = line.split(" ");
				p = new GraphPoint(parts[0], Integer.parseInt(parts[1]),Integer.parseInt(parts[2]), parts[3]);
				graphpoints.add(p);
				
			}
			sc.close();
		} catch (Exception ex) {

		}
	}
	public boolean writePointsToBinaryFile(File f) {
		try { 
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(graphpoints);
			oos.close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public void readPointsFromBinaryFile(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			ArrayList<GraphPoint> newPoints = (ArrayList<GraphPoint>)(ois.readObject());
			graphpoints.clear();
			for (GraphPoint point : newPoints) {
				graphpoints.add(new GraphPoint(point.getName(),point.getX(),point.getY(),point.getColor()));
			}
			ois.close();
		} catch (Exception ex) {
		}
	}
	public PointIO(ArrayList<GraphPoint> graphpoints) {
		this.graphpoints = graphpoints;
	}
}
class GraphFrame extends JFrame implements ActionListener, KeyListener {
	private ArrayList<GraphPoint> graphpoints;
	private JTextField dataEntryField;
	private DrawGraphPanelController drawgpan;
	private PointIO pointIO;
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	//The keyPressed function in this program determines the color of the Point or whether it is continuous
	//based on the key pressed:
	public void configureMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem miOpen = new JMenuItem("Open");
		JFileChooser jfc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("binary","bin");
		jfc.addChoosableFileFilter(filter);
		jfc.setFileFilter(filter);
		filter = new FileNameExtensionFilter("text","txt");
		jfc.addChoosableFileFilter(filter);
		jfc.setFileFilter(filter);
		miOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f;
				graphpoints.clear();
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					if (f.getName().endsWith("bin")) {
						pointIO.readPointsFromBinaryFile(f);
						repaint();
					}
					else if (f.getName().endsWith("txt")) {
						pointIO.readPointsFromTextFile(f);
						repaint();
					}
				}
			}
		});
		//
		menuFile.add(miOpen);
		JMenuItem miSave = new JMenuItem("Save");
		miSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f;
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					f = jfc.getSelectedFile();
					if (f.getName().endsWith("bin")) {
						pointIO.writePointsToBinaryFile(f);
					}
					else {
						pointIO.writePointsToTextFile(f);
					}
				}
			}
		});
		menuFile.add(miSave);
		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(miExit);
		bar.add(menuFile);
		setJMenuBar(bar);
	}
	public void actionPerformed(ActionEvent e) {	
	}
	public void configureUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,1000,1000);
		setTitle("Grapher v0.1");
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		drawgpan = new DrawGraphPanelController(graphpoints);
		content.add(drawgpan, BorderLayout.CENTER);
		JPanel panelSouthEntry = new JPanel();
		panelSouthEntry.setLayout(new FlowLayout());
		JLabel dataEntryLabel = new JLabel("Enter data: (name x-coord y-coord)");
		dataEntryField = new JTextField(10);
		dataEntryField.addKeyListener(this);
		panelSouthEntry.add(dataEntryLabel);
		panelSouthEntry.add(dataEntryField);
		content.add(panelSouthEntry, BorderLayout.SOUTH);
		configureMenu();
	}
	public GraphFrame(ArrayList<GraphPoint> graphpoints) {
		this.graphpoints = graphpoints;
		pointIO = new PointIO(graphpoints);
		configureUI();
	}
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String s = dataEntryField.getText();
			Scanner sc = new Scanner(s);
			String value1 = sc.next();
			int value2 = sc.nextInt();
			int value3 = sc.nextInt();
			String value4;
			GraphFrame graphFrameMe = this;
			Object[] possibilities = {"black", "red", "green", "blue"};
			String colorpicker = (String)JOptionPane.showInputDialog(graphFrameMe,"Select a color:","Color Picker", JOptionPane.PLAIN_MESSAGE,null,possibilities,"black");
			if ((colorpicker != null) && (colorpicker.equals("black"))) {
				value4 = colorpicker;
			} else if (colorpicker.equals("red")) {
				value4 = colorpicker;
			} else if (colorpicker.equals("green")) {
				value4 = colorpicker;
			} else if (colorpicker.equals("blue")) {
				value4 = colorpicker;
			} else {
				value4 = "black";
			}
			graphpoints.add(new GraphPoint(value1, value2, value3, value4));
		}
		
	}
}
class DrawGraphPanelController extends JPanel {
	private ArrayList<GraphPoint> graphpoints;
	private GraphPoint prevPoint;
	private Color col;
	private final int SCORE = 20;;
	private final int GAP = 30;
	private final int GRAPH_POINT_WIDTH = 12;
	private final int Y_MARK_COUNT = 10;
	public Color getColor() {
		return col;
	}
	public void setColor(Color col) {
		this.col = col;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Formulas for determining the scale are as follows:
		//X - get the width of the screen then subtract 2 * the GAP then divide by the size of the ArrayList - 1
		//Y - get the Height of the screen then subtract 2 * the gap then divide by the max score
		double xGraphScale = ((double) getWidth() - 2 * GAP) / (graphpoints.size() - 1);
		double yGraphScale = ((double) getHeight() - 2 * GAP) / (SCORE - 1); 
	    for (int i = 0; i < Y_MARK_COUNT; i++) {
	       int x0 = GAP;
	       int x1 = GRAPH_POINT_WIDTH + GAP;
	       int y0 = getHeight() - (((i + 1) * (getHeight() - GAP * 2)) / Y_MARK_COUNT + GAP);
	       int y1 = y0;
	       g.drawLine(x0, y0, x1, y1);
	     }
	    for (int i = 0; i < graphpoints.size() - 1; i++) {
	       int x0 = (i + 1) * (getWidth() - GAP * 2) / (graphpoints.size() - 1) + GAP;
	       int x1 = x0;
	       int y0 = getHeight() - GAP;
	       int y1 = y0 - GRAPH_POINT_WIDTH;
	       g.drawLine(x0, y0, x1, y1);
	       repaint();
	    }
	    g.drawLine(GAP, getHeight() - GAP, GAP, GAP);
	    g.drawLine(GAP, getHeight() - GAP, getWidth() - GAP, getHeight() - GAP);
	    for (int i = 0; i < graphpoints.size(); i++) {
	    	int x = graphpoints.get(i).getX() - 12 / 2;
	    	int y = graphpoints.get(i).getY() - 12 / 2;
	    	if (graphpoints.get(i).getColor().equals("red")) {
	    		g.setColor(Color.RED);
	    	} else if (graphpoints.get(i).getColor().equals("blue")) {
	    		g.setColor(Color.BLUE);
	    	} else if (graphpoints.get(i).getColor().equals("green")) {
	    		g.setColor(Color.GREEN);
	    	} else {
	    		g.setColor(Color.BLACK);
	    	}
	    	g.fillOval(x, y, 12, 12);
	    	repaint();
	    }
	    for (GraphPoint p : graphpoints) {
			//Set the color of the graphics that are about to be displayed based on the point's color.
			//g.setColor(gf.getColor());
			//if the point is part of a sequence, then we will:
			if (p.isConnected()) {
				//first check if the previousPoint is not null and that lines are set to be drawn
				if (prevPoint != null) {
					//if lines are set to be drawn, then draw a line between the sequence of points
					if (p.getName().equals(prevPoint.getName())) {
						if (prevPoint.getColor().equals("red")) {
							g.setColor(Color.RED);
						} else if (prevPoint.getColor().equals("blue")) {
							g.setColor(Color.BLUE);
						} else if (prevPoint.getColor().equals("green")) {
							g.setColor(Color.GREEN);
						} else {
							g.setColor(Color.BLACK);
						}
						g.drawLine(prevPoint.getX()+12/2,prevPoint.getY()+12/2, p.getX()+12/2, p.getY()+12/2);	
					}
				}
			}
			//set prevPoint to p and move on
			prevPoint = p;
			repaint();
		}
	}
	public DrawGraphPanelController(ArrayList<GraphPoint> graphpoints) {
		this.graphpoints = graphpoints;
		setColor(Color.BLACK);
	}
}
public class GraphApp {
	public static void main(String[] args) {
		ArrayList<GraphPoint> graphpoints = new ArrayList<GraphPoint>();
		System.out.println("-------------------------");
		System.out.println("Name:  X-Coord:  Y-Coord:");
		graphpoints.add(new GraphPoint("Test",100,300,"black"));
		graphpoints.add(new GraphPoint("Test",200,350,"black"));
		graphpoints.add(new GraphPoint("Test",300,450,"black"));
		graphpoints.add(new GraphPoint("Line2",250,300,"blue"));
		graphpoints.add(new GraphPoint("Line2",350,500,"blue"));
		graphpoints.add(new GraphPoint("Line2",350,600,"blue"));
		for (GraphPoint p : graphpoints) {
			System.out.println(p);
		}
		System.out.println("-------------------------");
		GraphFrame gf = new GraphFrame(graphpoints);
		gf.setVisible(true);
	}
}