/*
** File: LocationRecord.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2007-03-01
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


/*
 * This class is nothing more than a struct. Public access is allowed to each variable to reduce the overhead
 * of calling another function just to return a value.
 */
public class LocationRecord {
	
	public String label;
	public double latitude;
	public double longitude;
	public boolean isValid;

	/**
	 * Constructor
	 * @param inputs	Array of three values, assumes 0 = Label, 1 = latitude, 2 = longitude
	 */
	public LocationRecord(String[] inputs) {
		isValid = true;
		label = inputs[0].trim();
		
		try {
			latitude = Double.parseDouble(inputs[1]);
		}
		catch (NumberFormatException e) {
			latitude = -9999;
			isValid = false;
		}
		
		try {
			longitude = Double.parseDouble(inputs[2]);
		}
		catch (NumberFormatException e) {
			longitude = -9999;
			isValid = false;
		}
	}
}
