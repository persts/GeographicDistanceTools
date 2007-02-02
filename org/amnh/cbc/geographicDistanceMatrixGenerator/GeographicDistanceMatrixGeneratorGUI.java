package org.amnh.cbc.geographicDistanceMatrixGenerator;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.io.IOException;

import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.amnh.cbc.core.SplashScreen;
import org.amnh.cbc.core.SimpleFileFilter;
import org.amnh.cbc.geospatial.core.SphericalFunctionEngine;

import edu.stanford.ejalbert.BrowserLauncher;

public class GeographicDistanceMatrixGeneratorGUI extends JFrame implements ActionListener, ItemListener, DocumentListener, HyperlinkListener {
	private boolean fileLoaded;
	private JButton browseFileButton;
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JTextArea matrixDisplay;
	private JTextField filename;
	private JTextField spheroidRadius;							
	private JComboBox spheroidList;
	private JComboBox outputDistanceUnits;
	private ButtonGroup matrixDisplayOptionsGroup;
	private JRadioButton fullMatrix;
	private JRadioButton lowerTriangular;
	private JRadioButton lowerTriangularDiagonal;
	private GeographicDistanceMatrixGeneratorEngine matrixGenerator;
	private int spheroidRadiusActionCount;					/* Need because JComboBox fires two events and we only want to deal with the second */
	private int outputDistanceUnitsActionCount;				/* Need because JComboBox fires two events and we only want to deal with the second */
	private Object eventFireControl;						/* Used to short circuit an ActionEvent loop between spheroidList and spheroidRadius */
	
	
	private String distanceUnits[][] = {{"meters", "m"},
										{"kilometers", "km"},
										{"nautical mi", "nm"},
										{"radians", "rad"},
										{"degrees", "deg"}};
	private String spheroids[][] = {{"User Defined", "0"},			/* Just the semi-major axis because we assume perfect sphere */
									{"WGS84","6378137"}};
	
	/*
	 * Constructor and Required Listener Methods
	 */
	
	/**
	 * Constuctor
	 */
	public GeographicDistanceMatrixGeneratorGUI() {
		SplashScreen SS = new SplashScreen("SplashScreenGraphic-GDMG.jpg", this);
		Thread splashThread = new Thread(SS, "SplashThread");
        splashThread.start();

        Dimension defaultDimension = new Dimension(120, 25);
        matrixGenerator = new GeographicDistanceMatrixGeneratorEngine();
        fileLoaded = false;
        eventFireControl = null;
        spheroidRadiusActionCount = 0;
        outputDistanceUnitsActionCount = 0;
        
        int frameWidth = 700;
        int frameHeight = 500;
		setTitle("Geographic Distance Matrix Generator");
        setSize(frameWidth, frameHeight);
        setResizable(false); /* not great style but */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(screenSize.width/2 - frameWidth/2, screenSize.height/2 - frameHeight/2);
        setSize(frameWidth, frameHeight);
        
        JMenuBar mBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        
        fileMenu.add(aboutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        mBar.add(fileMenu);
        setJMenuBar(mBar);

        /*
         * Input Display Area
         */
        JPanel inputDisplayArea = new JPanel(new BorderLayout());
        inputDisplayArea.setBorder(BorderFactory.createTitledBorder("Inputs"));
        
        JPanel fileSeletionPanel = new JPanel();
        filename = new JTextField();
        filename.setEditable(false);
        filename.setPreferredSize(new Dimension(475, 25));
        browseFileButton = new JButton("Browse");
        browseFileButton.setPreferredSize(defaultDimension);
        browseFileButton.addActionListener(this);
        fileSeletionPanel.add(new JLabel("Input File "));
        fileSeletionPanel.add(filename);
        fileSeletionPanel.add(browseFileButton);

        JPanel geographicParameters = new JPanel(new FlowLayout(FlowLayout.CENTER, 26, 0));
		String distanceUnitsName[] = new String[distanceUnits.length];
		for(int x = 0; x < distanceUnits.length; x++)
			distanceUnitsName[x] = distanceUnits[x][0];
		outputDistanceUnits = new JComboBox(distanceUnitsName);
		outputDistanceUnits.setPreferredSize(defaultDimension);
		outputDistanceUnits.setMaximumRowCount(5);
		outputDistanceUnits.addItemListener(this);

		String spheroidName[] = new String[spheroids.length];
		for(int x = 0; x < spheroids.length; x++)
			spheroidName[x] = spheroids[x][0];
		
		spheroidRadius = new JTextField();
        spheroidRadius.setPreferredSize(defaultDimension);
		spheroidList = new JComboBox(spheroidName);
		spheroidList.setPreferredSize(defaultDimension);
		spheroidList.setMaximumRowCount(3);
		spheroidList.addItemListener(this); 
		spheroidList.setSelectedIndex(1);
		
		spheroidRadius.setText(spheroids[spheroidList.getSelectedIndex()][1]);
		spheroidRadius.getDocument().addDocumentListener(this);
        
		geographicParameters.add(new JLabel("Output Distance In"));
		geographicParameters.add(outputDistanceUnits);
		geographicParameters.add(new JLabel("Select Spheroid"));
		geographicParameters.add(spheroidList);
		geographicParameters.add(spheroidRadius);

		inputDisplayArea.add(fileSeletionPanel, BorderLayout.NORTH);
		inputDisplayArea.add(geographicParameters, BorderLayout.SOUTH);
		
        getContentPane().add(inputDisplayArea, BorderLayout.NORTH);
		
        /*
         * Output Display Area
         */
        JPanel outputDisplayArea = new JPanel(new BorderLayout());
        outputDisplayArea.setBorder(BorderFactory.createTitledBorder("Output"));
		
        JPanel matrixDisplayOptions = new JPanel();
        fullMatrix = new JRadioButton("Full NxN Matrix");
        lowerTriangularDiagonal = new JRadioButton("Lower Triangular Matrix");
        lowerTriangular = new JRadioButton("Lower Triangular Matrix Without Diagonal");
        fullMatrix.setSelected(true);
        fullMatrix.addItemListener(this);
        fullMatrix.setActionCommand("FULL_MATRIX");
        lowerTriangularDiagonal.addItemListener(this);
        lowerTriangularDiagonal.setActionCommand("LOWER_TRIANGULAR_DIAGONAL");
        lowerTriangular.addItemListener(this);
        lowerTriangular.setActionCommand("LOWER_TRIANGULAR");
        
        matrixDisplayOptionsGroup = new ButtonGroup();
        matrixDisplayOptionsGroup.add(fullMatrix);
        matrixDisplayOptionsGroup.add(lowerTriangularDiagonal);
        matrixDisplayOptionsGroup.add(lowerTriangular);
        matrixDisplayOptions.add(fullMatrix);
        matrixDisplayOptions.add(lowerTriangularDiagonal);
        matrixDisplayOptions.add(lowerTriangular);
        
        matrixDisplay = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(matrixDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputDisplayArea.add(matrixDisplayOptions, BorderLayout.NORTH);
        outputDisplayArea.add(scrollPane, BorderLayout.CENTER);
        
        getContentPane().add(outputDisplayArea, BorderLayout.CENTER);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
    public void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();
        
        if(obj == browseFileButton)
        	loadFile();
        if(obj == exitItem)
        	System.exit(0);
        if(obj == aboutItem)
        	displayInfo();
    }

    /*
     *  (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged( ItemEvent e) {
    	Object obj = e.getSource();
    	
    	if(obj == spheroidList) {
    		if(eventFireControl == null)			/* if eventFireControl is null the chain of events was started by the combobox */
    			eventFireControl = spheroidList;
    		spheroidRadiusActionCount++;

    		/*
    		 * Quick hack to just updated the textfield the second event that JComboBox files
    		 * Otherwise it would be updated twice, Once for the current radius (deselect event)
    		 * And once for the new radius (selection event), and we only want the latter
    		 */
    		if(spheroidRadiusActionCount == 2) {											
    			/* 
    			 * However, let's not update spheroid raduius when user defined or if user is typing in textfield.
    			 * Typing in the text field will cause the combo box to fire two events also because the combo box is changed
    			 * to "user defined".
    			 */ 
    			if (spheroidList.getSelectedIndex()!= 0 && eventFireControl == spheroidList)  	
    				spheroidRadius.setText(spheroids[spheroidList.getSelectedIndex()][1]); /* this causes the textfield to fire an event - see comments in insertUpdate method */ 
    			
    			if(eventFireControl == spheroidList) /* clear evenFireControl if chain of events was started by the combobox */
    				eventFireControl = null;
    			spheroidRadiusActionCount = 0;
    		}
    	}
			
        if(fileLoaded) {
        	if(obj == fullMatrix && fullMatrix.isSelected())
        		generateMatrix();
        	else if(obj == lowerTriangularDiagonal && lowerTriangularDiagonal.isSelected())
        		generateMatrix();
        	else if(obj == lowerTriangular && lowerTriangular.isSelected())
        		generateMatrix();
        	else if(obj == outputDistanceUnits) {
        		outputDistanceUnitsActionCount++;
        		if(outputDistanceUnitsActionCount == 2) {		/* Quick hack to just generate matrix the second event that JComboBox files */
        			generateMatrix();							/* Otherwise matrix would be generate twice, Once for the current distance unit (deselect event) */
        			outputDistanceUnitsActionCount = 0;			/* And once for the new distance unit (selection event), and we only want the latter */
        		}
        	}
        }
    }

    /*
     *  (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e) {
        //Required but don't need
    }
    /*
     *  (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent e) {
        //Required but don't need
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e) {
    	Object obj = e.getDocument();

    	if(obj == spheroidRadius.getDocument()) {
    		if(eventFireControl == null)
    			eventFireControl = spheroidRadius;
    		if(eventFireControl == spheroidRadius)		/* Lock eventFireControl to this object so the Combobox dones not try to update the textfield */
    			spheroidList.setSelectedIndex(0);
    		if(SphericalFunctionEngine.validateNumericString(spheroidRadius.getText())) {
    			if(Double.parseDouble(spheroidRadius.getText()) < 0)
    				JOptionPane.showMessageDialog(this, "ERROR: [Format Error] Spheroid radius can be negative","ERROR", JOptionPane.WARNING_MESSAGE);
				else if(fileLoaded && Double.parseDouble(spheroidRadius.getText()) > 6300000)  /* Keeps generateMatrix() from being fired each key stroke */
					generateMatrix();
    		} 
    		else
				JOptionPane.showMessageDialog(this, "ERROR: [Format Error] Spheroid radius contains invalid characters","ERROR", JOptionPane.WARNING_MESSAGE);
    		
    		if(eventFireControl == spheroidRadius)		/* clear eventFireControl */
    			eventFireControl = null;
    	}
    }
    
    /*
     *  (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				BrowserLauncher.openURL(evt.getURL().toString());
			}
			catch (IOException e) {}
		}
			
	}
    
    /*
     * Additional Methods
     */
    /**
     * Displays a small dialog with information about this applicatoin
     */
	private void displayInfo() {
		/*
		 * It would be nice to move this out of the application as a whole. The text here is required by 
		 * Peter Ersts's instiution and funders. This required information conflicts (in PJE's opinion) 
		 * with the openness of this code. If changes to this application are made, it is requested that
		 * the modified code is sent back to Peter Ersts (ersts@amnh.org) so that it can be incorperated
		 * in an "official release" 
		 */
		String messageText = "<HTML><STYLE>body {font-size: 12pt;}</STYLE><BODY>"+
		 "This is version 1.0 of the Geographic Distance Matrix Generator "+
		 "written by Peter J. Ersts, Project Specialist with the <a HREF=\"http://cbc.amnh.org\">Center for "+
		 "Biodiversity and Conservation</a> at the <a HREF=\"http://amnh.org\">American Museum of Natural History</a>. "+
		 "Eric Albert, Ned Horning, and Sergios-Orestis Kolokotronis should be acknowledged for their contributions which have "+
		 "taken the form of code, constructive criticism, beta-testing and moral support. This "+
		 "application implements Eric Albert's BrowserLauncher class.<BR><BR>"+
		 "Questions, comments and bug reports can be posted on: <BR>"+
		 "<a HREF=\"http://geospatial.amnh.org\">http://geospatial.amnh.org</a><BR>"+
		 "The source code for this program is available upon request.<BR><BR>"+
		 "This work has been partially supported by NASA under award No. NAG5-8543 and NNG05G041G. "+
		 "Additionally, this program was prepared by the the above author(s) under "+
		 "award No. NA04AR4700191 and NA05SEC46391002 from the National Oceanic and Atmospheric "+
		 "Administration, U.S. Department of Commerce.  The statements, findings, "+
		 "conclusions, and recommendations are those of the author(s) and do not "+
		 "necessarily reflect the views of the National Oceanic and Atmospheric "+
		 "Administration or the Department of Commerce.<BR></BODY></HTML>";
		
		JDialog info = new JDialog(this, true);
		info.setSize(450,400);
		info.setTitle("About");
		info.setResizable(false);
		info.getContentPane().setBackground(Color.WHITE);
		info.setBackground(new Color(255,255,255));
		info.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		info.setLocation((int)(this.getLocation().getX()+(this.getWidth()/2)-(info.getWidth()/2)),(int)(this.getLocation().getY()+(this.getHeight()/2)-(info.getHeight()/2)));

		JEditorPane text = new JEditorPane();
		text.addHyperlinkListener(this);
		text.setContentType("text/html");
		text.setText(messageText);
		text.setEditable(false);

		JPanel logos = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
		logos.setBackground(new Color(255, 255, 255));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("cbc-blue-sm.jpg"))));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("nasa-sm.jpg"))));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("noaa-sm.jpg"))));
		
		info.getContentPane().add(logos, BorderLayout.SOUTH);
		info.getContentPane().add(text, BorderLayout.CENTER);
		info.setVisible(true);
	}
    
    /**
     * Calls GeographicDistanceMatrixEngine.generateMatrix method and displays the resulting matrix
     */
    private boolean generateMatrix() {
    	if(Double.parseDouble(spheroidRadius.getText()) < 6300000) {
    		JOptionPane.showMessageDialog(this, "Error [Parameter Error] Your Spheroid Radius is unrealalistically small","Error", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
    	Vector matrix = matrixGenerator.generateMatrix(Double.parseDouble(spheroidRadius.getText()),distanceUnits[outputDistanceUnits.getSelectedIndex()][1], matrixDisplayOptionsGroup.getSelection().getActionCommand()); /* Ignore NumberFormatException because number should be valid by this point */
    	matrixDisplay.setText("");
    	for (int x = 0; x < matrix.size(); x++)
    		matrixDisplay.append((String)matrix.elementAt(x)+"\n");
    	
    	return true;
    }

    /**
     * Display file chooser and initialize GeographicDistanceMatrixEngine
     *
     */
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new SimpleFileFilter(".txt", "Text Files"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fileChooser.showOpenDialog(null) != JFileChooser.CANCEL_OPTION) {
        	if(matrixGenerator.loadFromFile(this, fileChooser.getSelectedFile().getAbsolutePath())) {
        			filename.setText(fileChooser.getSelectedFile().getAbsolutePath());
        			fileLoaded = true;
        			generateMatrix();
        	}
        	else {
        			fileLoaded = false;
        	}
        		
        }
    }
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GeographicDistanceMatrixGeneratorGUI gdmgGUI = new GeographicDistanceMatrixGeneratorGUI();
	}

}
