/*
** File: PerpepndicularDistanceCalculatorGui.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2004-10-10
**
** Copyright (c) 2004,2005 American Museum of Natural History. All rights reserved.
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
** This software is based upon work paritially supported by NASA under award number 
** NAG5-12333. Additionally, this program was prepared by the the above author(s) 
** under award No. NA04AR4700191 from the National Oceanic and Atmospheric
** Administration, U.S. Department of Commerce.  The statements, findings,
** conclusions, and recommendations are those of the author(s) and do not
** necessarily reflect the views of the National Oceanic and Atmospheric
** Administration or the Department of Commerce.
**
**/
package org.amnh.cbc.perpendicularDistanceCalculator;

/**
 *
 * TODO Allow user to define units for distanceToObservation field
 * TODO Add some level of help or roll-over hints
 * TODO Sync field names in DistanceCalculator, SphericalFunctionEngine, and manuscript
 * TODO Add FocusTraversalPolicy
 * 
 */

import java.awt.*;
import java.awt.event.*;

import java.text.DecimalFormat;

import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import org.amnh.cbc.core.VersionCheck;
import org.amnh.cbc.core.SplashScreen;
import org.amnh.cbc.core.SimpleFileFilter;

import org.amnh.cbc.geospatial.SphericalFunctionEngine;

/**
 * This class is the main window of the Perpendicular Distance Calculator
 * @author Peter J. Ersts
 *
 */
public class PerpendicularDistanceCalculatorGui extends JFrame implements ActionListener {
	/* Eclipse generated serialVersionUID */
	private static final long serialVersionUID = 2032448473434530299L;

	/* Current Version Number */
	private static final String version = "1.2.2";

	/** \brief Instance of the VersionCheck class */
	private VersionCheck versionCheck;
	
	/* GUI objects */
	private JTextField transectLegID;
	private JTextField transectLegStartLongitude;
	private JTextField transectLegStartLatitude;
	private JTextField transectLegEndLongitude;
	private JTextField transectLegEndLatitude;
	private JTextField observationLongitude;
	private JTextField observationLatitude;
	private JTextField observationDistance;
	private JTextField observationAngle;
	private JTextField sightingID;
	private JTextField spheroidRadius;
	private JComboBox spheroidList;
	private JComboBox outputDistanceUnits;
	private JTable displayArea;
	private DefaultTableModel tableModel;

	private JMenu fileMenu;
    private JMenu calculationMenu;
    private JMenuItem gcdItem;
    private JMenuItem igcpItem;
    private JMenuItem exitItem;
    private JMenuItem versionItem;
    private JMenuItem aboutItem;
    
	private JButton exportButton;
	private JButton runButton;
	private JButton clearButton;
	private JButton clearButton2;
	private JButton clearButton3;
	
	/** \brief Array holding the current distance units */
	private String distanceUnits[][] = {{"meters", "m"},
										{"kilometers", "km"},
										{"nautical mi", "nm"},
										{"radians", "rad"},
										{"degrees", "deg"}};
	/** \brief Array hlding the current hard coded speriod radi */
	private String spheroids[][] = {{"WGS84","6378137"},
			 						{"User Defined", "0"}};
								
	/**
	 * Constructor
	 *
	 */
	public PerpendicularDistanceCalculatorGui() {
		SplashScreen SS = new SplashScreen(this, "resources/SplashScreenGraphic-PDC.jpg", 3500);
        Thread splashThread = new Thread(SS, "SplashThread");
        splashThread.start();
        
        versionCheck = new VersionCheck(this, version, "http://biodiversityinformatics.amnh.org/open_source/pdc/version.php?query=true", "http://biodiversityinformatics.amnh.org/open_source/pdc/download.php", 4000);
        Thread versionThread = new Thread(versionCheck, "VersionThread");
        versionThread.start();
		
		/*
		 * Build main window
		 */
		int frameWidth = 725;
		int frameHeight = 400;
		Color baseColor = new Color(234,234,234);
        Dimension defaultDimension = new Dimension(120,20);
        
		setTitle("Perpendicular Distance Calculator");
        setResizable(false); /* Not good programing, but, for now the way it is...*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(screenSize.width/2 - frameWidth/2, screenSize.height/2 - frameHeight/2);
        setSize(frameWidth, frameHeight);
        
        /*
         * Menu Layout
         */
		JMenuBar mBar = new JMenuBar();
        fileMenu = new JMenu("File");
        calculationMenu = new JMenu("Calculations");
        gcdItem = new JMenuItem("Great Circle Distance");
        igcpItem = new JMenuItem("Intermediate Great Circle Point");
        exitItem = new JMenuItem("Exit");
        versionItem = new JMenuItem("Version");
        aboutItem = new JMenuItem("About");
        
        gcdItem.addActionListener(this);
        calculationMenu.add(gcdItem);
     
        igcpItem.addActionListener(this);
        calculationMenu.add(igcpItem);
        
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);
        
        JMenu aboutMenu = new JMenu("About");
        versionItem.addActionListener(this);
        aboutItem.addActionListener(this);
        aboutMenu.add(versionItem);
        aboutMenu.addSeparator();
        aboutMenu.add(aboutItem);

        mBar.add(fileMenu);
        mBar.add(calculationMenu);
        mBar.add(aboutMenu);
        setJMenuBar(mBar);

        /*
         * Main Input Area Layout
         */
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        
        // Row 0
        c.weightx = 0.5;
		c.ipadx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 0;
		
		JLabel label1 = new JLabel("Transect Leg ID");
		label1.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label1, c);
		
        transectLegID = new JTextField();
        transectLegID.setPreferredSize(defaultDimension);
		c.gridx = 1;
        inputPanel.add(transectLegID, c);
        
        JLabel label2 = new JLabel("Select Spheroid  ");
        label2.setPreferredSize(defaultDimension);
        label2.setHorizontalAlignment(JLabel.RIGHT);
        c.gridx = 2;
        inputPanel.add(label2, c);
        
        spheroidRadius = new JTextField();
        spheroidRadius.setPreferredSize(defaultDimension);
        
		String spheroidName[] = new String[spheroids.length];
		for(int x = 0; x < spheroids.length; x++)
			spheroidName[x] = spheroids[x][0];
		
		spheroidList = new JComboBox(spheroidName);
		spheroidList.setPreferredSize(defaultDimension);
		spheroidList.setBackground(baseColor);
		spheroidList.setMaximumRowCount(3);
		spheroidList.addItemListener( 
				new ItemListener() {
					public void itemStateChanged( ItemEvent e) {
						spheroidRadius.setText(spheroids[spheroidList.getSelectedIndex()][1]);
					}
		});
		spheroidList.setSelectedIndex(0);
		spheroidRadius.setText(spheroids[spheroidList.getSelectedIndex()][1]);
		c.gridx = 3;
        inputPanel.add(spheroidList, c);
        c.gridx = 4;
		inputPanel.add(spheroidRadius, c);
		
		JLabel label3 = new JLabel("(meters)");
		label3.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label3, c);
		
		//Row 1
		c.gridy = 1;
		
		JLabel label4 = new JLabel("Transect Leg Start");
		label4.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label4, c);
		
		transectLegStartLongitude = new JTextField();
		transectLegStartLongitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(transectLegStartLongitude, c);
		
		JLabel label5 = new JLabel("(longitude)");
		label5.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label5, c);
		
		JLabel label6 = new JLabel("Transect Leg End");
		label6.setPreferredSize(defaultDimension);
		c.gridx = 3;
		inputPanel.add(label6, c);
		
		transectLegEndLongitude = new JTextField();
		transectLegEndLongitude.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(transectLegEndLongitude, c);
		
		JLabel label7 = new JLabel("(longitude)");
		label7.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label7, c);
		
		//Row 2
		c.gridy = 2;
		
		transectLegStartLatitude = new JTextField();
		transectLegStartLatitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(transectLegStartLatitude, c);
		
		JLabel label8 = new JLabel("(latitude)");
		label8.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label8, c);
		
		
		transectLegEndLatitude = new JTextField();
		transectLegEndLatitude.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(transectLegEndLatitude, c);
		
		JLabel label9 = new JLabel("(latitude)");
		label9.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label9, c);

		//Row 3
		c.gridy = 3;
		c.gridx = 0;
		inputPanel.add(new JLabel(" "), c);
		
		//Row 4
		c.gridy = 4;
		
		JLabel label10 = new JLabel("Sighting ID");
		label10.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label10, c);
		
		sightingID = new JTextField();
		sightingID.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(sightingID, c);
		
		JLabel label11 = new JLabel("Output Distance In");
		label11.setPreferredSize(defaultDimension);
		c.gridx = 3;
		inputPanel.add(label11, c);
		
		String distanceUnitsName[] = new String[distanceUnits.length];
		for(int x = 0; x < distanceUnits.length; x++)
			distanceUnitsName[x] = distanceUnits[x][0];
		outputDistanceUnits = new JComboBox(distanceUnitsName);
		outputDistanceUnits.setPreferredSize(defaultDimension);
		outputDistanceUnits.setBackground(baseColor);
		outputDistanceUnits.setMaximumRowCount(5);
		c.gridx = 4;
		inputPanel.add(outputDistanceUnits, c);
		
		//Row 5
		c.gridy = 5;
		
		JLabel label12 = new JLabel("Position at Time of");
		label12.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label12, c);
		
		observationLongitude = new JTextField();
		observationLongitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(observationLongitude, c);

		JLabel label13 = new JLabel("(longitude)");
		label13.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label13, c);
		
		JLabel label14 = new JLabel("Detection Distance");
		label14.setPreferredSize(defaultDimension);
		c.gridx = 3;
		inputPanel.add(label14, c);
		
		observationDistance = new JTextField();
		observationDistance.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(observationDistance, c);
		
		
		JLabel label15 = new JLabel("(meters)");
		label15.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label15, c);
		
		//Row 6
		c.gridy = 6;
		
		JLabel label16 = new JLabel("Observation");
		label16.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label16, c);

		observationLatitude = new JTextField();
		observationLatitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(observationLatitude, c);
		
		JLabel label17 = new JLabel("(latitude)");
		label17.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label17, c);
		
		JLabel label18 = new JLabel("Bearing to Observation");
		c.gridx = 3;
		inputPanel.add(label18, c);
		
		observationAngle = new JTextField();
		observationAngle.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(observationAngle, c);

		/*
		 * Output Area Layout
		 */
        Vector columnNames = new Vector();
		columnNames.addElement("Leg ID");
		columnNames.addElement("Current Bearing");
		columnNames.addElement("Sighting ID");
		columnNames.addElement("Sighting Latitude");
		columnNames.addElement("Sighting Longitude");
		columnNames.addElement("Perpendicular Distance");

		displayArea = new JTable();
        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) { 
                return false; 	//Disable cell editing
            }
        };
        displayArea.setModel(tableModel);
        tableModel.setColumnIdentifiers(columnNames);
        
        
		displayArea.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        displayArea.getTableHeader().setReorderingAllowed(false);
        displayArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        displayArea.setCellSelectionEnabled(false);
        displayArea.setRowSelectionAllowed(true);
        
        displayArea.getColumnModel().getColumn(0).setPreferredWidth(75);
        displayArea.getColumnModel().getColumn(1).setPreferredWidth(100);
        displayArea.getColumnModel().getColumn(2).setPreferredWidth(75);
        displayArea.getColumnModel().getColumn(3).setPreferredWidth(125);
        displayArea.getColumnModel().getColumn(4).setPreferredWidth(125);
        displayArea.getColumnModel().getColumn(5).setPreferredWidth(190);
        
		JScrollPane outputPanel = new JScrollPane(displayArea);
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        /*
         * Button Bar Layout
         */
		JPanel buttons = new JPanel();
		exportButton = new JButton("Export");
		exportButton.addActionListener(this);
		clearButton = new JButton("Clear All");
		clearButton.addActionListener(this);
		clearButton2 = new JButton("Clear Last Row");
		clearButton2.addActionListener(this);
		clearButton3 = new JButton("Clear Selected Row");
		clearButton3.addActionListener(this);
		runButton = new JButton("Process OBS");
		runButton.addActionListener(this);

		buttons.add(exportButton);
		buttons.add(clearButton);
		buttons.add(clearButton2);
		buttons.add(clearButton3);
		buttons.add(runButton);
		buttons.setBackground(baseColor);
        
		/*
		 * Pack everything
		 */
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(outputPanel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        validate();
	} //Constructor
	
	/**
	 * Require method to implement ActionListener
	 */
    public void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();
        if(obj == aboutItem)
        	new About(this, version);
        else if(obj == clearButton) {
			for(int x = tableModel.getRowCount()-1; x >= 0; x--)
				tableModel.removeRow(x);        	
        }
        else if(obj == clearButton2) {
        	if(tableModel.getRowCount() != 0)
        		tableModel.removeRow(tableModel.getRowCount()-1);
        }
        else if(obj == clearButton3) {
			if(tableModel.getRowCount() != 0 && displayArea.getSelectedRow() != -1)
				tableModel.removeRow(displayArea.getSelectedRow());
        }
        else if(obj == exitItem)
        	System.exit(0);
        else if(obj == exportButton)
        	export();
        else if(obj == gcdItem)
        	new GreatCircleDistanceGui();
        else if(obj == igcpItem)
        	new IntermediateGreatCirclePointsGui();
        else if(obj == runButton)
        	processObservation();
        else if(obj == versionItem)
        	versionCheck.display();

    }
	
    /**
     * Check input fields for missing data
     * @return		True if an imput field is found to be empty
     */
	private boolean emptyFieldsExist() {
		if(transectLegID.getText().length() == 0)
			return true;
		if(transectLegStartLongitude.getText().length() == 0)
			return true;
		if(transectLegStartLatitude.getText().length() == 0)
			return true;
		if(transectLegEndLongitude.getText().length() == 0)
			return true;
		if(transectLegEndLatitude.getText().length() == 0)
			return true;
		if(observationLongitude.getText().length() == 0)
			return true;
		if(observationLatitude.getText().length() == 0)
			return true;
		if(observationDistance.getText().length() == 0)
			return true;
		if(observationAngle.getText().length() == 0)
			return true;
		if(sightingID.getText().length() == 0) 
			return true;
		if(spheroidRadius.getText().length() == 0)
			return true;
		
		return false;
	}
	
	/**
	 * Export table data to a text file
	 * @return		False if the table is empty or an error occurres
	 */
	private boolean export() {
		if(tableModel.getRowCount() == 0)
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
        		
        		for (int x = 0; x < tableModel.getColumnCount() - 1; x++)
        			outputStream.write(tableModel.getColumnName(x)+"\t");
        		outputStream.write(tableModel.getColumnName(tableModel.getColumnCount() - 1));
        		outputStream.newLine();
        		
        		for(int y = 0; y < tableModel.getRowCount(); y++) {
        			for(int x = 0; x < tableModel.getColumnCount() - 1; x++)
        				outputStream.write(tableModel.getValueAt(y, x)+"\t");
        			outputStream.write((String)tableModel.getValueAt(y, tableModel.getColumnCount() - 1));
        			if(y != tableModel.getRowCount() - 1)
        				outputStream.newLine();
        		}
        		
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
	 * Check all numeric input fields for possible invalid entries
	 * @return		False if an invalid number is encountered
	 */
	private boolean numberDataValid() {
		if(!SphericalFunctionEngine.validateNumericString(transectLegStartLongitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(transectLegStartLatitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(transectLegEndLongitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(transectLegEndLatitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(observationLongitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(observationLatitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(observationDistance.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(observationAngle.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(spheroidRadius.getText()))
			return false;
		return true;
	}
	
	/**
	 * Calculate perpendicular distance and dipslay result
	 * @return		False if an error was detected within the input data
	 */
	private boolean processObservation() {
		if(emptyFieldsExist()) {
			JOptionPane.showMessageDialog(this, "All input fields must contain data","Empty Field Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(!numberDataValid()) {
			JOptionPane.showMessageDialog(this, "A numeric field contains invalid characters","Number Field Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		DecimalFormat littleFormatter = new DecimalFormat("0.00");
		DecimalFormat bigFormatter = new DecimalFormat("0.00000000");
		SphericalFunctionEngine SFE = new SphericalFunctionEngine(Double.parseDouble(spheroidRadius.getText()));
		
		Vector newRow = new Vector();
		newRow.addElement(transectLegID.getText());
		String currentBearing = bigFormatter.format(SFE.initialBearing(Double.parseDouble(observationLongitude.getText()), Double.parseDouble(observationLatitude.getText()), Double.parseDouble(transectLegEndLongitude.getText()), Double.parseDouble(transectLegEndLatitude.getText()), "deg")); 
		newRow.addElement(currentBearing);
		newRow.addElement(sightingID.getText());
		String sightingLatitude = bigFormatter.format(SFE.calculateLatitude(Double.parseDouble(observationLatitude.getText()), Double.parseDouble(observationDistance.getText()), Double.parseDouble(observationAngle.getText()), "deg"));
		newRow.addElement(sightingLatitude);
		String sightingLongitude = bigFormatter.format(SFE.calculateLongitude(Double.parseDouble(observationLongitude.getText()), Double.parseDouble(observationLatitude.getText()), Double.parseDouble(observationDistance.getText()), Double.parseDouble(observationAngle.getText()), "deg")); 
		newRow.addElement(sightingLongitude);
		
		if(distanceUnits[outputDistanceUnits.getSelectedIndex()][1].equals("deg") || distanceUnits[outputDistanceUnits.getSelectedIndex()][1].equals("rad")) 
			newRow.addElement(bigFormatter.format(SFE.calculatePerpendicularDistance(Double.parseDouble(transectLegStartLongitude.getText()), Double.parseDouble(transectLegStartLatitude.getText()), Double.parseDouble(transectLegEndLongitude.getText()), Double.parseDouble(transectLegEndLatitude.getText()), Double.parseDouble(sightingLongitude), Double.parseDouble(sightingLatitude), distanceUnits[outputDistanceUnits.getSelectedIndex()][1])));
		else 
			newRow.addElement(littleFormatter.format(SFE.calculatePerpendicularDistance(Double.parseDouble(transectLegStartLongitude.getText()), Double.parseDouble(transectLegStartLatitude.getText()), Double.parseDouble(transectLegEndLongitude.getText()), Double.parseDouble(transectLegEndLatitude.getText()), Double.parseDouble(sightingLongitude), Double.parseDouble(sightingLatitude), distanceUnits[outputDistanceUnits.getSelectedIndex()][1])));
		
		tableModel.addRow(newRow);
		
		return true;
	}
}
