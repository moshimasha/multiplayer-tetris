package com.catsandcheese.tetris;

import java.awt.*;


public abstract class Block{
   private int myX;
   private int myY;
   public Block(int x, int y){
      myX = x;
      myY = y;
   }
      public void rotateCounterClockwise(Grid[][] array, Grid[][] back, int[][] coord){
         rotateClockwise(array, back, coord);
         rotateClockwise(array, back, coord);
         rotateClockwise(array, back, coord);
      }
   public void rotateClockwise(Grid[][] array, Grid[][] back, int[][] coord){
      resetBlack(array, coord);
//      int largestY = 0;
//      int smallestY = coord[0][0];
//      int largestX = 0;
//      int smallestX = coord[0][1];
      int[][] old = new int[4][2];
      for(int i=0; i<4; i++){
         old[i][0] = coord[i][0];
         old[i][1] =coord[i][1];
      }
      int[] temp = new int[4];
      int midX = coord[1][1];
      int midY = coord[1][0];
      for(int i = 0; i<4; i++){
         coord[i][1] = coord[i][1] - midX;
         coord[i][0]  = coord [i][0] - midY;
      }
      for(int i = 0; i<4; i++){
         temp[i] = coord[i][0];
      }
      for(int i = 0; i<4; i++){
         coord[i][0]  = coord[i][1] + midY;
      }
      for(int i = 0; i<4; i++){
         coord[i][1]  = (temp[i] * (-1)) + midX;
      }
      if(!isLegal(back, coord)){
         for(int i=0; i<4; i++){
            coord[i][0] = old[i][0] ;
            coord[i][1] = old[i][1];
         }
      }
   }
   public abstract int[][] getArray();
   public abstract void draw(Grid[][] array); 
   public boolean isLegal(Grid[][] array, int[][] coord){
      for(int i=0; i<4; i++){
         if(coord[i][0] < 0 || coord[i][0] > array.length-1){
            return false;
         }
         if(coord[i][1] < 0 || coord[i][1] > array[0].length-1){
            return false;
         }
         if(array[coord[i][0]][coord[i][1]].hasColor()){
            return false; 
         }
      }
      return true;
   }
   public boolean fallIsLegal(Grid[][] array, Grid[][] board, int[][] coord){
      for(int i=0; i<4; i++){
         coord[i][0] +=1;	
      }  
      for(int i=0; i<4; i++){
         if(coord[i][0] > array.length-1){
            for(int k=0; k<4; k++){
               coord[k][0] -=1;	
            }  
            return false;
         }
         if(board[coord[i][0]][coord[i][1]].hasColor()){
            for(int k=0; k<4; k++){
               coord[k][0] -=1;	
            }
            return false; 
         }
      }
      
      for(int k=0; k<4; k++){
         coord[k][0] -=1;	
      }  
      
      return true;
   }
   public void move(int dx, int dy, Grid[][] array, Grid[][] back, int[][] coord){
      if(isLegal(back, coord)){
         resetBlack(array, coord);
      }
      for(int i=0; i<4; i++){
         coord[i][0] +=dy;
         coord[i][1] +=dx;		
      }  
      if(!isLegal(back, coord)){
         move(-1*dx, -1*dy, array, back, coord);
      }
   }
   //public abstract boolean hasLanded(Grid[][] array);
   public void resetBlack(Grid[][] array, int[][] coord){
      array[coord[0][0]][coord[0][1]].setColor(Color.BLACK);
      array[coord[1][0]][coord[1][1]].setColor(Color.BLACK);
      array[coord[2][0]][coord[2][1]].setColor(Color.BLACK);
      array[coord[3][0]][coord[3][1]].setColor(Color.BLACK);
   }
   public void setX(int x){
      myX = x;
   }
   public void setY(int y){
      myY = y;
   }
   public int getX(){
      return myX;
   }
   public  int getY(){
      return myY;
   }
}