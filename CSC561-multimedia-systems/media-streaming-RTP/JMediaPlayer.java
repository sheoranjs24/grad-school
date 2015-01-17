/*
*                   CSC 461/561 Multimedia System Programming Assignment 1 Sample Java Code
*                   To implement programming assignment 1, please enter the code following
*                   the comment such as "please enter the code here.".
*
*                   Again, to be remained here, please check your installations for JDK,
*                   JRE and JMF. From now on, we are going to implement java applications with
*                   features imported from JMF packages. Incorrect installations of JDK or JRE
*                   could cause compiler to produce nothing but error messages.
*/


/*imported packages */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.media.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;

/* SamplePlayer inherite the window frame feature */
public class JMediaPlayer extends JFrame  implements ControllerListener
{
       private Container content;        // Window content panel
       private Panel panel1;             // Media Player Control panel
       private Panel panel2;             // Slider panel
	   private Panel panel3;  //added
       private JSlider Slide_bar;        // Slider object
       private JButton Open;             // Open button
       private JButton Start;            // Play button
       private JButton Rec;              // Record button
       private JButton Fast_Forward;     // Fast Forward button
       private JButton Fast_Backward;    // Fast Backward button
       private JButton Pause;            // Pause button
       private JButton Stop;             // Stop button
       private JLabel MediaTimer;
       private Component comp;
       private Component comp1;
       
       private File mediaFile;                // File to be played
       private URL url;
       /* JS : Added code here : ActionListener class objects */
       private Control_Open File_Open;
       private Control_Start File_Start;
       private Control_Fastforward File_Fastforward;
       private Control_Backward File_Backward;
       private Control_Pause File_Pause;
       private Control_Stop File_Stop;
       private Control_Timer File_Timer;
       private Control_Slider File_Slider;   // need timer  class too
       private javax.swing.Timer countupTimer;
       
       private Player player;
       private Time pausedAt;     // in seconds or nanoseconds
       private Time mediaDuration;
       private Time mediaTime;
       private Integer sliderValue;
       private boolean isPaused;
       private boolean isStopped;
       private boolean isStarted;
       private boolean isSlided;
       private boolean callFromSlider;
       private boolean callFromTimer;
       private boolean waslastVisual;
       private boolean isFileOpened;
       private float rate_count;
       
       /* JS End */

       public JMediaPlayer()
       {
              super("Sample Audio Recorder/Player"); // Set the Window Title
              /* Initialize the Buttons and the Slider */

              Open = new JButton("Open");
              Start = new JButton("Play");
              Rec = new JButton("Rec");  // Not implemented yes, not even class
              Fast_Forward = new JButton(">>");
              Fast_Backward = new JButton("<<");
              Pause = new JButton("||");
              Stop = new JButton("$ ");
              MediaTimer = new JLabel("  0:0 ");
              Slide_bar = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);

              // Initialize the content panel
              panel1 = new Panel();
              panel2 = new Panel();
			  panel3 = new Panel(); //added
			  
              // Added code here
			  // Initialize ActionListner objects
			  File_Open = new Control_Open();
			  File_Start = new Control_Start(); 
			  File_Fastforward = new Control_Fastforward(); 
			  File_Backward = new Control_Backward(); 
			  File_Pause = new Control_Pause();
			  File_Stop = new Control_Stop(); 
			  File_Slider = new Control_Slider(); 
			  File_Timer = new Control_Timer();
			  
			  Open.addActionListener(File_Open);
			  Start.addActionListener(File_Start);
			  Fast_Forward.addActionListener(File_Fastforward);
			  Fast_Backward.addActionListener(File_Backward);
			  Pause.addActionListener(File_Pause);
			  Stop.addActionListener(File_Stop);
			  Slide_bar.addChangeListener(File_Slider);
			  
			  isPaused = false;
		      isStopped = true;
		      isStarted = false;
		      isSlided = false;
		      callFromSlider = false;
		      callFromTimer = false;
		      waslastVisual = false;
		      isFileOpened = false;
		      rate_count = 1;
		      sliderValue = 0;
		      
		      countupTimer = new Timer(1000, File_Timer);
		      player = null;
		       
			  /* JS End */
			  
       }

       // Initialize the GUI layout in the window frame
       public void Player_GUI_Init()
       {
              content = getContentPane();
              // Add the player control panel.
              //content.add(panel1, BorderLayout.CENTER);
              // Add the player slider panel.
             // content.add(panel2, BorderLayout.SOUTH);
              // Add player controls to the control panel.
              panel1.setLayout(new GridLayout(1,7));
              panel1.add(Open);
              panel1.add(Start);
              panel1.add(Rec);
              panel1.add(Fast_Backward);
              panel1.add(Fast_Forward);
              panel1.add(Pause);
              panel1.add(Stop);
              panel1.add(MediaTimer);
              // Add player slider to slider panel.
              panel2.setLayout(new GridLayout(1,1));
              panel2.add(Slide_bar);
              panel3.setLayout(new GridLayout(2,1));
              panel3.add(panel1);
              panel3.add(panel2);
              content.add(panel3, BorderLayout.SOUTH);
       }

       public void Control_Handler()
       {
              /* Initialize the Event handling controls  */
    	   
              /* Please enter code here. */
			  

       }
       
       // Added code here
	   public File FileOpen()
	   {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.showOpenDialog(this);
			return chooser.getSelectedFile();
	   }
	   
	   public void createPlayer(){
		   try {
				  
    		   player = Manager.createPlayer(mediaFile.toURI().toURL());
               player.addControllerListener(this);
               
    	   } 
           catch (Exception e) {
              System.err.println("Got exception "+e);
           }
	   }
	   
	   public void init()
	   {
    	   isPaused = false;
    	       isStopped = true;
    	       isStarted = false;
    	       isSlided = false;
    	       pausedAt = new Time(0.0) ;
    	       mediaDuration = new Time(0.0) ;
    	       mediaTime = new Time(0.0);
    	       sliderValue = 0;
    	       rate_count = 1;
        	   Slide_bar.setValue(0);
    	       MediaTimer.setText("   0:0  ");
	   }
	   
	   public void updateTimer(int count) 
	   {
		   Integer min=0, sec=0, sV=0, mT=0, mD=0;
		   
		   mT = (int) player.getMediaTime().getSeconds();
		   mD = (int) player.getDuration().getSeconds();
		   sV = Slide_bar.getValue(); 
		   
		   if ( (mD > 0) && (mT <= mD) ) 
		   {
			   if (count == 1) 
			   {
				   //System.out.println("Timer");
				   sliderValue = (int) ((mT * 100) / mD);
				   Slide_bar.setValue(sliderValue);     
			   }
			   else if (count == 2)
			   {
				   //System.out.println("Slider");
				   mT = (int) ((sV * mD) / 100); 
				   mediaTime = new Time((double) mT);
				   pausedAt = mediaTime;  
				   if (player.getState() > 200){  
				     player.setMediaTime(mediaTime);
				   }
			   }
			   
			   min = mT / 60;
	      	   sec = mT % 60;
	      	   if (player.getState() > 200){
	      	      MediaTimer.setText("   " + min.toString() + ":" + sec.toString());
	      	 }
		   }
		   else {
	          MediaTimer.setText("   x:x  ");
	          countupTimer.stop();
	          sliderValue = 100;
	          Slide_bar.setValue(100);  
	       }
	   }
	   
	   public synchronized void controllerUpdate(ControllerEvent event) 
	   {
		   if (event instanceof RealizeCompleteEvent) {
		       
		        if ((comp = player.getVisualComponent()) != null)
		        {
		        	content.add(comp, BorderLayout.CENTER, 1);
		        	waslastVisual = true;
		        /*	comp1 = content.findComponentAt(300,300); */
		        } 
		     /*  else if (waslastVisual && comp1 != null)
		        {
		        	content.remove(comp1);
		        	waslastVisual = false;
		        	comp1 = null;
		        } */
		        	  
		        validate();  
				pack();   //resize window as per its components
				
		   }
		   
	   } 
	   /* JS End */
		  
       public class Control_Open implements ActionListener
       {
              public void actionPerformed(ActionEvent ae1)
              {
                   /* To open pre-stored file,
                   *  Please Enter code here.
                   */
            	  if (player!=null && player.getState() > 300){
            		   isStarted = false;
               	   	   isStopped = true;
               	       isPaused = false;
					   player.deallocate();  
				   }
				   mediaFile = FileOpen();   /* JS Added */
				   isFileOpened = true;
				   sliderValue = 0;
            	   Slide_bar.setValue(0);
            	   
            	   /* create player */
            	   createPlayer();
            	    
              }
			  
       }
       
       public class Control_Timer implements ActionListener
       {
    	   public void actionPerformed(ActionEvent ae2)
           {
    		   
    		   updateTimer(1);
    		
           }
       }
       
       public class Control_Slider implements javax.swing.event.ChangeListener
       {
              /* Slider will be used to update the media playing progress
              ** Please enter the slider bar code here.
              */
		  @Override
		  public void stateChanged(ChangeEvent arg0) 
		  {
			  
			  if (player!= null && (Slide_bar.getValue() != sliderValue))
			  {
				  
				  if (player.getState() == 600)
				  { 
					  player.stop();  
					  isSlided = true;
					  isStarted = false;
				  }
				  isPaused = true;
				  isStopped = false;
				  updateTimer(2); 
			      if (isSlided){
			        player.start();
			        isPaused = false;
			        isStopped = false;
			        isSlided = false;
			      }
			  }
		  }
       }
       
       
       public class Control_Fastforward implements ActionListener
       {
              /* Media player will jump forward by clicking fast forward button
              ** Please enter the fast forward code here.
              */
               public void actionPerformed(ActionEvent ae3)
              {
            	  if (player != null){
            		  if (rate_count < 3){
            			  //float set_rate;
            			  if (rate_count < 1) {
            				  rate_count = 1;
            			  }
            			  else {
            				  rate_count += 0.5;
            			  }
            			  /* if (isStarted){
            				 player.stop();
            			  } */
            			  rate_count = player.setRate(rate_count);
            			  System.out.println("Set rate: "+rate_count);
            			  //player.start();
            		  }
            	  } 
              }
       }
       public class Control_Backward implements ActionListener
       {
              /* Media player will jump backward by clicking backward button
              ** Please enter the backward code here.
              */
               public void actionPerformed(ActionEvent ae4)
              {
            	   if (player != null){
             		  if (rate_count > 0.2){
             			  //float set_rate;
             			  if (rate_count > 1) { rate_count = 1; }
             			  else if (rate_count > 0.3) { rate_count -= 0.2; }
             			  else { rate_count -= 0.1; }
             			  
             			  rate_count = player.setRate(rate_count);
             			  System.out.println("Set rate: "+rate_count);
             			  //player.start();
             		  }
             	  } 
              }
       }
       public class Control_Start implements ActionListener
       {
              /* Audio player starts playing content by clicking Start button
              ** Please enter the Start code here.
              */
               public void actionPerformed(ActionEvent ae5)
              {
            	   /* JS Added code here */
            	   if (player != null){
            	    if (player.getState() < 500) {
            		   init();  
            		   player.start();  
            		   
            		   isStarted = true;
            		   isStopped = false;
            		   isPaused = false;
            		   
            		   
            	    } else {
            		   player.setMediaTime(pausedAt);   
            		   player.start();
            		   
            		   isStarted = true;
            		   isStopped = false;
            		   isPaused = false;
            	    }
            	    countupTimer.start();
            	   /* JS End */
                }
              }
       }
       public class Control_Stop implements ActionListener
       {
              /* Audio player stops play content by clicking Stop button
              ** Please enter the Stop code here.
              */
               public void actionPerformed(ActionEvent ae6)
              {
            	   /* JS Added code here */
            	   if (player != null){
            		   player.stop();  
            	  
            		   isStarted = false;
            		   isStopped = true;
            		   isPaused = false;
            	   
            		   countupTimer.stop();
            		   sliderValue = 100;
            		   Slide_bar.setValue(100);
            		   player.deallocate();  
            	   }
                   /* JS End */
              }

       }
       public class Control_Pause implements ActionListener
       {
              /* Please enter the pause code here. */
               public void actionPerformed(ActionEvent ae7)
              {
            	   /* JS Added code here */
            	   if (player != null){
            		   player.stop();
            		   countupTimer.stop();
            	   
            		   mediaDuration =  player.getDuration();
            		   pausedAt = player.getMediaTime();
            	   
            		   isStarted = false;
            		   isPaused = true;
            		   isStopped = false;
            	   }
                   /* JS End */
              
              }
       }

       public static void main(String argv[])
       {
              /* Display the window frame */
              /* You are very welcomed to design your own
              *  Player GUI interface.
              */
              JMediaPlayer myplayer = new JMediaPlayer();
              myplayer.Player_GUI_Init();
              myplayer.Control_Handler();
              myplayer.setSize(550,100);
		      myplayer.setLocation(300,300);
		      myplayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      myplayer.setVisible(true);
       }
}