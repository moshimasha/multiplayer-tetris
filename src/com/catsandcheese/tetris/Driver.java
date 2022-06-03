//Maria Molchanova, 10/26/18
package com.catsandcheese.tetris;

import javax.swing.JFrame;

//import com.oracle
//import java.io.*;
//import sun.audio.*;
//import javax.swing.JOptionPane;
//import java.io.*;
//import java.net.URL;
//import javax.sound.sampled.*;
//import javax.swing.*;
//import java.applet.Applet;
//import java.applet.AudioClip;
//import java.net.URL;


public class Driver {

	
   public static void main(String[] args) throws Exception{
   /*
      String newPlayer = JOptionPane.showInputDialog("New player? Y/N");
      if(newPlayer.toUpperCase.equals("N")){
         username = JOptionPane.showInputDialog("Enter username");
         boolean namePresent = true;
         for(int i=0; i<array.length; i++){
            if(array[i].getName().equals(username)){
               namePresent = false;
            }
         }
         if(namePresent){
            String x = JOptionPane.showInputDialog("Username not found.");
         }  
      }
      
      else{
         username = JOptionPane.showInputDialog("Enter username");
      }
      */
      JFrame frame = new JFrame("TETRIS");
      frame.setSize(360, 800);
      frame.setLocation(0, 0);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new Tetris());
      frame.setVisible(true);

  
   }

   
   public void gameOver(){
      
   }
}
