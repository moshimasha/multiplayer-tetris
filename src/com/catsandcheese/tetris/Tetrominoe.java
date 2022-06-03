package com.catsandcheese.tetris;

import java.awt.*;

public class Tetrominoe{
   int piece = 0;
   int coord[][] = new int [4][2];
   Color color;
   Color blue = new Color(0, 255, 255);
   Color purple  = new Color(128,0,128);
   Color[] colors = {blue, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, purple, Color.RED};
   int[][][] tetrominoes = {
   {{0, 4}, {1, 4}, {2, 4}, {3, 4}}, //I
   {{0, 4}, {1, 5}, {1, 6}, {1, 4}}, //J
   {{0, 6}, {1, 5}, {1, 4}, {1, 6}}, //L
   {{0, 4}, {1, 4}, {0, 5}, {1, 5}}, //O
   {{1, 4}, {1, 5}, {0, 5}, {0, 6}}, //S
   {{1, 3}, {1, 4}, {1, 5},{0, 4}}, //T
   {{1, 5}, {1, 4}, {0, 4}, {0, 3}} //Z
   };                          
   public Tetrominoe(int t){
      piece = t;
      color = colors[piece];
      for(int i=0; i<4; i++){
         coord[i][0] = tetrominoes[piece][i][0];
         coord[i][1] = tetrominoes[piece][i][1];    
      }
   }
   public void rotateClockwise(Grid[][] array, Grid[][] back){
      if(piece == 3){
      }else{
         resetBlack(array);
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
         if(!isLegal(back)){
            for(int i=0; i<4; i++){
               coord[i][0] = old[i][0] ;
               coord[i][1] = old[i][1];
            }
         }
      }
   }
   public void rotateCounterClockwise (Grid[][] array, Grid[][]back) {
	   rotateClockwise(array, back);
	   rotateClockwise(array, back);
	   rotateClockwise(array, back);
   }
   public void newTetrominoe (int t){
      piece = t;
      color = colors[piece];
      for(int i=0; i<4; i++){
         coord[i][0] = tetrominoes[piece][i][0];
         coord[i][1] = tetrominoes[piece][i][1];    
      }
   }
   public void draw(Grid[][] array){
      for(int i=0; i<4; i++){
         array[coord[i][0]][coord[i][1]].setColor(color);
      }
   }
   public boolean isLegal(Grid[][] array){
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
   public boolean fallIsLegal(Grid[][] array, Grid[][] board){
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
    public void move(int dx, int dy, Grid[][] array, Grid[][] back){ 
      if(isLegal(back)){
         resetBlack(array);
      }
      for(int i=0; i<4; i++){
         coord[i][0] +=dy;
         coord[i][1] +=dx;		
      }  
      if(!isLegal(back)){
         move(-1*dx, -1*dy, array, back);
      }
   }
   public void resetBlack(Grid[][] array){
      for(int i=0; i<4; i++){
         array[coord[0][0]][coord[0][1]].setColor(Color.BLACK);
         array[coord[1][0]][coord[1][1]].setColor(Color.BLACK);
         array[coord[2][0]][coord[2][1]].setColor(Color.BLACK);
         array[coord[3][0]][coord[3][1]].setColor(Color.BLACK);
      } 
   }
   public boolean tetrominoePresent(int a, int b){
	   for(int i=0; i<4; i++) {
		   if((coord[i][0] == a) && (coord[i][1] == b)) {   
			   return true;
		   }   	  
	   }
	   return false;
   }   
}