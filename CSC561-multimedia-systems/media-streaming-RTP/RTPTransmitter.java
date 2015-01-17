/* processor.stop();  //processor needs to be stopped as well before closing the sendStream
sendStream.close(); */


import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.media.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.*;
import javax.media.protocol.*;
import javax.media.Format.*;
import javax.media.control.*;
import javax.media.format.UnsupportedFormatException;


public class RTPTransmitter extends JFrame
{
	
	 /* variable declaration */
	 private Container content;             // Window content panel
     private Panel panel1;                  // Media Player Control panel
     private JButton select_file;             
     private JButton select_ipaddr;
     private JButton Start;
     private JButton Pause;
     private JButton Stop;
     private Component comp;
     
     /* controllers */
     private Control_Select file_Select;
     private Control_Select ipaddr_Select;
     private Control_Select Pause_control;
     private Control_Select Stop_control;
     private Control_Select Start_control;
     
     private File mediaFile;
     private boolean isPlaying;
     private String ip_addr;
     private int port;
     private int ttl;   /* not using it for now */
     
     RTPServer mainRTPServer;
     
     /* Constructor */
	 public RTPTransmitter() 
	 { 
		 super("RTP Transmitter/Server"); // Set the Window Title
         
		 /* Initialize the Buttons and the Slider */
		 select_file = new JButton("Select File");
		 select_ipaddr = new JButton("Select IP Addr");
		 Pause = new JButton("Pause");
		 Stop = new JButton("Stop");
		 Start = new JButton("Start");
	    
		 /* Initialize the content panel */
         panel1 = new Panel();
         
         /* Initialize ActionListner objects */
         file_Select = new Control_Select();
         ipaddr_Select = new Control_Select();
         Pause_control = new Control_Select();
         Stop_control = new Control_Select();
         Start_control = new Control_Select();
         
         select_file.addActionListener(file_Select);
         select_ipaddr.addActionListener(ipaddr_Select);
         Pause.addActionListener(Pause_control);
         Stop.addActionListener(Stop_control);
         Start.addActionListener(Start_control);
         
         /* initialize */
         isPlaying = false;
         
	 }
	 
	 /* Initialize GUI component */
	 public void GUI_init()
	 {
		 content = getContentPane();
		 content.add(panel1, BorderLayout.SOUTH);
		 panel1.setLayout(new GridLayout(1,5));
		 panel1.add(select_file);
		 panel1.add(select_ipaddr);
		 panel1.add(Start);
		 panel1.add(Pause);
		 panel1.add(Stop);
		 
	 }
	 
	 /* Select media file */
	 public File FileOpen()
	 {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.showOpenDialog(this);
			return chooser.getSelectedFile();
	 }
	 
	 // Get IP address from user
	 public String getIP()
	 {
		String input = JOptionPane.showInputDialog(this, "Enter IP Address: " );
		// if user presses OK with no input
		if ( input != null && input.length() == 0 )
		{
			System.err.println( "No input!" );
			return null;
		}
		
		return input;
	 }
			
	// Get Port number from user
	public int getPort()
	{
		String input = JOptionPane.showInputDialog(this, "Enter Port Number: " );
				
		// return flag value if user clicks OK with no input
		if ( input != null && input.length() == 0 ) 
		{
			System.err.println( "No input!" );
			return -999;
		}
				
		// return flag value if user clicked CANCEL
		if ( input == null )
			return -999;
				
		// else return input
		return Integer.parseInt( input );
				
	}
	 
	 public class Control_Select implements ActionListener
     {
            public void actionPerformed(ActionEvent ae)
            {
                
            	if ( ae.getSource() == select_file ) 
             	{
            		if (isPlaying == false)
            		{
            			mediaFile = FileOpen();
            		}
            		
             	}
            	 
                if ( ae.getSource() == select_ipaddr ) 
            	{
                	if (isPlaying == false)
                	{
                		/* get IP address of server */
                		ip_addr = getIP();
                		if ( ip_addr == null )
                			return;
                	
                		/* get port number */
                		port = getPort(); 
                		if ( port <= 0 )  //check for valid positive port number and input
               	 		{
               	 			if ( port != -999 )
               	 			{
               	 				System.err.println( "Invalid port number!" );
               	 				return;
               	 			}
               	 		}
                	
                		/* initalize ttl */
                		ttl = 1;
                		
                	}
            	}
            	
                /* Pause transmission button event */
                if ( ae.getSource() == Pause ) 
            	{
                	if (mainRTPServer != null && isPlaying == true)
                	{
                		if (Pause.getText().compareTo("Pause") == 0)
                		{
                			mainRTPServer.pauseTransmission();
                			Pause.setText("Continue");
                		} else {
                			Pause.setText("Pause");
                			mainRTPServer.continueTransmission();
                		}
                	}
            	}
                
                /* stop transmission button event */
                if ( ae.getSource() == Stop ) 
            	{
                	if (mainRTPServer != null && isPlaying == true)
                	{
                		mainRTPServer.stopTransmission();
                		isPlaying = false;
                	}
            	}
                
                /* start transmission button event */
                if ( ae.getSource() == Start ) 
            	{ 
                	if ( mainRTPServer == null && mediaFile != null && ip_addr!= null && isPlaying == false)
                	{ 
                		mainRTPServer = new RTPServer(mediaFile.toURI().toString(), ip_addr, port);
                		mainRTPServer.beginSession();
                		isPlaying = true;
                	}
            	}
          	    
            } 
			  
     }
	 	 
	 public static void main(String[] args) 
	 {
		 RTPTransmitter RTPSender = new RTPTransmitter();
		
		 RTPSender.GUI_init();
		 RTPSender.setSize(550,100);
		 RTPSender.setLocation(300,300);
		 RTPSender.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 RTPSender.setVisible(true);
	
	 }
	 
	 /* RTP server */
	 public class RTPServer 
	 {

		 // IP address
		 private String ipAddress, fileName;
		 private int port;

		 // processor & datasources, RTPmanager
		 private Processor processor;
		 private DataSource outSource;
		 private TrackControl tracks[];
		 private RTPManager rtpManager[];
		 private Time mediaPlayed;

		 // constructor 
		 public RTPServer( String locator, String ip, int portNumber )
		 {
			 fileName = locator; 
			 port = portNumber;
			 ipAddress = ip;
		 }

		 // initialize 
		 public boolean beginSession()
		 {
			 // get MediaLocator from specific location
			 MediaLocator mediaLocator = new MediaLocator( fileName );

			 // create processor from MediaLocator
			 try 
			 {
				 processor = Manager.createProcessor( mediaLocator );
				 processor.addControllerListener(new ProcessorEventHandler() );
			
				 System.out.println( "Processor configuring..." );
				 processor.configure();
			 } 
			 catch ( IOException ioException ) 
			 {
				 ioException.printStackTrace();
				 return false;
			 }
			
			 // exception thrown when no processor could be found for specific data source
			 catch ( NoProcessorException noProcessorException ) 
			 {
				 noProcessorException.printStackTrace();
				 return false;
			 }
			
			return true;
			
		 } // end method beginSession
			
		// ControllerListener handler for processor
		private class ProcessorEventHandler extends ControllerAdapter 
		{
			 // set output format and realize configured processor
			 public void configureComplete(ConfigureCompleteEvent configureCompleteEvent )
			 {
				 setOutputFormat();
				 processor.realize();
			 }
			
			 // start sending when processor is realized
			 public void realizeComplete(RealizeCompleteEvent realizeCompleteEvent )
			 {
				 System.out.println("\nInitialization successful for " + fileName );
			 
				 if ( transmitMedia() == true )
					 System.out.println( "\nTransmission setup OK" );
				 else
					 System.out.println( "\nTransmission failed." );
			 }
			 
			 // stop RTP session when there is no media to send
			 public void endOfMedia( EndOfMediaEvent mediaEndEvent )
			 {
				 stopTransmission();
				 System.out.println ( "Transmission completed." );
			 }
			 
		} // end inner class ProcessorEventHandler
			
		// set output format of all tracks in media
		public void setOutputFormat()
		{
			 // set output content type to RTP capable format
			 processor.setContentDescriptor(new ContentDescriptor( ContentDescriptor.RAW_RTP ) );
			 
			 tracks = processor.getTrackControls();
			 Format rtpFormats[];
			 
			  // set each track to first supported RTP format found in that track
			  for ( int i = 0; i < tracks.length; i++ ) 
			  {
				  if ( tracks[ i ].isEnabled() ) 
				  {
					  rtpFormats = tracks[ i ].getSupportedFormats();
			 
					  // if supported formats of track exist,
					  // display all supported RTP formats and set
					  // track format to be first supported format
					  if ( rtpFormats.length > 0 ) 
					  {
						  for ( int j = 0; j < rtpFormats.length; j++ )
							  System.out.println( rtpFormats[ j ] );
			
						  tracks[ i ].setFormat( rtpFormats[ 0 ] );
			
						  System.out.println ( "Track format set to " + tracks[ i ].getFormat() );
					  }
					  else
						  System.err.println ("No supported RTP formats for track!" );
			
				  } // end if
			 } // end for loop
			
		} // end method setOutputFormat
			  
		// send media with boolean success value
		public boolean transmitMedia()
		{
			  outSource = processor.getDataOutput();
			 
			  if ( outSource == null ) 
			  {
				  System.out.println ( "No data source from media!" );
				  return false;
			  }
			 
			  // rtp stream managers for each track
			  rtpManager = new RTPManager[ tracks.length ];
			  SessionAddress localAddress, remoteAddress;
			  SendStream sendStream;
			  InetAddress ip;
			 
			  // initialize transmission addresses and send out media
			  try 
			  {
			 
				  // transmit every track in media
				  for ( int i = 0; i < tracks.length; i++ ) 
				  {
					  rtpManager[ i ] = RTPManager.newInstance();
			 
					  // add 2 to specify next control port number
					  port += ( 2 * i );
					  ip = InetAddress.getByName( ipAddress );
			  
					  // encapsulate pair of IP addresses for control and data with 2 ports into local session address
					  localAddress = new SessionAddress(ip.getLocalHost(), port );
					  remoteAddress = new SessionAddress( ip, port );
			  
					  // initialize the session and add destination
					  rtpManager[ i ].initialize( localAddress );
					  rtpManager[ i ].addTarget( remoteAddress );
					  System.out.println( "\nStarted RTP session: " + ipAddress + " " + port);
			  
					  // create send stream in RTP session
					  sendStream = rtpManager[ i ].createSendStream( outSource, i );
					  sendStream.start();
					  System.out.println( "Transmitting Track #" + ( i + 1 ) + " ... " );
			  
				  } // end for loop
			  
			      // start media feed
				  processor.start();
			  
			} // end try
			  
			// unknown local or unresolvable remote address
			catch ( InvalidSessionAddressException addressError ) 
			{
			   addressError.printStackTrace();
			   return false;
			}
			  
			// DataSource connection error
			catch ( IOException ioException ) 
			{
				ioException.printStackTrace();
				return false;
			}
			  
			 // format not set or invalid format set on stream source
			catch ( UnsupportedFormatException formatException ) 
			{
				formatException.printStackTrace();
				return false;
			}
			  
			// transmission initialized successfully
			return true;
			   
		} // end method transmitMedia
			  
		public void pauseTransmission()
		{
			if (processor != null){
				processor.stop();
				mediaPlayed = processor.getMediaTime();
			}
		}
		
		public void continueTransmission() 
		{
			if (processor != null && mediaPlayed != null)
			{
				processor.setMediaTime(mediaPlayed);
				mediaPlayed = null;
				processor.start();
			}
		}
		// stop transmission and close resources
		public void stopTransmission()
		{
			if ( processor != null ) 
			{
				// stop processor
				processor.stop();
				processor.close();
				
				if ( rtpManager != null )
				{ 
					// close destination targets and dispose RTP managers
					for ( int i = 0; i < rtpManager.length; i++ ) 
					{
						rtpManager[ i ].removeTargets("Session stopped." );
						rtpManager[ i ].dispose();
					}
				  
				} // end if
				 
				System.out.println ( "Transmission stopped." );
			}  
		} // end method stopTransmission
				   
	} // end class RTPServer
		
} // end of RTPReceiver