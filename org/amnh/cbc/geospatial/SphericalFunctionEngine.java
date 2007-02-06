/*
** File: SphericalFunctionEngine.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2004-11-10
** Revision Date: 2005-11-09
**
** version 1.1_RC_1 [2005-11-09] Release candidate packaged for distribution (P.J.Ersts)
**
** version 1.1_PR [2005-10-19] Modified some parameter names and added additional comments (P.J.Ersts)
**
** version 1.1_PR [2005-10-13] Fix initialBearing(....) so the result is relative to 360 deg (P.J.Ersts)
**
** version 1.0_PR [2004-11-25] (P.J.Ersts)
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
package org.amnh.cbc.geospatial;

public class SphericalFunctionEngine {
	static final int WGS84 = 6378137;
	private double spheroidRadius;

	/**
	 * Constructor, sets default spherical representation of the earth to WGS84.
	 */
	public SphericalFunctionEngine() {
		spheroidRadius = WGS84;
	}
	
	/**
	 * Constructor
	 * @param value	Radius of spherical representation of the earth.  Currently assumed to be in meeters. 
	 */
	public SphericalFunctionEngine(double value) {
		spheroidRadius = value;
	}
	
	/**
	 * Calculates the initial bearing from one geographic position (Point1) to a second 
	 * geographic position (Point2).  Longitude and Latitude are expected to be represented
	 * as decimal degrees and south and west values are expected to be negative.
	 * 
	 * @param longitudePoint1	Longitude of the start or initial position, Point1, expressed in in decimal degrees.
	 * @param latitudePoint1	Latitude of the start or initial position, Point1, expressed in in decimal degrees.
	 * @param longitudePoint2	Longitude of the destination, Point2, expressed in in decimal degrees.
	 * @param latitudePoint2	Latitude of the destination, Point2, expressed in in decimal degrees.
	 * @param units				The units that the bearing sould be returned in.  Valid options are "rad" for radians and "deg" for decimal degrees.
	 * @return					Initial Bearing, east of true north, from Point1 to Point2
	 */
	public double initialBearing(double longitudePoint1, double latitudePoint1, double longitudePoint2, double latitudePoint2, String units) {
		double p1x = Math.toRadians(longitudePoint1);
		double p1y = Math.toRadians(latitudePoint1);
		double p2x = Math.toRadians(longitudePoint2);
		double p2y = Math.toRadians(latitudePoint2);
		double distance = greatCircleDistance(longitudePoint1, latitudePoint1, longitudePoint2, latitudePoint2, "rad");
		double course = Math.acos((Math.sin(p2y)-(Math.sin(p1y)*Math.cos(distance)))/(Math.cos(p1y)*Math.sin(distance)));
		
		/*
		 * 2005-10-13 Fixed this conditional statement so that it returned
		 * the correct bearing relative to 360 degrees
		 */
		if(Math.sin(p2x-p1x) < 0)
			course = (2*Math.PI) - course;
		
		if(units.equalsIgnoreCase("rad"))
			return course;
		if(units.equalsIgnoreCase("deg"))
			return Math.toDegrees(course);
		return -9999;
	}
	
	/**
	 * Calculates the coordinates a point that lies some percetance of the great circle distance between Point1 and Point2 and 
	 * along the great circle defined by said points 
	 * @param longitudePoint1	Longitude of the start point, Point1, expressed in in decimal degrees
	 * @param latitudePoint1	Latitude of Point1, expressed in in decimal degrees
	 * @param longitudePoint2	Longitude of the destination, Point2, expressed in in decimal degrees
	 * @param latitudePoint2	Latitude of Point2, expressed in in decimal degrees
	 * @param fraction			The percentace of the distance from Point1 to Point2 where the desired point is located, Point3.
	 * @return					An array containing the coordinates of the new location, [0] = longitude, [1] = latitude, expressed in decimal degrees.
	 */
	public double[] calculateIntermediateGreatCirclePoints(double longitudePoint1, double latitudePoint1, double longitudePoint2, double latitudePoint2, double fraction) {
		double distance = greatCircleDistance(longitudePoint1, latitudePoint1, longitudePoint2, latitudePoint2, "rad");
		double scalarA = Math.sin((1-fraction)*distance)/Math.sin(distance);
		double scalarB = Math.sin(fraction*distance)/Math.sin(distance);
		double x = (scalarA*Math.cos(Math.toRadians(latitudePoint1))*Math.cos(Math.toRadians(longitudePoint1)))+(scalarB*Math.cos(Math.toRadians(latitudePoint2))*Math.cos(Math.toRadians(longitudePoint2)));
		double y = (scalarA*Math.cos(Math.toRadians(latitudePoint1))*Math.sin(Math.toRadians(longitudePoint1)))+(scalarB*Math.cos(Math.toRadians(latitudePoint2))*Math.sin(Math.toRadians(longitudePoint2)));
		double z = (scalarA*Math.sin(Math.toRadians(latitudePoint1))) + (scalarB*Math.sin(Math.toRadians(latitudePoint2)));
		double[] position = new double[2];
		position[0] = Math.toDegrees(Math.atan2(y, x)); 
		position[1] = Math.toDegrees(Math.atan2(z, Math.sqrt((x*x)+(y*y))));
		return position;
	}
	
	/**
	 * Calculates the shortest distance, perpendicular distance, between a point (point3) and a great
	 * circle passing through Point1 and Point2
	 * @param longitudePoint1	Longitude of Point1, expressed in in decimal degrees (Great Circle Reference).
	 * @param latitudePoint1	Latitude of Point1, expressed in in decimal degrees.
	 * @param longitudePoint2	Longitude of Point2, expressed in in decimal degrees (Great Circle Reference).
	 * @param latitudePoint2	Latitude of Point2, expressed in in decimal degrees .
	 * @param longitudePoint3	Longitude of Point3, expressed in in decimal degrees (Object of interest). 
	 * @param latitudePoint3	Latitude of Point3, expressed in in decimal degrees .
	 * @param units				The units that the bearing sould be returned in.  Valid options are "rad" for radians and "deg" for decimal degrees, "nm" for nautical mile, "km" for kilometer and "m" for meters.
	 * @return					Great circle distance between Point3 and the great circle passing through Point1 to Point2.
	 */
	public double calculatePerpendicularDistance(double longitudePoint1, double latitudePoint1, double longitudePoint2, double latitudePoint2, double longitudePoint3, double latitudePoint3, String units) {
		/*
		 * Convert Point 1 (Great Circle Reference)
		 */
		double x1 = spheroidRadius*Math.cos(longitudeTo2pi(longitudePoint1))*Math.sin(getColatitude(latitudePoint1));
		double y1 = spheroidRadius*Math.sin(longitudeTo2pi(longitudePoint1))*Math.sin(getColatitude(latitudePoint1));
		double z1 = spheroidRadius*Math.cos(getColatitude(latitudePoint1));

		/*
		 * Convert Point 2 (Great Circle Reference)
		 */
		double x2 = spheroidRadius*Math.cos(longitudeTo2pi(longitudePoint2))*Math.sin(getColatitude(latitudePoint2));
		double y2 = spheroidRadius*Math.sin(longitudeTo2pi(longitudePoint2))*Math.sin(getColatitude(latitudePoint2));
		double z2 = spheroidRadius*Math.cos(getColatitude(latitudePoint2));	
		
		/*
		 * Convert Point 3 ( The sighting ) 
		 */
		double x3 = spheroidRadius*Math.cos(longitudeTo2pi(longitudePoint3))*Math.sin(getColatitude(latitudePoint3));
		double y3 = spheroidRadius*Math.sin(longitudeTo2pi(longitudePoint3))*Math.sin(getColatitude(latitudePoint3));
		double z3 = spheroidRadius*Math.cos(getColatitude(latitudePoint3));
		
		/*
		 * Cross normalize Point 1 and Point 2 = N
		 */
		double Nx = y1*z2 - z1*y2;
		double Ny = z1*x2 - x1*z2;
		double Nz = x1*y2 - y1*x2;
		double length = Math.sqrt(Nx*Nx + Ny*Ny + Nz*Nz);
		Nx = Nx / length;
		Ny = Ny / length;
		Nz = Nz / length;
		
		/*
		 * dot product N with Point 3
		 */
		double angleNOP3 = Nx*x3 + Ny*y3 + Nz*z3;
		
		/*
		 * Normalize N.P3
		 */
		length = Math.sqrt(x3*x3 + y3*y3 + z3*z3);
		angleNOP3 = angleNOP3/length;

		/*
		 * Calculate final distance
		 */
		double pDistance = Math.abs((Math.PI/2.0) - Math.acos(angleNOP3));
		
		if(units.equalsIgnoreCase("rad"))
			return pDistance;
		if(units.equalsIgnoreCase("deg"))
			return Math.toDegrees(pDistance); 
		if(units.equalsIgnoreCase("nm"))
			return Math.toDegrees(pDistance)*60;
		if(units.equalsIgnoreCase("km"))
			return pDistance*spheroidRadius/1000;
		if(units.equalsIgnoreCase("m"))
			return pDistance*spheroidRadius;
		
		return -9999;
	}

	/**
	 * Calculates the latitude of a new position which is at a specific distance and compass bearing from a given latitude.
	 * @param latitude			Latitude of the initial or starting point, expressed in decimal degrees
	 * @param distance			Distance from current position to new position, expressed in meters
	 * @param bearing			Compass bearing to new position, expressed in degrees
	 * @param units				The units of the new latitude.  Valid options are "rad" for radians and "deg" for decimal degrees.
	 * @return
	 */
	public double calculateLatitude(double latitude, double distance, double bearing, String units) {
		if(units.equalsIgnoreCase("rad"))
			return Math.asin(Math.sin(Math.toRadians(latitude))*Math.cos(distance/spheroidRadius)+Math.cos(Math.toRadians(latitude))*Math.sin(distance/spheroidRadius)*Math.cos(Math.toRadians(bearing)));
		else if(units.equalsIgnoreCase("deg"))
			return Math.toDegrees(Math.asin(Math.sin(Math.toRadians(latitude))*Math.cos(distance/spheroidRadius)+Math.cos(Math.toRadians(latitude))*Math.sin(distance/spheroidRadius)*Math.cos(Math.toRadians(bearing))));
		return -9999;
	}
	
	/**
	 * Calculates the longitude of a new position which is at a specific distance and compass bearing from any given position.
	 * @param longitude 		Longitude of the initial or starting point, express in decimal degrees.
	 * @param latitude			Latitude of the initial or starting point, express in decimal degrees.
	 * @param distance			Distance from current location to new position, expressed in meters.
	 * @param bearing			Compass bearing to new position, expressed in degrees.
	 * @param units				The units of the new longitude.  Valid options are "rad" for radians and "deg" for decimal degrees.
	 * @return
	 */
	public double calculateLongitude(double longitude, double latitude, double distance, double bearing, String units) {
		if(units.equalsIgnoreCase("rad"))
			return mod((Math.toRadians(longitude)+Math.asin((Math.sin(Math.toRadians(bearing))*Math.sin(distance/spheroidRadius))/Math.cos(calculateLatitude(latitude, distance, bearing, "rad")))+Math.PI),(2*Math.PI))-Math.PI;
		if(units.equalsIgnoreCase("deg"))
			return Math.toDegrees(mod((Math.toRadians(longitude)+Math.asin((Math.sin(Math.toRadians(bearing))*Math.sin(distance/spheroidRadius))/Math.cos(calculateLatitude(latitude, distance, bearing, "rad")))+Math.PI),(2*Math.PI))-Math.PI);
		return -9999;
	}
	
	/**
	 * Calculates the co-latitude for a given Latitude, expressed in decimal degrees.
	 * @param latitude			Latitude to be converted
	 * @return 					(90 - latitude) expressed in radians
	 */
	public double getColatitude(double latitude) {
		return Math.toRadians(90.0 - latitude);
	}
	
	/**
	 * Calculates the great circle distance between one geographic position (Point1) to a second 
	 * geographic position (Point2).  Longitude and Latitude are expected to be represented
	 * as decimal degrees.
	 * 
	 * @param longitudePoint1 	Longitude of the start or current position, Point1, expressed in decimal degrees.
	 * @param latitudePoint1 	Latitude of the start or current position, Point1, expressed in decimal degrees.
	 * @param longitudePoint2 	Longitude of the destination, Point2, expressed in decimal degrees.
	 * @param latitudePoint2 	Latitude of the destination, Point2, expressed in decimal degrees.
	 * @param units 			The units that the bearing sould be returned in.  Valid options are "rad" for radians and "deg" for decimal degrees, "nm" for nautical mile, "km" for kilometer and "m" for meters.
	 * @return					Great circle distance between Point1 to Point2
	 */
	public double greatCircleDistance(double longitudePoint1, double latitudePoint1, double longitudePoint2, double latitudePoint2, String units) {
		double distance = Math.acos((Math.sin(Math.toRadians(latitudePoint1))*Math.sin(Math.toRadians(latitudePoint2)))+(Math.cos(Math.toRadians(latitudePoint1))*Math.cos(Math.toRadians(latitudePoint2))*Math.cos(Math.toRadians(longitudePoint2)-Math.toRadians(longitudePoint1))));
		if(units.equalsIgnoreCase("rad"))
			return distance;
		else if(units.equalsIgnoreCase("deg"))
			return Math.toDegrees(distance);
		else if(units.equalsIgnoreCase("nm"))
			return Math.toDegrees(distance)*60;
		else if(units.equalsIgnoreCase("km"))
			return Math.toDegrees(distance)*((2*Math.PI*spheroidRadius)/360)/1000;
		else if(units.equalsIgnoreCase("m"))
			return Math.toDegrees(distance)*((2*Math.PI*spheroidRadius)/360);
		return -9999;
	}
	
	/**
	 * Translates a longitude, expressed in decimal degrees, into radians between the value of 0 and 2PI
	 * @param longitude			Longitude to be translated into 2PI
	 * @return 					Longitude expressed in radians, relative to 2PI
	 */
	public double longitudeTo2pi(double longitude) {
		if(longitude < 0)
			return Math.toRadians(360 + longitude);
		return Math.toRadians(longitude);
	}
	
	/**
	 * Initially it seemed that % was not returning currect values for doubles, That initial 
	 * impression seems(?) to have been incorrect
	 * @param 					number
	 * @param 					divisor
	 * @return 					The modulus of two numbers.  Analogous to number%divisor.
	 */
	private double mod(double number, double divisor) {
		return number%divisor;	
		//return number - (Math.floor(number/divisor))*divisor;
	}
	
	/**
	 * Sets the value of spheroidRadius
	 * @param value				New value representing the radius of the spherical representation of the Earth
	 */
	public void setSpheroidRadius(double value) {
		spheroidRadius = value;
	}
	
	/**
	 * A check that can be called by the GUI on an input string to verify that it only contains valid numeric 
	 * characters. Helps to prevent or eliminate NumberFormatExceptions.
	 * @param numberString 		The string representing a number to be tested for invalid characters
	 * @return 					True or false
	 */
	public static boolean validateNumericString(String numberString) {
		String validCharacters = new String("-.0123456789");
		
		for(int x = 0; x < numberString.length(); x++)
			if(validCharacters.indexOf(numberString.charAt(x)) == -1)
				return false;
		return true;
	}
}
