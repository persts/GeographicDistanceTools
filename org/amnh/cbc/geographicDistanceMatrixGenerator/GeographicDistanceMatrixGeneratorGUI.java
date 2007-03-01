/*
** File: GeographicDistanceMatrixGeneratorGUI.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2007-02-07
** Revision Date: 2007-03-01
**
** Copyright (c) 2007, American Museum of Natural History. All rights reserved.
** 
** This library is free software; you can redistribute it and/or
** modify it under the terms of the GNU Library General Public
** License as published by the Free Software Foundation; either
** version 2 of the License, or (at your option) any later version.
** 
** This library is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Library General Public License for more details.
** 
** You should have received a copy of the GNU Library General Public
** License along with this library; if not, write to the
** Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
** MA 02110-1301, USA.
**
** This work has been partially supported by NASA under award No. NAG5-8543 and NNG05G041G 
** Additionally, this program was prepared by the the above author(s) under award 
** No. NA04AR4700191 and NA05SEC46391002 from the National Oceanic and Atmospheric
** Administration, U.S. Department of Commerce.  The statements, findings,
** conclusions, and recommendations are those of the author(s) and do not
** necessarily reflect the views of the National Oceanic and Atmospheric
** Administration or the Department of Commerce.
**
**/
package org.amnh.cbc.geographicDistanceMatrixGenerator;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;

import java.util.Vector;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import org.amnh.cbc.core.VersionCheck;
import org.amnh.cbc.core.SplashScreen;
import org.amnh.cbc.core.SimpleFileFilter;
import org.amnh.cbc.geospatial.SphericalFunctionEngine;

/**
 * This is the main window of the application
 * @author Peter J. Ersts
 *
 */
public class GeographicDistanceMatrixGeneratorGUI extends JFrame implements ActionListener, ItemListener, DocumentListener {
	/* Eclipse generated serialVersionUID */
	private static final long serialVersionUID = 7765098507171187206L;

	/* Current Version Number */
	private static final String version = "1.2.0";
	
	/** \brief Instance of the VersionCheck class */
	private VersionCheck versionCheck;
	/** \brief Instance of the matrix generating engine */
	private GeographicDistanceMatrixGeneratorEngine matrixGenerator;

	/** \brief Flag to indicate if a file has been loaded */
	private boolean fileLoaded;
	/** \brief Objected used to short circuit an ActionEvent loop between spheroidList and spheroidRadius */
	private Object eventFireControl;						
	/** \brief Event counter needed because JComboBox fires two events and we only want to deal with the second */
	private int spheroidRadiusActionCount;
	/** \brief Event counter needed because JComboBox fires two events and we only want to deal with the second */
	private int outputDistanceUnitsActionCount;				
		
	/** \brief Array holding the current distance units */
	private String distanceUnits[][] = {{"meters", "m"},
										{"kilometers", "km"},
										{"nautical mi", "nm"},
										{"radians", "rad"},
										{"degrees", "deg"}};
	/** \brief Array hlding the current hard coded speriod radi */
	private String spheroids[][] = {{"WGS84","6378137"},
			 						{"User Defined", "0"}}; /* User Defined must always be last */

	/* GUI Objects */
	private JButton browseFileButton;
	private JButton exportButton;
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JMenuItem versionItem;
	private JTextArea matrixDisplay;
	private JTextField filename;
	private JTextField spheroidRadius;							
	private JComboBox spheroidList;
	private JComboBox outputDistanceUnits;
	private ButtonGroup matrixDisplayOptionsGroup;
	private JRadioButton fullMatrix;
	private JRadioButton lowerTriangular;
	private JRadioButton lowerTriangularDiagonal;
	
	/*
	 * Constructor and Required Listener Methods
	 */
	
	/**
	 * Constuctor
	 * 
	 */
	public GeographicDistanceMatrixGeneratorGUI() {
		SplashScreen SS = new SplashScreen(this, "resources/SplashScreenGraphic-GDMG.jpg", 3500);
		Thread splashThread = new Thread(SS, "SplashThread");
        splashThread.start();
        
        versionCheck = new VersionCheck(this, version, "http://geospatial.amnh.org/open_source/gdmg/version", "http://geospatial.amnh.org/open_source/gdmg/download.php", 4000);
        Thread versionThread = new Thread(versionCheck, "VersionThread");
        versionThread.start();
        
        /*
         * Set up main window
         */
        Dimension defaultDimension = new Dimension(120, 25);
        matrixGenerator = new GeographicDistanceMatrixGeneratorEngine();
        fileLoaded = false;
        eventFireControl = null;
        spheroidRadiusActionCount = 0;
        outputDistanceUnitsActionCount = 0;
        
        int frameWidth = 725;
        int frameHeight = 500;
		setTitle("Geographic Distance Matrix Generator");
        setSize(frameWidth, frameHeight);
        //setResizable(false); /* not great style but */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(screenSize.width/2 - frameWidth/2, screenSize.height/2 - frameHeight/2);
        setSize(frameWidth, frameHeight);
        
        JMenuBar mBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);
        mBar.add(fileMenu);
        
        JMenu aboutMenu = new JMenu("About");
        versionItem = new JMenuItem("Version");
        versionItem.addActionListener(this);
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        aboutMenu.add(versionItem);
        aboutMenu.addSeparator();
        aboutMenu.add(aboutItem);
        mBar.add(aboutMenu);
        
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
		spheroidList.setSelectedIndex(0);
		
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
        
        exportButton = new JButton("Export");
        exportButton.addActionListener(this);
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonBar.add(exportButton);
        
        getContentPane().add(buttonBar, BorderLayout.SOUTH);
	}
	
	/**
	 * Required method
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
    public void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();
        
        if(obj == browseFileButton)
        	loadFile();
        else if(obj == exportButton)
        	exportResults();
        else if(obj == exitItem)
        	System.exit(0);
        else if(obj == aboutItem)
        	new About(this, version);
        else if(obj == versionItem)
        	versionCheck.display();
    }

    /**
     * Required method
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
    			if (spheroidList.getSelectedIndex()!= (spheroids.length-1) && eventFireControl == spheroidList)  	
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
        		if(outputDistanceUnitsActionCount == 2) {		/* Quick hack to just generate matrix on the second event that JComboBox fires */
        			generateMatrix();							/* Otherwise matrix would be generate twice, Once for the current distance unit (deselect event) */
        			outputDistanceUnitsActionCount = 0;			/* And once for the new distance unit (selection event), and we only want the latter */
        		}
        	}
        }
    }

    /**
     * Required method
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e) {
        //Required but don't need
    }
    
    /**
     * Required method
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent e) {
        //Required but don't need
    }
    
    /**
     * Required method
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e) {
    	Object obj = e.getDocument();

    	if(obj == spheroidRadius.getDocument()) {
    		if(eventFireControl == null)
    			eventFireControl = spheroidRadius;
    		if(eventFireControl == spheroidRadius)		/* Lock eventFireControl to this object so the Combobox dones not try to update the textfield */
    			spheroidList.setSelectedIndex(spheroids.length-1);
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
     * Additional Methods
     */

    
    /**
     * 
     */
    private boolean exportResults() {
		if(matrixDisplay.getText().equals(""))
			return false;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new SimpleFileFilter(".txt", "Text Files"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(fileChooser.showSaveDialog(this) != JFileChooser.CANCEL_OPTION) {
        	try {
        		BufferedWriter outputStream = null;
        		if(fileChooser.getSelectedFile().getName().toLowerCase().endsWith(".txt"))
        			outputStream = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
        		else
        			outputStream = new BufferedWriter(new FileWriter(new File(fileChooser.getSelectedFile().getAbsolutePath()+".txt")));
        		
        		outputStream.write(matrixDisplay.getText());
        		       		
        		outputStream.close();
        	}
        	catch (IOException e) {
        		JOptionPane.showMessageDialog(this, "An error occurred while writing to the export file","Write Error", JOptionPane.ERROR_MESSAGE);
        		return false;
        	}
        }
    	return true;
    }
    
    /**
     * Call GeographicDistanceMatrixEngine.generateMatrix method and displays the resulting matrix
     * 
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
}
