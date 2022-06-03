package com.catsandcheese.tetris;

import java.awt.*;
public class Grid{
   private Color myColor;
   private int myX;
   private int myY;
   private int mySide;
   public Grid(int x, int y, int s, Color  c){
      myColor = c;
      myX = x;
      myY = y;
      mySide = s;
   }
   public void setColor(Color c){
      myColor =c;
   }
   public Color getColor(){
      return myColor;
   }
   public boolean hasColor(){
      if(myColor != Color.BLACK){
         return true;
      }
      return false;
   }
   public void draw(Graphics myBuffer){
      myBuffer.setColor(myColor);
      myBuffer.fillRect(myX, myY, mySide, mySide);
   }
}