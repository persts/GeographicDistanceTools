package org.amnh.cbc.geographicDistanceMatrixGenerator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressDialog extends JFrame {

	private JProgressBar cvOverallProgressBar;
	private JProgressBar cvRowProgressBar;
	
	public ProgressDialog()
	{
		setTitle( "Running" );
		setSize( 300, 75 );
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout( new GridLayout( 2,1 ));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(screenSize.width/2 - this.WIDTH/2, screenSize.height/2 - this.HEIGHT/2);
	    
	    cvOverallProgressBar = new JProgressBar();
	    cvOverallProgressBar.setStringPainted( true );
	    cvRowProgressBar = new JProgressBar();
	    cvRowProgressBar.setStringPainted( true );
	    
	    getContentPane().add( cvRowProgressBar );
		getContentPane().add( cvOverallProgressBar );
		
	}
	
	public void setOverallMaximum( int theMaximum )
	{
		cvOverallProgressBar.setMaximum( theMaximum );
	}
	
	public void setOverallMinimum( int theMinimum )
	{
		cvOverallProgressBar.setMinimum( theMinimum );
	}
	
	public void setOverallValue( int theProgress )
	{
		cvOverallProgressBar.setValue( theProgress );
	}
	
	public void setRowMaximum( int theMaximum )
	{
		cvRowProgressBar.setMaximum( theMaximum );
	}
	
	public void setRowMinimum( int theMinimum )
	{
		cvRowProgressBar.setMinimum( theMinimum );
	}
	
	public void setRowValue( int theProgress )
	{
		cvRowProgressBar.setValue( theProgress );
	}

}
