/*
** File: About.java
** Author: Peter J. Ersts (ersts@amnh.org)
** Creation Date: 2007-02-06
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
** This program was prepared by the the above author(s) 
** under award No. NA04AR4700191 from the National Oceanic and Atmospheric
** Administration, U.S. Department of Commerce.  The statements, findings,
** conclusions, and recommendations are those of the author(s) and do not
** necessarily reflect the views of the National Oceanic and Atmospheric
** Administration or the Department of Commerce.
**
**/
package org.amnh.cbc.perpendicularDistanceCalculator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * This class simply displays a little dialog with information about the application.
 * @author Peter J. Ersts
 *
 */
public class About extends JDialog implements HyperlinkListener {

	/**
	 * Constructor
	 * @param parent	Parent frame
	 * @param modal		Boolean to indicate if this dialog should be modal
	 */
	About(JFrame parent, String version) {
		String messageText = "<HTML><BODY>"+
	 						"Version "+ version +" of the Perpendicular Distance Calculator "+
 							"written by Peter J. Ersts, Project Specialist with the <a HREF=\"http://cbc.amnh.org\">Center for "+
 							"Biodiversity and Conservation</a> at the <a HREF=\"http://amnh.org\">American Museum of Natural History</a>. "+
 							"Eric Albert, Tim Collins, Ned Horning, Kevin Koy, Matt Leslie, and Marco Polin "+
 							"should be acknowledged for their contributions which have taken the form of code, constructive criticism, "+
 							"beta-testing and moral support. This application implements Eric Albert's BrowserLauncher class, which is know not to work on many linux systems.<BR><BR>"+
 							"Questions, comments can be posted on: <BR>"+
 							 "<a HREF=\"http://biodiversityinformatics.amnh.org/open_source/gdmg/comments.php\">http://biodiversityinformatics.amnh.org/gdmg/comments.php</a><BR><BR>"+
 							 "Bug reports and enhancements can be posted on: <a HREF=\"http://trac.biodiversityinformatics.amnh.org/projects/javageodistancetools\">http://trac.biodiversityinformatics.amnh.org/projects/javageodistancetools</a><BR><BR>"+
 							"This work has been partially supported by NASA under award No. NAG5-8543. "+
 							"Additionally, this program was prepared by the the above author(s) under "+
 							"award No. NA04AR4700191 from the National Oceanic and Atmospheric "+
 							"Administration, U.S. Department of Commerce.  The statements, findings, "+
 							"conclusions, and recommendations are those of the author(s) and do not "+
 							"necessarily reflect the views of the National Oceanic and Atmospheric "+
 							"Administration or the Department of Commerce.</BODY></HTML>";
		setSize(435,600);
		setTitle("About");
		
		getContentPane().setBackground(Color.WHITE);
		setBackground(new Color(255,255,255));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocation((int)parent.getLocationOnScreen().getX() + (parent.getWidth()/2) - (getWidth()/2), (int)parent.getLocationOnScreen().getY() + (parent.getHeight()/2) - (getHeight()/2));
		
		JEditorPane text = new JEditorPane();
		text.addHyperlinkListener(this);
		text.setContentType("text/html");
		text.setText(messageText);
		text.setCaretPosition(0);
		text.setEditable(false);
		JScrollPane textScrollPane = new JScrollPane(text);
		textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
		JPanel splash = new JPanel();
		splash.setBackground(new Color(255,255,255));
		splash.setBorder(BorderFactory.createEmptyBorder());
		splash.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("resources/SplashScreenGraphic-PDC.jpg"))));
		getContentPane().add(splash, BorderLayout.NORTH);
		
		JPanel logos = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
		logos.setBackground(new Color(255, 255, 255));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("resources/cbc-blue-sm.jpg"))));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("resources/nasa-sm.jpg"))));
		logos.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("resources/noaa-sm.jpg"))));
		
		getContentPane().add(logos, BorderLayout.SOUTH);
		getContentPane().add(textScrollPane, BorderLayout.CENTER);
		setVisible(true);
	}
	
	/**
	 * Required method to implement HyperlinkListener
	 */
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				BrowserLauncher.openURL(evt.getURL().toString());
			}
			catch (IOException e) {}
		}
	}
}
