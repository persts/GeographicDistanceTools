package org.amnh.cbc.core;
/*
** File: SplashScreen.java
** Author: Peter J. Erts (ersts@amnh.org)
** Creation Date: 2004-11-12
** Revision Date: 2004-11-12
**
** Version: 1.0
**
** Copyright (c) 2004, American Museum of Natural History. All rights reserved.
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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class SplashScreen extends JWindow implements Runnable {
	private Frame application;
	
	public void run() {
        try {
        	setVisible(true);
        	Thread.sleep(5000);
        	if(!application.isVisible()) {
        		application.setVisible(true);
        		setVisible(false);
        	}
            dispose();
        }
        catch(InterruptedException e) {}
	}
	
    public SplashScreen(String splashImageFilename, Frame mainFrame) {
    	application = mainFrame;
    	    	
        JLabel displayImage = new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource(splashImageFilename)));
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension imageSize = displayImage.getPreferredSize();
        setLocation(screenSize.width/2 - (imageSize.width/2), screenSize.height/2 - (imageSize.height/2));
        setSize(imageSize);
        getContentPane().add(displayImage, BorderLayout.CENTER);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setVisible(false);
            	application.setVisible(true);
            }
        });
    }
}
