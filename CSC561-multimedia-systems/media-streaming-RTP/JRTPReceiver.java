
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




public class RTPReceiver extends JFrame implements ReceiveStreamListener, SessionListener, ControllerListener
{
	
	 /* variable declaration */
	 private Container content;             // Window content panel
     private Panel panel1;                  // Media Player Control panel
     private JButton select_video_button;             // Video button
     private JButton select_audio_button;             // Audio button
     private Component comp;
     
     /* controllers */
     private Control_Select videoURL_Select;
     private Control_Select audioURL_Select;
     
     private RTPManager mgrs[] = null;
     private Player player[] = null;
     private ReceiveStream stream[] = null;
     private int player_count;
     
     private boolean video_playback;
     private boolean dataReceived;
     private String ip_addr;
     private int port[];
     private int ttl[];   /* not using it for now */
     
     /* Constructor */
	 public RTPReceiver() 
	 { 
		 super("RTP Receiver/Player"); // Set the Window Title
         
		 /* Initialize the Buttons and the Slider */
		 select_video_button = new JButton("Select_Video_url");
		 select_audio_button = new JButton("Select_Audio_url");
	    
		 /* Initialize the content panel */
         panel1 = new Panel();
         
         /* Initialize ActionListner objects */
         videoURL_Select = new Control_Select();
         audioURL_Select = new Control_Select();
         
         select_video_button.addActionListener(videoURL_Select);
         select_audio_button.addActionListener(audioURL_Select);
         
         /* initialize */
         player_count = -1;
		 player = new Player[2];
         
	 }
	 
	 /* Initialize RTP sessions */
	 protected boolean initialize() 
	 {
		try
		{
			
		 InetAddress ipAddr;
		 SessionAddress localAddr = new SessionAddress();
		 SessionAddress destAddr;
		 
		 int mngr_size = 1;
		 
		 if (video_playback == true){
			   mngr_size = 2;
		 }
		 
		 mgrs = new RTPManager[mngr_size];
	     stream = new ReceiveStream[mngr_size];
		 
		// Open the RTP sessions.
		for (int i = 0; i < mngr_size; i++) 
		{
			mgrs[i] = (RTPManager) RTPManager.newInstance();
			mgrs[i].addSessionListener(this);
			mgrs[i].addReceiveStreamListener(this);
			
			ipAddr = InetAddress.getByName(ip_addr); 
			
			if( ipAddr.isMulticastAddress())  // local and remote address pairs are identical
			{ 
			    localAddr= new SessionAddress( ipAddr, port[i], ttl[i]);    
			    destAddr = new SessionAddress( ipAddr, port[i], ttl[i]);
			} else 
			{
			    localAddr= new SessionAddress( InetAddress.getLocalHost(), port[i]);
	            destAddr = new SessionAddress( ipAddr, port[i]);
			}
				
			mgrs[i].initialize(localAddr);
			mgrs[i].addTarget(destAddr);
			
			BufferControl bc = (BufferControl) mgrs[i].getControl("javax.media.control.BufferControl");
			if (bc != null)
			    bc.setBufferLength(350);
			    	    	
		}
	   } catch (Exception e){
            System.err.println("Cannot create the RTP Session: " + e.getMessage());
            return false;
       }
		
	    return true;
		 
	 }
	 
	 /* Initialize GUI component */
	 public void GUI_init()
	 {
		 content = getContentPane();
		 content.add(panel1, BorderLayout.SOUTH);
		 panel1.setLayout(new GridLayout(1,2));
		 panel1.add(select_video_button);
		 panel1.add(select_audio_button);
		 
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
			 
	 public boolean isDone() 
	 {
		 /* check player size */
		 if (player_count == -1){
			 return true;
		 } else {
		   return false;
		 }
	 }
	 
	 /* Close the players and the session managers.*/
	 protected void close() 
	 {
		for (int i = 0; i <= player_count; i++)
		{
			try {
				player[i].close();
			} catch (Exception e) {}
		}

		player_count = -1;

		// close the RTP session.
		for (int i = 0; i < mgrs.length; i++) {
			if (mgrs[i] != null) 
			{
		          mgrs[i].removeTargets( "Closing session from RTPReceiver");
		          mgrs[i].dispose();
		          mgrs[i] = null;
			 }
		}	
	 }
	 
	 /* create the players */
	 public void createPlayer()
	 {
		 try 
		 { 
			 /* for audio playback */
			 String audio_url = "rtp://" + ip_addr.toString() + ":" + port[0] + "/audio/1";
			 MediaLocator mla = new MediaLocator(audio_url);
			 player[0] =  Manager.createPlayer(mla);
			 player[0].addControllerListener(this); 
			 player[0].realize();
			   
			 if (video_playback == true){
				   String video_url = "rtp://" + ip_addr.toString() + ":" + port[1] + "/video/1";   
				   MediaLocator mlv = new MediaLocator(video_url);
				   player[1] = Manager.createPlayer(mlv);
				   player[1].addControllerListener(this);
				   player[1].realize();
			   }   
  	   	} 
        catch (Exception e) {
            System.err.println("Got exception "+e);
        }
	 }
	    
	 /* SessionListener.*/
	 public synchronized void update(SessionEvent event)
	 {
		 if (event instanceof NewParticipantEvent) 
		 {
			    Participant p = ((NewParticipantEvent)event).getParticipant();
			    System.err.println("  - A new participant has just joined: " + p.getCNAME());
		}
	 }
	 
	 /* ReceiveStreamListener */
	 public synchronized void update( ReceiveStreamEvent event) 
	 {
			RTPManager mgr = (RTPManager)event.getSource();
			Participant participant = event.getParticipant();	// could be null.
			ReceiveStream new_stream = event.getReceiveStream();  // could be null.

			if (event instanceof RemotePayloadChangeEvent) {
		     
			    System.err.println("  - Received an RTP PayloadChangeEvent.");
			    System.exit(0);

			}
		    
			else if (event instanceof NewReceiveStreamEvent) 
			{
			   try 
			   {
				   new_stream = ((NewReceiveStreamEvent)event).getReceiveStream();
				   DataSource ds = new_stream.getDataSource();

				   // Find out the formats.
				   RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
				   if (ctl != null){
					   System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
				   } else {
					   System.err.println("  - Recevied new RTP stream with no control");
				   }
				   
				   if (participant == null) {
					   System.err.println("      The sender of this stream has yet to be identified.");
				   } else {
					   System.err.println("      The stream comes from: " + participant.getCNAME()); 
				   }

				   // create a player by passing datasource to the Media Manager
				   player_count ++;
				   player[player_count] = javax.media.Manager.createPlayer(ds);  
				   if (player[player_count] == null)
				   {
					   System.out.println("unable to create the player");
					   return;
				   } 

				   player[player_count].addControllerListener(this);  
				   player[player_count].realize(); 
				   stream[player_count] = (ReceiveStream) new_stream;


			   } catch (Exception e) {
				   System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
				   return;
			   }
		        
			}

			else if (event instanceof StreamMappedEvent) 
			{
				if (new_stream != null && new_stream.getDataSource() != null) {
					DataSource ds = new_stream.getDataSource();
					// Find out the formats.
					RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
					System.err.println("  - The previously unidentified stream ");
					if (ctl != null)
						System.err.println("      " + ctl.getFormat());
					System.err.println("      had now been identified as sent by: " + participant.getCNAME());
			     }
			}

			else if (event instanceof ByeEvent) 
			{
				System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
				/* find location of stream in array */
				int location = -1;
				for(int i=0; i <= player_count; i++){
					if (stream[player_count] == new_stream){
						location = i;
						break;
					}
				}
			    if (location > -1) {
			    	player[location].close();
			    	if (location == 0 && player_count > 0){  /* considering only 2 streams */
			    		player[0] = player[1];
			    		stream[0] = stream[1];
			    	}
			    	player_count --;
			    }
			}
	 }
	 
	 /* ControllerListener */
	 public synchronized void controllerUpdate(ControllerEvent ce)
	 {
		 Player p = (Player) ce.getSourceController();

		 if (p == null)
			return;
			
		 if (ce instanceof PrefetchCompleteEvent)
		 {
			 if ((comp = p.getVisualComponent()) != null)
		     {
		        	content.add(comp, BorderLayout.CENTER, 1);
		     } 
		     
		     validate();  
		     pack();   //resize window as per its components
			 p.start();
				
		 }
		   
		 if (ce instanceof RealizeCompleteEvent )
		 {
			   p.prefetch();
		 }
		   
		 if (ce instanceof EndOfMediaEvent)  
		 {
			   p.setMediaTime( new Time( 0 ) );
			   p.stop();
		 }
		   
		 if (ce instanceof ControllerErrorEvent) 
		 {
			 p.removeControllerListener(this);
			 p.close();
			 System.err.println("RTPPlayer internal error: " + ce);
		 }
	 }
	 
	 public class Control_Select implements ActionListener
     {
            public void actionPerformed(ActionEvent ae)
            {
                
                /* Open select box */
            	/* get IP address of server */
            	ip_addr = getIP();
            	if ( ip_addr == null )
            		 return;
            	
            	/* get port number */
            	port = new int [2];
            	port[0] = getPort(); 
            	if ( port[0] <= 0 )  //check for valid positive port number and input
           	 	{
           	 		if ( port[0] != -999 )
           	 		{
           	 			System.err.println( "Invalid port number!" );
           	 			return;
           	 		}
           	 	} 
           	 	
            	if ( ae.getSource() == select_video_button ) 
            	{
            		video_playback = true;
            		port[1] = port[0];
            		port[0] = port[0] + 2;
            	}
            	
            	if (ae.getSource() == select_audio_button )
            	{
            		video_playback = false;
            	}
          	   
            	/* initalize ttl */
            	ttl = new int[2];
            	ttl[0] = 1;
            	ttl[1] = 1;
            	
               /* create player */
          	   //createPlayer();
            	
            	if (!initialize()) {
            	    System.err.println("Failed to initialize the sessions.");
            	    System.exit(-1);
            	} 
          	    
            } 
			  
     }
	 
	 public static void main(String[] args) 
	 {
		 RTPReceiver RTPPlayer = new RTPReceiver();
		
		 RTPPlayer.GUI_init();
		 RTPPlayer.setSize(550,100);
		 RTPPlayer.setLocation(300,300);
		 RTPPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 RTPPlayer.setVisible(true);
	
	 }
	 
} // end of RTPReceiver