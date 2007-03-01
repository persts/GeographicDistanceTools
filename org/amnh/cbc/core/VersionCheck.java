/*
** File: VersionCheck.java
** Author: Peter J. Erts (ersts@amnh.org)
** Creation Date: 2007-02-05
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
**/
package org.amnh.cbc.core;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.stanford.ejalbert.BrowserLauncher;

/**
 * This is a very quickly written version control class. It could be made much much more elegant
 * @author Peter J. Ersts
 *
 */
public class VersionCheck extends JDialog implements ActionListener, Runnable {
	/* Eclipse generated serialVersionUID */
	private static final long serialVersionUID = 5277765529181660601L;

	/** \brief Parent Window */
	private JFrame parent;
	/** \brief Label used to display message in dialog */
	private JEditorPane message;
	/** \brief Version number passed from main application */
	private String version;
	/** \brief Url pointing to the version number of the current release */
	private String versionURL;
	/** \brief Url pointing to the location where the most current version can be downloaded */
	private String downloadURL;
	
	/** \brief Close button */
	private JButton closeButton;
	/** \brief Update button that will open a browser and load the download page*/
	private JButton updateButton;
	/** \brief The time to sleep, and insure that the main window has been loaded */
	private int initalDelay;

	/**
	 * Constructor
	 * @param parentWindow		Parent frame
	 * @param versionNumber		Current staticly set version number of the application
	 * @param versionAddress	Url pointing to the version number of the current release
	 * @param downloadAddress	Url pointing to the location where the most current version can be downloaded
	 * @param delay				The time to sleep, and insure that the main window has been loaded
	 */
	public VersionCheck(JFrame parentWindow, String versionNumber, String versionAddress, String downloadAddress, int delay) {
		parent = parentWindow;
		version = versionNumber;
		versionURL = versionAddress;
		downloadURL = downloadAddress;
		initalDelay = delay;
		
		setSize(400, 150);
		setTitle("Version Control");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
        JPanel buttonBar = new JPanel();
        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        updateButton.setEnabled(false);
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonBar.add(updateButton);
        buttonBar.add(closeButton);

        message = new JEditorPane();
		JScrollPane messageScrollPane = new JScrollPane(message);
		messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        getContentPane().add(messageScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonBar, BorderLayout.SOUTH);
	}
	
	/**
	 * Required method for runnable classes
	 */
	public void run() {
        try {
        	Thread.sleep(initalDelay);
        	checkVersion();
        }
        catch(InterruptedException e) {}
	}
	
	/**
	 * Required method to implement ActionListener
	 */
    public void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();
        if(obj == closeButton)
        	dispose();
        else if(obj == updateButton)
        	try {
        		BrowserLauncher.openURL(downloadURL);
    		}
			catch (IOException e) {}

    }
    
    /**
     * Get the current release version from the url and compare it to the statically set version within the program
     *
     */
    public void checkVersion() {
    	try {
    		URL versionInfo = new URL(versionURL);
    		URLConnection versionInfoConnection = versionInfo.openConnection();
    		BufferedReader inputStream = new BufferedReader(new InputStreamReader(versionInfoConnection.getInputStream()));
    		String remoteVersion = inputStream.readLine();
    		if(remoteVersion.equals(version))
    			message.setText("VERSION: " + version +"\nYou have the most current release.");
    		else {
    			String[] versionList = version.split("\\.");
    			String[] remoteVersionList = remoteVersion.split("\\.");
    			if(versionList.length < 3 || remoteVersionList.length < 3) {
    				message.setText("VERSION: " + version +"\nUnable to confirm if updates are availble,\nclick UPDATE to get the most recent release or visit:\n" + downloadURL);
    				updateButton.setEnabled(true);
    				display();
    			}
    			
    			if(Integer.parseInt(versionList[0]) < Integer.parseInt(remoteVersionList[0])) {
    				message.setText("VERSION: " + version +"\nA newer release is available,\nclick UPDATE to get the most recent release or visit:\n" + downloadURL);
    				updateButton.setEnabled(true);
    				display();
    			}
    			else if(Integer.parseInt(versionList[1]) < Integer.parseInt(remoteVersionList[1])) {
    				message.setText("VERSION: " + version +"\nA newer release is available,\nclick UPDATE to get the most recent release or visit:\n" + downloadURL);
    				updateButton.setEnabled(true);
    				display();
    			}
    			else if(Integer.parseInt(versionList[2]) < Integer.parseInt(remoteVersionList[2])) {
    				message.setText("VERSION: " + version +"\nA newer release is available,\nclick UPDATE to get the more recent release or visit:\n" + downloadURL);
    				updateButton.setEnabled(true);
    				display();
    			}
    			else if(versionList.length < remoteVersionList.length) {
    				message.setText("VERSION: " + version +"\nA newer release is available,\nclick UPDATE to get the more recent release or visit:\n" + downloadURL);
    				updateButton.setEnabled(true);
    				display();
    			}
    			else {
    				message.setText("VERSION: " + version +"\nYou have the most current release.");
    			}
    		}
    	}
    	catch(NumberFormatException e) {
			message.setText("VERSION: " + version +"\nUnable to confirm if updates are availble,\nclick UPDATE to get the more recent release or visit:\n" + downloadURL);
			updateButton.setEnabled(true);
    	}
    	catch(Exception e){
    		message.setText("VERSION: " + version +"\nUnable to connect to server to check version");
    	}
    }
    
    /**
     * Center the dialog in the middle of the parent window and set visible
     *
     */
    public void display() {
    	setLocation((int)parent.getLocationOnScreen().getX() + (parent.getWidth()/2) - (getWidth()/2), (int)parent.getLocationOnScreen().getY() + (parent.getHeight()/2) - (getHeight()/2));
    	setVisible(true);
    }
}
