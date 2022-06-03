package com.catsandcheese.tetris;

import javax.swing.*;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.json.*;
import javax.json.JsonValue.ValueType;

class Row {
	
	public Date timestamp;
	public String player;
	
	public Row(Date timestamp, String player) {
		this.timestamp = timestamp;
		this.player = player;
	}
	public String getPlayer() {
		return player;
	}
	
}

public class Tetris  extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Color BACKGROUND = Color.WHITE; 

	JTextField textField;
   private BufferedImage myImage;
   private Graphics myBuffer;
   private Timer timer;
   private Timer timerFall;
   private Grid [][] matrix;
   private Grid [][] backboard;
   Tetrominoe tetrominoe = new Tetrominoe(0);
   int myScore = 0;
   int level = 0;
   int totalClears = 0;
   int rowAddCount = 0;
   String myPlayer;
   int numPressed = 0;
   String myUserName;
   boolean gameHasBegun=false;
   boolean gameOver;
   KeyListener MyKeyAdapter;
   java.util.Timer TIMER;
   public Tetris()
   {   
	  textField = new JTextField(20);
	  textField.setHorizontalAlignment(SwingConstants.CENTER);
	  textField.addActionListener(new ListenerEnter());
	  
	 
      myImage =  new BufferedImage(401, 800, BufferedImage.TYPE_INT_RGB);  
      myBuffer = myImage.getGraphics(); 
      myBuffer.setColor(Color.white);
      matrix = new Grid[20][10]; 
      backboard = new Grid[20][10]; 
   
      for(int i=0; i<backboard.length; i++){
         for(int j=0; j<backboard[0].length; j++){
            backboard[i][j] = new Grid(j*40+1, i*40, 39, Color.BLACK);
         }
      }
      for(int i=0; i<matrix.length; i++){
         for(int j=0; j<matrix[0].length; j++){
            matrix[i][j] = new Grid(j*40+1, i*40, 39, Color.BLACK);
         }
      }
      
      
      Font font = new Font("Courier", Font.PLAIN, 25);
      myBuffer.setFont(font);
      int a = 50;
      int b = 38;
      int x=20;
      myBuffer.drawString("Multiplayer Tetris Game.", x, a);
      myBuffer.drawString("Keys: Up and down arrows", x, a+1*b);
      myBuffer.drawString("rotate the figure, left", x, a+2*b);
      myBuffer.drawString("and right arrows move", x, a+3*b);
      myBuffer.drawString("the figure. Press space", x, a+4*b);
      myBuffer.drawString("to instantly drop figure.", x, a+5*b);
      myBuffer.drawString("Rules: When a player", x, a+6*b);
      myBuffer.drawString("completes a row, a row", x, a+7*b);
      myBuffer.drawString("will be added to the", x, a+8*b);
      myBuffer.drawString("other players' fields.", x, a+9*b);
      myBuffer.drawString("Game ends for all players", x, a+10*b);
      myBuffer.drawString("when a player loses.", x, a+11*b);
      myBuffer.drawString("Type name and press ENTER", x, a+12*b);
      myBuffer.drawString("to start.", x, a+13*b);
      
      textField.setFont(font);
      textField.setBackground(Color.black);
      textField.setForeground(Color.white);
      
      timer = new Timer(80, new Listener());
      timerFall = new Timer(400, new ListenerFall());
      TIMER = new java.util.Timer("tetris_timer", true);
      
      this.myPlayer = UUID.randomUUID().toString();
      TIMER.schedule(new TetrisTimerTask(), SERVER_CHECK_DELAY, SERVER_CHECK_DELAY);
      
      setLayout( new GridLayout(5, 1,200 , 130) );
      add(new JLabel(""));
      add(new JLabel(""));
      add(new JLabel(""));
      add(textField);
      add(new JLabel(""));
     
      MyKeyAdapter = new MyKeyAdapter();
      addKeyListener(MyKeyAdapter);
      setFocusable(true);
         
      //Sound.BACK.loop();
   }  
   
   public void paintComponent(Graphics g)
   {
      g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);
   }
   
   private class Listener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {  
         myBuffer.setColor(BACKGROUND);
         myBuffer.fillRect(0,0,401,800); 
         tetrominoe.draw(matrix);
         for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
               matrix[i][j].draw(myBuffer);
            }
         }
         repaint();
      }
   }
   private class ListenerEnter implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {  
    	  
    	  if((textField.getText()).equals("")) {
    		  textField.setText("Anonymous");
    	  }
    	  myUserName = textField.getText();;
    	  myBuffer.drawString("Waiting for other players", 20, 650);
    	  myBuffer.drawString("...", 20, 688);
    	  postPlayers(1,false);
    	  remove(textField);
      }
   }
   
   private class ListenerFall implements ActionListener
   {
      public void actionPerformed(ActionEvent b)
      {  
         myBuffer.setColor(BACKGROUND);
         myBuffer.fillRect(0,0,401,800);
         if(!tetrominoe.fallIsLegal(matrix, backboard)){
            Sound.FALL.play();
            tetrominoe.draw(backboard);
            tetrominoe.draw(matrix);
            changeScore();
            if(!tetrominoe.fallIsLegal(matrix, backboard)){
               //tetrominoe.draw(matrix);
               for(int i=0; i<matrix.length; i++){
                  for(int j=0; j<matrix[0].length; j++){
                     matrix[i][j].draw(myBuffer);
                  }
               }
               
               postPlayers(0, true);
               gameOver = true;
               timer.stop();
               timerFall.stop();
               removeKeyListener(MyKeyAdapter);
               
            }
         }
         
         tetrominoe.draw(matrix);
      
         for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
               matrix[i][j].draw(myBuffer);
            }
         }
        if(!gameOver) {
         tetrominoe.move(0, 1,matrix, backboard);
         repaint();
        }
      }
   }
   
   private void rowsAdded(ArrayList<Row> rows) {	
			   rowAddCount += rows.size();
   }

   private final static long SERVER_CHECK_DELAY = 2000;
   private final static String ROWS_BASE_REQUEST_STRING = "http://catsandcheese.com:8080/catsandcheese/rows";
   private final static String PLAYERS_BASE_REQUEST_STRING = "http://catsandcheese.com:8080/catsandcheese/players";
   private final static String THREADS_BASE_REQUEST_STRING = "http://catsandcheese.com:8080/catsandcheese/threads";
   private final static String PLAYERS_GET_REQUEST_STRING = PLAYERS_BASE_REQUEST_STRING + "?gameOver=";
   private final static String ROWS_GET_REQUEST_STRING = ROWS_BASE_REQUEST_STRING + "?timestamp=";
   int t = 0;
   private class TetrisTimerTask extends java.util.TimerTask {
  
		private Date timestamp = new Date(0);
		
		
		private void checkServer() {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			String fullRequestString = ROWS_GET_REQUEST_STRING.concat(String.valueOf(this.timestamp.getTime()));
			//System.out.println(timestamp.getTime());
			
			HttpGet httpGet = new HttpGet(fullRequestString);
			try {
				CloseableHttpResponse response = httpclient.execute(httpGet);
				HttpEntity entity = response.getEntity();
		        String retSrc = EntityUtils.toString(entity); 
		        
		        JsonReader reader = Json.createReader(new StringReader(retSrc));
		        JsonArray jsonArray = reader.readArray();
		        ArrayList<Row> rows = new ArrayList<Row>();
		        
		        for (JsonValue jsonValue : jsonArray) {
		        	JsonObject jsonObject = (JsonObject)jsonValue;
		        	
		        	String player = ((JsonString)jsonObject.get("player")).getString();
		        	Date rowTimestamp = new Date(((JsonNumber)jsonObject.get("timestamp")).longValue());
		        	
		        	if (rowTimestamp.after(this.timestamp)) {
		        		this.timestamp = rowTimestamp;
		        	}
		        	if (!(myPlayer.equals(player))) {
			        	Row row = new Row(rowTimestamp, player);
			        	rows.add(row);
		        	}
		        }
		     
		        
		        
		        //System.out.println(retSrc);
		        
		        SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						
						rowsAdded(rows);
					}
		        	
		        });
		        
			} catch (Exception e) {
				//System.out.println(e.getLocalizedMessage());
			}
			
			CloseableHttpClient httpclientPlayers = HttpClients.createDefault();
			String fullRequestStringPlayers;
			if(gameOver) {
				fullRequestStringPlayers = PLAYERS_GET_REQUEST_STRING.concat("1");
			}else {
				fullRequestStringPlayers = PLAYERS_GET_REQUEST_STRING.concat("0");
			}
	 		 HttpGet httpGetPlayers = new HttpGet(fullRequestStringPlayers);
	 		Date timestamp1 = new Date();
	 		if(t==0) {
	 		 timestamp1= new Date();
	 		System.out.println(timestamp1.getTime());
	 		}
	 		t=1;
	 		 try {
	 			if(gameOver) {
	 				 
	 				 timer.stop();
	 				 timerFall.stop();
	 				 myBuffer.setColor(Color.black);
	 				 myBuffer.fillRect(0,0,401, 800);
	 				 Font font = new Font("Courier", Font.PLAIN, 50);
	 			     myBuffer.setFont(font);
	 				 myBuffer.setColor(Color.white);
	 				 myBuffer.drawString("GAME OVER", 20, 50);
	 				 Font font2 = new Font("Courier", Font.PLAIN, 20);
	 			     myBuffer.setFont(font2);
	 				 myBuffer.drawString("Receiving scores...", 20, 70);
	 				 repaint();
	 				//System.out.println("receiving scores...");
	 				 //POST myUserName and score
	 				 postEndGame();				 
	 				 Thread.sleep(4000); 				 
	 				//GET Player array
	 				 getEndGame();				
	 				//Display scores on myBuffer
	 				 
	 			 } else {
	 			
	 			//System.out.println("game over???");
	 			 CloseableHttpResponse responsePlayers = httpclientPlayers.execute(httpGetPlayers);
	 			 HttpEntity entityPlayers = responsePlayers.getEntity();
	 			 String retSrcPlayers = EntityUtils.toString(entityPlayers); 	 			
	 			 JsonReader readerPlayers = Json.createReader(new StringReader(retSrcPlayers));	 			 
	 			 JsonObject objectPlayers = readerPlayers.readObject(); //throws exception
	 			 int playersReady = objectPlayers.getInt("playersReady");			
	 			 int playersPlaying = objectPlayers.getInt("playersPlaying");
	 			  gameOver = objectPlayers.getBoolean("gameOver");
	 			  //System.out.println(gameOver);
	 			 if(!gameHasBegun) {
	 				 if(playersReady == playersPlaying) {
	 					 
	 					Date timestamp2 = new Date();
	 			 		System.out.println(timestamp2.getTime());
	 			 		
	 			 		postTime(timestamp2.getTime());
	 			 		
	 			 		Thread.sleep(3000);
	 			 		Thread.sleep(getMilliTime());
	 			 		
	 					 timer.start();
	 					 timerFall.start();
	 					 gameHasBegun=true;
	 					
	 				 }
	 			 }
	 		}
	 			 
	 
	 		 } catch (Exception a) {
	 			
	 			 //System.out.println(a.getLocalizedMessage());
	 		 }

		}
		
		
		
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//	    	  try {
//	  			Thread.sleep(2000);
//	  		} catch (InterruptedException e1) {
//	  			// TODO Auto-generated catch block
//	  			e1.printStackTrace();
//	  		}
			this.checkServer();
			repaint();
		}
		public void interrupt() {
			this.interrupt();
			
		}
	}
   
   
   private class RowPoster implements Runnable {

	   private Integer rowCount;
	   
	   public RowPoster(Integer rowCount) {
		   this.rowCount = rowCount;
	   }
	   
	   private void postRows() {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			
			HttpPost httpPost = new HttpPost(ROWS_BASE_REQUEST_STRING);
			try {
				
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			    postParameters.add(new BasicNameValuePair("count", this.rowCount.toString()));
			    postParameters.add(new BasicNameValuePair("player", myPlayer)); //TODO: add player name here

			    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
				/*CloseableHttpResponse response = */httpclient.execute(httpPost);
				
			} catch (Exception e) {
				//System.out.println(e.getLocalizedMessage());
			}
	   }
	   
		@Override
		public void run() {
			
			this.postRows();
			//System.out.println(this.rowCount.toString().concat(" rows posted"));
		}
	   
   }
   public void postTime(long time) {
	   CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(THREADS_BASE_REQUEST_STRING);
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			
		    postParameters.add(new BasicNameValuePair("id", myPlayer));  
		    postParameters.add(new BasicNameValuePair("time", Long.toString(time)));  
		    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			httpclient.execute(httpPost);
			
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			
		}
   
   }
   public long getMilliTime() {
  CloseableHttpClient httpclient = HttpClients.createDefault();
	   HttpGet httpGet = new HttpGet(THREADS_BASE_REQUEST_STRING + "?id=" + myPlayer);
	   try {
			 CloseableHttpResponse response = httpclient.execute(httpGet);
			 HttpEntity entity = response.getEntity();
			 String retSrc = EntityUtils.toString(entity); 	 			
			 
			 JsonReader reader = Json.createReader(new StringReader(retSrc));	
			 
			 JsonObject jObject = reader.readObject(); 
			 long wait = Long.parseLong(jObject.getString("wait"));
			 System.out.println(wait);
			 return wait;
		 } catch (Exception a) {
			 
			 System.out.println(a.getLocalizedMessage());
			 return -1;
		 }
   
   }
   public void getEndGame() {
	   	myBuffer.setColor(Color.black);
	   	myBuffer.fillRect(0,0,401, 800);
		 Font font = new Font("Courier", Font.PLAIN, 50);
	     myBuffer.setFont(font);
		 myBuffer.setColor(Color.white);
		 myBuffer.drawString("GAME OVER", 20, 50);
		 Font font2 = new Font("Courier", Font.PLAIN, 20);
	     myBuffer.setFont(font2);
		 
		 
	   CloseableHttpClient httpclient = HttpClients.createDefault();
	   HttpGet httpGet = new HttpGet("http://catsandcheese.com:8080/catsandcheese/players?gameOver=1");
	   try {
			 CloseableHttpResponse response = httpclient.execute(httpGet);
			 HttpEntity entity = response.getEntity();
			 String retSrc = EntityUtils.toString(entity); 	 			
			 
			 JsonReader reader = Json.createReader(new StringReader(retSrc));	
			 
			 JsonArray array = reader.readArray(); //throws unexpected char
			
			 int i = 0;
			 for(JsonValue jsonValue : array){
			 	JsonObject jsonObject = (JsonObject)jsonValue;
			 	
			 	String userName = ((JsonString)jsonObject.get("userName")).getString();
			 	myBuffer.drawString(userName, 20, 70+20*i);		 	
			 	
			 	int finalScore = jsonObject.getInt("finalScore");
			 	myBuffer.drawString(Integer.toString(finalScore), 200, 70+20*i);
			 	i++;
			 	
			 	
			 }
			TIMER.cancel();
			 repaint();
		 } catch (Exception a) {
			 
			 //System.out.println(a.getLocalizedMessage());
		 }
		 
   }
   public void postEndGame() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(PLAYERS_BASE_REQUEST_STRING);
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			
		    postParameters.add(new BasicNameValuePair("userName", myUserName));  
		    postParameters.add(new BasicNameValuePair("finalScore", Integer.toString(myScore)));  
		    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			httpclient.execute(httpPost);
			
		} catch (Exception e) {
			//System.out.println(e.getLocalizedMessage());
			
		}
  }
   public void postPlayers(int addPlayerReady, boolean gameOver) {
	   	CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(PLAYERS_BASE_REQUEST_STRING);
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			if(addPlayerReady==1) {
		    postParameters.add(new BasicNameValuePair("playersReady", "1"));
		    
			}else {
			postParameters.add(new BasicNameValuePair("playersReady", "0"));
			}
			if(gameOver) {
		    postParameters.add(new BasicNameValuePair("gameOver", "1"));
		    	
			}else {
			postParameters.add(new BasicNameValuePair("gameOver", "0"));		
			}
		    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			httpclient.execute(httpPost);
			
		} catch (Exception e) {
			//System.out.println(e.getLocalizedMessage());
		}
   }
   
   public boolean checkRow(Grid[][] array, int num){
      int colored = 0;
      for(int i=0; i<10; i++){
         if(array[num][i].hasColor()){
            colored++;
         }
      }
      if(colored == array[0].length){
         return true;
      }
      return false;
   }
   public void remove(Grid[][]array, int num){
      for(int i = num; i>=1; i--){
         for(int j = 0; j<array[0].length; j++){
            array[i][j] .setColor(array[i-1][j].getColor());
         }
      }
   }
   public void add(int num) {
	   for(int i =0; i<backboard.length-num; i++){
	         for(int j = 0; j< backboard[0].length; j++){
	        	 if(!tetrominoe.tetrominoePresent(i+num, j)) {   
	        		backboard[i][j].setColor(backboard[i+num][j].getColor());
	            	matrix[i][j].setColor(matrix[i+num][j].getColor());
	            }
	         }
	      }
	   int random;
	      for(int i = 0; i<num; i++){
	         for(int j = 0; j<backboard[0].length; j++){
	             random = (int)(Math.random()*10);
	            if(random<1) {
	            	backboard[backboard.length-i-1][j] .setColor(Color.BLACK);
	            	matrix[matrix.length-i-1][j] .setColor(Color.BLACK);
	            	
	            }else {
	           
	            backboard[backboard.length-i-1][j] .setColor(new Color(100,100,100)); 
	            matrix[matrix.length-i-1][j] .setColor(new Color(100,100,100));
	            }
	            
	         }
	         random = (int)(Math.random()*10);
	            	backboard[backboard.length-i-1][random] .setColor(Color.BLACK);
	            	matrix[matrix.length-i-1][random] .setColor(Color.BLACK);
	            	
	            
	         
	      }

   }
   
   public void changeScore(){
      int random = (int)(Math.random() * 7);
      tetrominoe.newTetrominoe(random);
      int clears = 0;
      for(int i=backboard.length-1; i>=0; i--){
         if(checkRow(backboard, i)){
            remove(backboard, i);
            remove(matrix, i);
            clears++;
            i++;
         }
      }
      add(rowAddCount);
      rowAddCount = 0;
      if (clears>0) {
    	 new Thread(new RowPoster(clears)).start();
         Sound.CLEAR.play();
      }
      totalClears += clears;      
      level = totalClears/10;  
      timerFall.setDelay(400-(25*level));
      if(clears==1){
         myScore += 50*(level+1);
      }
      if(clears==2){
         myScore += 150*(level+1);
      }
      if(clears==3){
         myScore += 350*(level+1);
      }
      if(clears==4){
         myScore += 1000*(level+1);
      }  
} 
   private class MyKeyAdapter extends KeyAdapter
   {
      public void keyPressed(KeyEvent e){
         if(e.getKeyCode()==(KeyEvent.VK_UP)) {  
            tetrominoe.rotateClockwise(matrix, backboard); 
         }
         if(e.getKeyCode()==(KeyEvent.VK_SPACE)) {
         
        	 while(tetrominoe.fallIsLegal(matrix, backboard)){
        		if(gameOver) {
        			break;
        		}
               tetrominoe.move(0, 1,matrix, backboard);
        	 }
            Sound.FALL.play();
            //tetrominoe.move(0, 1,matrix, backboard);
            tetrominoe.draw(backboard);  
            tetrominoe.draw(matrix); 
            changeScore();
            if(!tetrominoe.fallIsLegal(matrix, backboard)) {
            	//System.out.println("THE END");
                postPlayers(0, true);
                gameOver = true;
                timer.stop();
                timerFall.stop();
                removeKeyListener(MyKeyAdapter);
            
            }
         }
         if(e.getKeyCode()==(KeyEvent.VK_LEFT)) {
            tetrominoe.move(-1, 0, matrix, backboard); 
         }
         if(e.getKeyCode()==(KeyEvent.VK_RIGHT)) {
            tetrominoe.move(1, 0, matrix, backboard); 
         }
         if(e.getKeyCode()==(KeyEvent.VK_DOWN)) {
            tetrominoe.rotateCounterClockwise(matrix, backboard); 
         }
      } 
   }
}