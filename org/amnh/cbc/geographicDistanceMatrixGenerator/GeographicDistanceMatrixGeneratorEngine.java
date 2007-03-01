/*
** File: GeographicDistanceMatrixGeneratorEngine.java
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

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.amnh.cbc.geospatial.SphericalFunctionEngine;

/**
 * This class builds the geographic distance matrix
 * @author Peter J. Ersts
 *
 */
public class GeographicDistanceMatrixGeneratorEngine {
	/** \Brief a Vector to hold location records from the input file */
	private Vector rawData; 
	
	/**
	 * Constructor 
	 * 
	 */
	public GeographicDistanceMatrixGeneratorEngine() {
		rawData = new Vector();
	}
	
	/**
	 * Generates a matrix representing the great circle distances for all pairwise combinations of
	 * points stored in the rawData vector. If a coordinate is not a valid number, display ERROR in output
	 * 
	 * @param spheroidRadius			Radius of spherical representation of the earth.  Currently assumed to be in meeters
	 * @param units						The unit of measurement for the resulting distances
	 * @param outputFormat				The type of matrix to generate
	 * @return							A vector of string representing, where each string in the vector represents a row in the matrix
	 */
	public Vector generateMatrix(double spheroidRadius, String units, String outputFormat) {
		if( rawData.isEmpty())
			return null;
		
		String rowData = null;
		double distance = 0.0;
		NumberFormat formatter;
		Vector results = new Vector();
		SphericalFunctionEngine SFE = new SphericalFunctionEngine(spheroidRadius);
		
		if(units.equalsIgnoreCase("RAD") || units.equalsIgnoreCase("DEG"))
			formatter = new DecimalFormat("0.000000000");
		else
			formatter = new DecimalFormat("0.00");
	
		/*
		 * This could be made more elegant and compress into one routine but I am lazy ;)
		 */
		if(outputFormat.equalsIgnoreCase("FULL_MATRIX")) {
			rowData = new String("");
			for(int x = 0; x < rawData.size(); x++) {
				rowData = rowData+"\t"+((LocationRecord)rawData.elementAt(x)).label;
			}
			results.add(rowData);
			for(int y = 0; y < rawData.size(); y++) {	
				LocationRecord lr = (LocationRecord)rawData.elementAt(y);
				rowData = new String(lr.label);
				for(int x = 0; x < rawData.size(); x++) {
					LocationRecord lr2 = (LocationRecord)rawData.elementAt(x);
					if(lr.isValid && lr2.isValid) {
						distance = SFE.greatCircleDistance(lr.longitude, lr.latitude, lr2.longitude, lr2.latitude, units);
						rowData = rowData+"\t"+formatter.format(distance);
					}
					else
						rowData = rowData+"\tERROR";
				}
				results.add(rowData);
			}
		}
		
		if(outputFormat.equalsIgnoreCase("LOWER_TRIANGULAR_DIAGONAL")) {
			rowData = new String("");
			for(int x = 0; x < rawData.size(); x++) {
				rowData = rowData+"\t"+((LocationRecord)rawData.elementAt(x)).label;
			}
			results.add(rowData);
			for(int y = 0; y < rawData.size(); y++) {	
				LocationRecord lr = (LocationRecord)rawData.elementAt(y);
				rowData = new String(lr.label);
				for(int x = 0; x <= y; x++) {
					LocationRecord lr2 = (LocationRecord)rawData.elementAt(x);
					if(lr.isValid && lr2.isValid) {
						distance = SFE.greatCircleDistance(lr.longitude, lr.latitude, lr2.longitude, lr2.latitude, units);
						rowData = rowData+"\t"+formatter.format(distance);
					}
					else
						rowData = rowData+"\tERROR";
				}
				results.add(rowData);
			}
		}
		
		if(outputFormat.equalsIgnoreCase("LOWER_TRIANGULAR")) {
			rowData = new String("");
			for(int x = 0; x < rawData.size()-1; x++) {
				rowData = rowData+"\t"+((LocationRecord)rawData.elementAt(x)).label;
			}
			results.add(rowData);
			for(int y = 1; y < rawData.size(); y++) {	
				LocationRecord lr = (LocationRecord)rawData.elementAt(y);
				rowData = new String(lr.label);
				for(int x = 0; x < y; x++) {
					LocationRecord lr2 = (LocationRecord)rawData.elementAt(x);
					if(lr.isValid && lr2.isValid) {
						distance = SFE.greatCircleDistance(lr.longitude, lr.latitude, lr2.longitude, lr2.latitude, units);
						rowData = rowData+"\t"+formatter.format(distance);
					}
					else
						rowData = rowData+"\tERROR";
				}
				results.add(rowData);
			}
		}
		
		return results;		
	}

	/**
	 * Opens the input file, and check to make sure that each line only has three tokens.  The expected format of the input file is
	 * Lable, Latitude, Longitude.
	 * 
	 * @param mainWindow		The main application window
	 * @param filename			The name of the input file to load
	 * @return					True or false on successful load
	 */
	public boolean loadFromFile(JFrame mainWindow, String filename) {
		BufferedReader inputStream;
        try {
        	rawData = new Vector();
            inputStream = new BufferedReader(new FileReader(filename));
            String[] tokens;
            String inputLine = inputStream.readLine();
            while(inputLine != null) {
            	tokens = inputLine.split("[\t,]");
            	if(tokens.length != 3) {
            		JOptionPane.showMessageDialog(mainWindow, "ERROR: [Data Format] Exactly three tokens per line are expected from the input file, "+ tokens.length +" were encountered.","Error", JOptionPane.WARNING_MESSAGE);
            		return false;
            	}

            	rawData.add(new LocationRecord(tokens));
            	inputLine = inputStream.readLine();
            }
        }
        catch (FileNotFoundException e) {
        	JOptionPane.showMessageDialog(mainWindow, "ERROR: [File Not Found] The input file ["+ filename+"] could not be found.","Error", JOptionPane.WARNING_MESSAGE);
        	return false;
        }
        catch (IOException e) {
        	JOptionPane.showMessageDialog(mainWindow, "ERROR: [I/O Exception] A problem was encountered while reading your input file.","Error", JOptionPane.WARNING_MESSAGE);
        	return false;
        }	
        
		return true;
	}
}
