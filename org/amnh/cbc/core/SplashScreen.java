/*
** File: SplashScreen.java
** Author: Peter J. Erts (ersts@amnh.org)
** Creation Date: 2004-11-12
** Revision Date: 2007-05-02
**
** Copyright (c) 2004 - 2007, American Museum of Natural History. All rights reserved.
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
package org.amnh.cbc.core;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SplashScreen extends JWindow implements Runnable {
	
	/* Eclipse generated serialVersionUID */
	static final long serialVersionUID = 6644698351859596439L;
	
	/* \brief The main frame representing the application */ 
	private Frame application;
	
	/* \brief Length of time (milliseconds) to display the splash screen */
	private int timeout;
	
	/**
	 * Runable method that will actually display the splash screen image and 
	 */
	public void run() {
        try {
        	setVisible(true);
        	Thread.sleep(timeout);
        	if(!application.isVisible()) {
        		application.setVisible(true);
        		setVisible(false);
        	}
            dispose();
        }
        catch(InterruptedException e) {}
	}
	
	/**
	 * Constructor
	 * @param mainFrame				Main frame representing the application window
	 * @param splashImageFilename	Filename, relative to the prject root, of the image to display
	 * @param displayTime			The length of time to display the splash screen, in milliseconds 
	 */
    public SplashScreen(Frame mainFrame, String splashImageFilename, int displayTime) {
    	application = mainFrame;
    	timeout = displayTime;
    	    	
        JLabel displayImage = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource(splashImageFilename)));
        
        /* center splash screen within the display */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension imageSize = displayImage.getPreferredSize();
        setLocation(screenSize.width/2 - (imageSize.width/2), screenSize.height/2 - (imageSize.height/2));
        setSize(imageSize);
        getContentPane().add(displayImage, BorderLayout.CENTER);
        
        /* Hide splash screen and display main application if user clicks on the splash image */
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setVisible(false);
            	application.setVisible(true);
            }
        });
    }
}
