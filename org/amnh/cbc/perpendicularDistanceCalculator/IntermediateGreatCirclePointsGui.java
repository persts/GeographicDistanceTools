/*
** File: IntermediateGreatCirclePointsGui.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2005-10-13
** Revision Date: 2007-02-07
**
** Copyright (c) 2005 - 2007, American Museum of Natural History. All rights reserved.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.BorderLayout;
import java.text.DecimalFormat;

import javax.swing.*;

import org.amnh.cbc.geospatial.SphericalFunctionEngine;

/**
 * This class will display a window in which you can calculate intermediate points along a great circle
 * @author Peter J. Ersts
 *
 */
public final class IntermediateGreatCirclePointsGui extends JFrame {
	/* Eclipse generated serialVersionUID */
	private static final long serialVersionUID = 7741427362013326640L;
	
	/** \brief local instance of the spherical function engine */
	private SphericalFunctionEngine sfe;
	/** \brief Array holding the current distance units */
	private String distanceUnits[][] = {{"meters", "m"},
			{"kilometers", "km"},
			{"nautical mi", "nm"},
			{"radians", "rad"},
			{"degrees", "deg"}};
	/** \brief Array hlding the current hard coded speriod radi */
	private String spheroids[][] = {{"WGS84","6378137"},
			{"User Defined", "0"}};
	
	/* GUI objects */
	private JTextField point1Longitude;
	private JTextField point1Latitude;
	private JTextField point2Longitude;
	private JTextField point2Latitude;
	private JTextField spheroidRadius;
	private JComboBox spheroidList;
	private JComboBox outputDistanceUnits;
	private JTextField GCDFraction;
	private JLabel distanceSummary;
	private JLabel coordinatesPoint3;
	
	IntermediateGreatCirclePointsGui() {
		sfe = new SphericalFunctionEngine();
		Color baseColor = new Color(234,234,234);
		
		setTitle("Intermediate Great Circle Points");
		int frameWidth = 750;
		int frameHeight = 240;
        setResizable(false);  /* Not good programing but... */
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(screenSize.width/2 - frameWidth/2, screenSize.height/2 - frameHeight/2);
        setSize(frameWidth, frameHeight);
        setBackground(baseColor);
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        inputPanel.setBackground(baseColor);
        
        Dimension defaultDimension = new Dimension(120,20);
        // Row 0
        c.weightx = 0.5;
		c.ipadx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 0;
		
		JLabel label4 = new JLabel("Position 1");
		label4.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label4, c);
		
		point1Longitude = new JTextField();
		point1Longitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(point1Longitude, c);
		
		JLabel label5 = new JLabel("(longitude)");
		label5.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label5, c);
		
		JLabel label6 = new JLabel("Position 2");
		label6.setPreferredSize(defaultDimension);
		c.gridx = 3;
		inputPanel.add(label6, c);
		
		point2Longitude = new JTextField();
		point2Longitude.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(point2Longitude, c);
		
		JLabel label7 = new JLabel("(longitude)");
		label7.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label7, c);
		
		//Row 1
		c.gridy = 1;
		
		point1Latitude = new JTextField();
		point1Latitude.setPreferredSize(defaultDimension);
		c.gridx = 1;
		inputPanel.add(point1Latitude, c);
		
		JLabel label8 = new JLabel("(latitude)");
		label8.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 2;
		inputPanel.add(label8, c);
		
		
		point2Latitude = new JTextField();
		point2Latitude.setPreferredSize(defaultDimension);
		c.gridx = 4;
		inputPanel.add(point2Latitude, c);
		
		JLabel label9 = new JLabel("(latitude)");
		label9.setFont(new Font(null, Font.ITALIC, 12));
		c.gridx = 5;
		inputPanel.add(label9, c);
		
		//Row 2
		c.gridy = 2;
		c.gridwidth = 2;
		JLabel label14 = new JLabel("Caclulate coordinates of new point that is ");
		c.gridx = 0;
		inputPanel.add(label14, c);
		
		c.gridx = 2;
		c.gridwidth = 1;
		GCDFraction = new JTextField();
        GCDFraction.setPreferredSize(defaultDimension);
        inputPanel.add(GCDFraction, c);
        
        c.gridx = 3;
        c.gridwidth = 2;
		JLabel label15 = new JLabel("% of the distance between point 1 and 2");
		inputPanel.add(label15, c);
		
		//Row 3
		c.gridy = 3;
		
		JLabel label11 = new JLabel("Output Distance In");
		label11.setPreferredSize(defaultDimension);
		c.gridx = 0;
		inputPanel.add(label11, c);
		
		String distanceUnitsName[] = new String[distanceUnits.length];
		for(int x = 0; x < distanceUnits.length; x++)
			distanceUnitsName[x] = distanceUnits[x][0];
		outputDistanceUnits = new JComboBox(distanceUnitsName);
		outputDistanceUnits.setPreferredSize(defaultDimension);
		outputDistanceUnits.setBackground(baseColor);
		outputDistanceUnits.setMaximumRowCount(5);
		c.gridx = 1;
		inputPanel.add(outputDistanceUnits, c);
		
		
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
		
		JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        distanceSummary = new JLabel("");
        distanceSummary.setHorizontalAlignment(JLabel.CENTER);
        outputPanel.add(distanceSummary, BorderLayout.NORTH);
        coordinatesPoint3 = new JLabel("");
        coordinatesPoint3.setHorizontalAlignment(JLabel.CENTER);
        outputPanel.add(coordinatesPoint3, BorderLayout.SOUTH);
        
		JPanel buttons = new JPanel();
		JButton calculateButton = new JButton("Calculate");
		calculateButton.addActionListener(
				new ActionListener() {
					public void actionPerformed ( ActionEvent e) {
						calculate();
					}
		});
		buttons.add(calculateButton);
		
		getContentPane().add(inputPanel, BorderLayout.NORTH);
		getContentPane().add(outputPanel, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);
        setVisible(true);
		
	}
	
	/**
	 * Calculate great circle distance and dipslay result
	 * @return		False if an error was detected within the input data
	 */
	private boolean calculate() {
		if(emptyFieldsExist()) {
			JOptionPane.showMessageDialog(this, "All input fields must contain data","Empty Field Warning", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(!numberDataValid()) {
			JOptionPane.showMessageDialog(this, "A numberic field contains invalid characters","Number Field Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		DecimalFormat littleFormatter = new DecimalFormat("0.0000");
		DecimalFormat bigFormatter = new DecimalFormat("0.00000000");

		sfe.setSpheroidRadius(Double.parseDouble(spheroidRadius.getText()));
		double[] point3 = sfe.calculateIntermediateGreatCirclePoints(Double.parseDouble(point1Longitude.getText()), Double.parseDouble(point1Latitude.getText()), Double.parseDouble(point2Longitude.getText()), Double.parseDouble(point2Latitude.getText()), Double.parseDouble(GCDFraction.getText())/100);
		double distanceP12 = sfe.greatCircleDistance(Double.parseDouble(point1Longitude.getText()), Double.parseDouble(point1Latitude.getText()), Double.parseDouble(point2Longitude.getText()), Double.parseDouble(point2Latitude.getText()), distanceUnits[outputDistanceUnits.getSelectedIndex()][1]);
		double distanceP13 = sfe.greatCircleDistance(Double.parseDouble(point1Longitude.getText()), Double.parseDouble(point1Latitude.getText()), point3[0], point3[1], distanceUnits[outputDistanceUnits.getSelectedIndex()][1]);
				
		if(distanceUnits[outputDistanceUnits.getSelectedIndex()][1].equals("deg") || distanceUnits[outputDistanceUnits.getSelectedIndex()][1].equals("rad")){ 
			distanceSummary.setText("Distance P1->P2: "+bigFormatter.format(distanceP12)+" "+distanceUnits[outputDistanceUnits.getSelectedIndex()][1]+"          Distance P1->P3: "+bigFormatter.format(distanceP13)+" "+distanceUnits[outputDistanceUnits.getSelectedIndex()][1]);
			coordinatesPoint3.setText("P3 Longitude: "+bigFormatter.format(point3[0])+"          P3 Latitude: "+bigFormatter.format(point3[1]));
		}
		else {
			distanceSummary.setText("Distance P1->p2: "+littleFormatter.format(distanceP12)+" "+distanceUnits[outputDistanceUnits.getSelectedIndex()][1]+"          Distance P1->P3: "+bigFormatter.format(distanceP13)+" "+distanceUnits[outputDistanceUnits.getSelectedIndex()][1]);
			coordinatesPoint3.setText("P3 Longitude: "+bigFormatter.format(point3[0])+"          P3 Latitude: "+bigFormatter.format(point3[1]));
		}
		
		return true;
	}
	
    /**
     * Check input fields for missing data
     * @return		True if an imput field is found to be empty
     */
	private boolean emptyFieldsExist() {
		if(point1Longitude.getText().length() == 0)
			return true;
		if(point2Longitude.getText().length() == 0)
			return true;
		if(point1Latitude.getText().length() == 0)
			return true;
		if(point2Latitude.getText().length() == 0)
			return true;
		if(GCDFraction.getText().length() == 0)
			return true;
		if(spheroidRadius.getText().length() == 0)
			return true;
		return false;
	}
	
	/**
	 * Check all numeric input fields for possible invalid entries
	 * @return		False if an invalid number is encountered
	 */
	private boolean numberDataValid() {
		if(!SphericalFunctionEngine.validateNumericString(point1Longitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(point2Longitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(point1Latitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(point2Latitude.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(GCDFraction.getText()))
			return false;
		if(!SphericalFunctionEngine.validateNumericString(spheroidRadius.getText()))
			return false;
		return true;
	}
	
}
