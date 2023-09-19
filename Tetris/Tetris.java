//////TETRIS v.0.1


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;
import java.io.*;




class Tetris{

static Random random = new Random();
static JButton playAgainButton;
//static boolean playAgainButtonVisible=false;
static JFrame frame;
static GamePanel mainPanel;
  
public static void main(String[] args){

   frame = new JFrame("TETRIS");
   mainPanel = new GamePanel();
   
   playAgainButton = new JButton("PLAY AGAIN");
   playAgainButton.setPreferredSize(new Dimension(GamePanel.SIZE_X, 25));
   playAgainButton.setVisible(false);
   playAgainButton.setRequestFocusEnabled(false);
   
   
   mainPanel.setRequestFocusEnabled(true);
   mainPanel.requestFocusInWindow();
   
   
   frame.setLayout(new BorderLayout());   
   frame.add(mainPanel, BorderLayout.CENTER);
   frame.add(playAgainButton, BorderLayout.PAGE_END);
 
    
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.setResizable(false);
   frame.setVisible(true);
   frame.pack();
   
   Timer timer = new Timer(mainPanel);

   
   mainPanel.addKeyListener(new KeyBoardListener(mainPanel));
   //frame.addKeyListener(new KeyBoardListener(mainPanel));
   playAgainButton.addActionListener(new PlayButtonListener(mainPanel,timer));

   System.out.println(FileRW.path);

}

public static void showPlayButton(boolean show){
	
	/*
	if(show){
		frame.add(playAgainButton, BorderLayout.PAGE_END);
		playAgainButtonVisible=true;
	}else{
		frame.remove(playAgainButton);
		playAgainButtonVisible=false;
	}
	*/
	
	playAgainButton.setVisible(show);
	frame.revalidate();
	frame.repaint();
	frame.pack();
}


}


class GamePanel extends JPanel{

Color backgroundColor = Color.BLACK;
static final int SIZE_X=Piece.SQUARES_SIZE*10;   //*25;
static final int SIZE_Y=Piece.SQUARES_SIZE*20;   //*50;

public GamePanel(){


this.setBackground(backgroundColor);
	this.setVisible(true);
	this.setFocusable(true);
	this.setPreferredSize(new Dimension(SIZE_X,SIZE_Y));

}

public void paintComponent(Graphics g){
super.paintComponent(g);


synchronized(Piece.allPiecesList){
	
	
for(Piece piece : Piece.allPiecesList){
	g.setColor(piece.color);
	g.fillRect(piece.positionX,piece.positionY,piece.SQUARES_SIZE,piece.SQUARES_SIZE);
	//border
	g.setColor(Color.WHITE);
	g.drawRect(piece.positionX,piece.positionY,piece.SQUARES_SIZE,piece.SQUARES_SIZE);
	
}
}

if(Timer.spawnBonus || Timer.showBonusTxt){
	g.setColor(Color.WHITE);
	g.setFont(new Font("",Font.BOLD,50));
	g.drawString("NEIN!",SIZE_X/4,SIZE_Y/2);
	
	
}

     //SCORE
    g.setColor(Color.WHITE);
	g.setFont(new Font("",Font.BOLD,25));
	g.drawString(Integer.toString(Timer.linesCounter),10, 25);
	
	if(Timer.showTetrisTxt){
		g.setColor(Color.WHITE);
	g.setFont(new Font("",Font.BOLD,25));
	g.drawString("TETRIS!",SIZE_X/3,SIZE_Y/2);
		
	}
	
	//NEW GAME!
	if(Timer.showNewGameTxt){
		g.setColor(Color.WHITE);
	g.setFont(new Font("",Font.BOLD,25));
	g.drawString("NEW GAME!",SIZE_X/4,SIZE_Y/2);
		Timer.showNewGameTxt=false;
	}
	
	
	//GAMEOVER
	if(Timer.gameover){
	g.setColor(Color.WHITE);
	g.setFont(new Font("",Font.BOLD,25));
	g.drawString("GAMEOVER",SIZE_X/4,SIZE_Y/2);
	
	String highScoreTxt="";
	if(Timer.isHighScore){
		highScoreTxt+="New ";
	}
	highScoreTxt+=("HIGHSCORE: " + Integer.toString(Timer.highScore));
	
	g.setFont(new Font("",Font.BOLD,15));
	g.drawString(highScoreTxt,SIZE_X/4,(SIZE_Y/2)+15);
	
	}


}

}

class PlayButtonListener implements ActionListener{
	
	GamePanel gamePanel;
	Timer timer;
	
	public PlayButtonListener(GamePanel gamePanel, Timer timer){
		this.gamePanel=gamePanel;
		this.timer=timer;
	}
	
	public void actionPerformed(ActionEvent event){
		
		System.out.println("PLAY AGAIN!");
		
		synchronized(Tetrimin.tetriminList){
			synchronized(Piece.allPiecesList){
			
		for(Tetrimin tetrimin: Tetrimin.tetriminList){
			tetrimin.piecesList.clear();
		}
		
		Tetrimin.tetriminList.clear();
		Piece.allPiecesList.clear();
		Piece.stuckList.clear();
		
		}
		}
		
		if(Timer.gameover){
			Tetris.showPlayButton(false);
		}
			
		Timer.resetTimer();	
		gamePanel.repaint();
		
		synchronized(timer){
			timer.notify();
		}
		
		

			
	}
	
	
}


class KeyBoardListener extends KeyAdapter{
	
	
	
	static final int VK_MINUS=109;
	static final int VK_PLUS=107;
	
	
	
	
	boolean downPressed = false;
	
	GamePanel gamePanel;
	
	public KeyBoardListener(GamePanel gamePanel){
		this.gamePanel=gamePanel;
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		//System.out.println("KeyPressed : "+key);
		
		
		if(key==VK_MINUS){
			if(Timer.gameSpeed<Timer.currentMinGameSpeed){
			    Timer.gameSpeed+=Timer.SPEED_INCREMENT;
			}
		}
		else if(key==VK_PLUS){
			if(Timer.gameSpeed-Timer.SPEED_INCREMENT>Timer.MAX_GAME_SPEED){
			Timer.gameSpeed-=Timer.SPEED_INCREMENT;
			}
		}
		else if(key==KeyEvent.VK_DOWN  || key==KeyEvent.VK_S){
			if(!downPressed){
			//Timer.focusedTetrimin.move();	
			Timer.savedGameSpeed=Timer.gameSpeed;
			Timer.gameSpeed=Timer.MAX_GAME_SPEED;
			}
			downPressed=true;
		}
		else if(key==KeyEvent.VK_LEFT || key==KeyEvent.VK_A){
			if(Timer.focusedTetrimin!=null){
			Timer.focusedTetrimin.moveLeft();
			gamePanel.repaint();
			}
		}
		else if(key==KeyEvent.VK_RIGHT || key==KeyEvent.VK_D){
			if(Timer.focusedTetrimin!=null){
			Timer.focusedTetrimin.moveRight();
			gamePanel.repaint();
			}
		}
		else if(key==KeyEvent.VK_V){
			//Tetris.showPlayButton(!Tetris.playAgainButtonVisible);
			Tetris.showPlayButton(!Tetris.playAgainButton.isVisible());
		
		}else{
			if(Timer.focusedTetrimin!=null){
			Timer.focusedTetrimin.rotate(0);  //zero is the axis piece (first in the list)
			gamePanel.repaint();
			}
		}
		
		//System.out.println("GameSpeed = "+Timer.gameSpeed);
		
	}
	
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		//System.out.println("KeyReleased : "+key);
		
		if(key==KeyEvent.VK_DOWN || key==KeyEvent.VK_S){
			Timer.gameSpeed=Timer.savedGameSpeed;
			downPressed=false;
		}
		//System.out.println("GameSpeed = "+Timer.gameSpeed);
		
		
		
	}
	

	
}






class Tetrimin{  //made of Pieces ->squares

    static java.util.List<Tetrimin> tetriminList = Collections.synchronizedList(new ArrayList<Tetrimin>());
	java.util.List<Piece> piecesList = Collections.synchronizedList(new ArrayList<Piece>());
	
	
	static final Color DARK_YELLOW = ((Color.YELLOW).darker());//.darker();
	
	static final int BONUS_TYPE=-1;
	static final int I_TYPE=0;
	static final int Q_TYPE=1;
	static final int T_TYPE=2;
	static final int SL_TYPE=3;
	static final int SR_TYPE=4;
	static final int LL_TYPE=5;
	static final int LR_TYPE=6;
	
	static final Color BONUS_COLOR=Color.BLACK;
	static final Color I_COLOR=Color.CYAN;
	static final Color Q_COLOR=DARK_YELLOW;
	static final Color T_COLOR=Color.GREEN;
	static final Color SL_COLOR=Color.RED;
	static final Color SR_COLOR=Color.PINK;
	static final Color LL_COLOR=Color.BLUE;
	static final Color LR_COLOR=Color.MAGENTA;
	
	
	
	
	int type;
	//types L, I , Sx2 , O 
	//orientation x4
	
	boolean stuck = false;
	
	public Tetrimin(){
		if(Timer.spawnBonus){
			type=BONUS_TYPE;;
		}
		else{
		type= Tetris.random.nextInt(7);
		}
		
		//Timer.focusedTetrimin=this;
		this.create(type);
		
	}
	
	synchronized void create(int type){	
	
	if(type == I_TYPE){
		
		Piece b = new Piece();
		b.setColor(I_COLOR);
		piecesList.add(b);
		
		
		Piece b1 =new Piece();
		b1.setColor(I_COLOR);
		piecesList.add(b1);
		b1.setPosition(b.positionX ,b.positionY-Piece.SQUARES_SIZE);
		
		Piece b2 = new Piece();
		b2.setColor(I_COLOR);
		piecesList.add(b2);
		b2.setPosition(b.positionX ,b.positionY-Piece.SQUARES_SIZE*2);
		
		Piece b3 =new Piece();
		b3.setColor(I_COLOR);
		piecesList.add(b3);
		b3.setPosition(b.positionX ,b.positionY+Piece.SQUARES_SIZE);
		
	    
	}
	if(type == Q_TYPE){
		
		Piece q = new Piece();
		q.setColor(Q_COLOR);
		piecesList.add(q);
		
		Piece q1 =new Piece();
		q1.setColor(Q_COLOR);
		piecesList.add(q1);	
		q1.setPosition(q.positionX ,q.positionY-Piece.SQUARES_SIZE);
		
		
		Piece q2 = new Piece();
		q2.setColor(Q_COLOR);
		piecesList.add(q2);	
		q2.setPosition(q.positionX+Piece.SQUARES_SIZE ,q.positionY);
		
		Piece q3 =new Piece();
		q3.setColor(Q_COLOR);
		piecesList.add(q3);	
		q3.setPosition(q.positionX+Piece.SQUARES_SIZE ,q.positionY-Piece.SQUARES_SIZE);
		
	    
	}
	if(type == SL_TYPE){
		
		Piece sl = new Piece();
		sl.setColor(SL_COLOR);
		piecesList.add(sl);
		
		Piece sl1 =new Piece();
		sl1.setColor(SL_COLOR);
		piecesList.add(sl1);
		sl1.setPosition(sl.positionX ,sl.positionY-Piece.SQUARES_SIZE);
		
		Piece sl2 = new Piece();
		sl2.setColor(SL_COLOR);
		piecesList.add(sl2);
		sl2.setPosition(sl.positionX+Piece.SQUARES_SIZE ,sl.positionY+Piece.SQUARES_SIZE);
		
		Piece sl3 =new Piece();
		sl3.setColor(SL_COLOR);
		piecesList.add(sl3);
		sl3.setPosition(sl.positionX+Piece.SQUARES_SIZE ,sl.positionY);
		
	    
	}
	if(type == SR_TYPE){
		
		Piece sr = new Piece();
		sr.setColor(SR_COLOR);
		piecesList.add(sr);
		
		Piece sr1 =new Piece();
		sr1.setColor(SR_COLOR);
		piecesList.add(sr1);
		sr1.setPosition(sr.positionX ,sr.positionY-Piece.SQUARES_SIZE);
		
		Piece sr2 = new Piece();
		sr2.setColor(SR_COLOR);
		piecesList.add(sr2);
		sr2.setPosition(sr.positionX -Piece.SQUARES_SIZE,sr.positionY+Piece.SQUARES_SIZE);
		
		Piece sr3 =new Piece();
		sr3.setColor(SR_COLOR);
		piecesList.add(sr3);
		sr3.setPosition(sr.positionX-Piece.SQUARES_SIZE ,sr.positionY);
		
		
	    
	}
	if(type == LL_TYPE){
		
			Piece ll = new Piece();
		ll.setColor(LL_COLOR);
		piecesList.add(ll);
		
		Piece ll1 =new Piece();
		ll1.setColor(LL_COLOR);
		piecesList.add(ll1);
		ll1.setPosition(ll.positionX ,ll.positionY-Piece.SQUARES_SIZE);
		
		Piece ll2 = new Piece();
		ll2.setColor(LL_COLOR);
		piecesList.add(ll2);
		ll2.setPosition(ll.positionX ,ll.positionY-Piece.SQUARES_SIZE*2);
		
		Piece ll3 =new Piece();
		ll3.setColor(LL_COLOR);
		piecesList.add(ll3);
		ll3.setPosition(ll.positionX-Piece.SQUARES_SIZE ,ll.positionY);
	    
	}
	if(type == LR_TYPE){
		
			Piece lr = new Piece();
		lr.setColor(LR_COLOR);
		piecesList.add(lr);
		
		Piece lr1 =new Piece();
		lr1.setColor(LR_COLOR);
		piecesList.add(lr1);
		lr1.setPosition(lr.positionX ,lr.positionY-Piece.SQUARES_SIZE);
		
		Piece lr2 = new Piece();
		lr2.setColor(LR_COLOR);
		piecesList.add(lr2);
		lr2.setPosition(lr.positionX ,lr.positionY-Piece.SQUARES_SIZE*2);
		
		Piece lr3 =new Piece();
		lr3.setColor(LR_COLOR);
		piecesList.add(lr3);
		lr3.setPosition(lr.positionX+Piece.SQUARES_SIZE ,lr.positionY);
	    
	}
	if(type == T_TYPE){
		
			Piece t = new Piece();
		t.setColor(T_COLOR);
		piecesList.add(t);
		
		Piece t1 =new Piece();
		t1.setColor(T_COLOR);
		piecesList.add(t1);
		t1.setPosition(t.positionX ,t.positionY-Piece.SQUARES_SIZE);
		
		Piece t2 = new Piece();
		t2.setColor(T_COLOR);
		piecesList.add(t2);
		t2.setPosition(t.positionX-Piece.SQUARES_SIZE ,t.positionY);
		
		Piece t3 =new Piece();
		t3.setColor(T_COLOR);
		piecesList.add(t3);
		t3.setPosition(t.positionX+Piece.SQUARES_SIZE ,t.positionY);
	    
	}
	if(type==BONUS_TYPE){
		
			Piece bonus = new Piece();
			bonus.isBonusPiece=true;
		bonus.setColor(BONUS_COLOR);
		piecesList.add(bonus);
		///////
		Piece bonus1 =new Piece();
		bonus1.isBonusPiece=true;
		bonus1.setColor(BONUS_COLOR);
		piecesList.add(bonus1);
		bonus1.setPosition(bonus.positionX ,bonus.positionY-Piece.SQUARES_SIZE);
		
		Piece bonus2 =new Piece();
		bonus2.isBonusPiece=true;
		bonus2.setColor(BONUS_COLOR);
		piecesList.add(bonus2);
		bonus2.setPosition(bonus.positionX ,bonus.positionY-Piece.SQUARES_SIZE*2);
		
		Piece bonus3 =new Piece();
		bonus3.isBonusPiece=true;
		bonus3.setColor(BONUS_COLOR);
		piecesList.add(bonus3);
		bonus3.setPosition(bonus.positionX+Piece.SQUARES_SIZE ,bonus.positionY-Piece.SQUARES_SIZE*2);
		
		Piece bonus4 =new Piece();
		bonus4.isBonusPiece=true;
		bonus4.setColor(BONUS_COLOR);
		piecesList.add(bonus4);
		bonus4.setPosition(bonus.positionX+Piece.SQUARES_SIZE*2 ,bonus.positionY-Piece.SQUARES_SIZE*2);
		////////////////
		
		Piece bonus14 =new Piece();
		bonus14.isBonusPiece=true;
		bonus14.setColor(BONUS_COLOR);
		piecesList.add(bonus14);
		bonus14.setPosition(bonus.positionX ,bonus.positionY+Piece.SQUARES_SIZE);
		
		Piece bonus15 =new Piece();
		bonus15.isBonusPiece=true;
		bonus15.setColor(BONUS_COLOR);
		piecesList.add(bonus15);
		bonus15.setPosition(bonus.positionX ,bonus.positionY+Piece.SQUARES_SIZE*2);
		
		Piece bonus16 =new Piece();
		bonus16.isBonusPiece=true;
		bonus16.setColor(BONUS_COLOR);
		piecesList.add(bonus16);
		bonus16.setPosition(bonus.positionX-Piece.SQUARES_SIZE ,bonus.positionY+Piece.SQUARES_SIZE*2);
		
		Piece bonus17 =new Piece();
		bonus17.isBonusPiece=true;
		bonus17.setColor(BONUS_COLOR);
		piecesList.add(bonus17);
		bonus17.setPosition(bonus.positionX-Piece.SQUARES_SIZE*2 ,bonus.positionY+Piece.SQUARES_SIZE*2);
				
		///////////////
		Piece bonus5 =new Piece();
		bonus5.isBonusPiece=true;
		bonus5.setColor(BONUS_COLOR);
		piecesList.add(bonus5);
		bonus5.setPosition(bonus.positionX-Piece.SQUARES_SIZE,bonus.positionY);
		
		Piece bonus6 =new Piece();
		bonus6.isBonusPiece=true;
		bonus6.setColor(BONUS_COLOR);
		piecesList.add(bonus6);
		bonus6.setPosition(bonus.positionX-Piece.SQUARES_SIZE*2 ,bonus.positionY);
		
		Piece bonus7 =new Piece();
		bonus7.isBonusPiece=true;
		bonus7.setColor(BONUS_COLOR);
		piecesList.add(bonus7);
		bonus7.setPosition(bonus.positionX-Piece.SQUARES_SIZE*2 ,bonus.positionY-Piece.SQUARES_SIZE);
		
		Piece bonus8 =new Piece();
		bonus8.isBonusPiece=true;
		bonus8.setColor(BONUS_COLOR);
		piecesList.add(bonus8);
		bonus8.setPosition(bonus.positionX-Piece.SQUARES_SIZE*2 ,bonus.positionY-Piece.SQUARES_SIZE*2);
		//////////////
		Piece bonus10 =new Piece();
		bonus10.isBonusPiece=true;
		bonus10.setColor(BONUS_COLOR);
		piecesList.add(bonus10);
		bonus10.setPosition(bonus.positionX+Piece.SQUARES_SIZE,bonus.positionY);
		
		Piece bonus11 =new Piece();
		bonus11.isBonusPiece=true;
		bonus11.setColor(BONUS_COLOR);
		piecesList.add(bonus11);
		bonus11.setPosition(bonus.positionX+Piece.SQUARES_SIZE*2 ,bonus.positionY);
		
		Piece bonus12 =new Piece();
		bonus12.isBonusPiece=true;
		bonus12.setColor(BONUS_COLOR);
		piecesList.add(bonus12);
		bonus12.setPosition(bonus.positionX+Piece.SQUARES_SIZE*2 ,bonus.positionY+Piece.SQUARES_SIZE);
		
		Piece bonus13 =new Piece();
		bonus13.isBonusPiece=true;
		bonus13.setColor(BONUS_COLOR);
		piecesList.add(bonus13);
		bonus13.setPosition(bonus.positionX+Piece.SQUARES_SIZE*2 ,bonus.positionY+Piece.SQUARES_SIZE*2);
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	tetriminList.add(this);
	}
	
	
	
	
		public void move(){
			  
			  
	      
			  for(Piece piece : this.piecesList){
				  
				  
				  if(piece.positionY>=GamePanel.SIZE_Y-Piece.SQUARES_SIZE){
					  piece.setStuck(true);
					  		  
					  if(this.equals(Timer.focusedTetrimin)){
					  Timer.focusedTetrimin=null;
					  Timer.tetriminStuck=true; //checkLines
					  }
				  }
				  
				  
				  //////////
				  
				  ArrayList<Piece> pieceKilledList = new ArrayList<Piece>();
				 
				  for(Piece otherPiece: Piece.allPiecesList){
					  if(!piece.equals(otherPiece)){
						  		
							
						if(!piece.isBonusPiece){	
					  if(otherPiece.stuck && !piece.stuck && piece.positionY+Piece.SQUARES_SIZE == otherPiece.positionY && piece.positionX == otherPiece.positionX){
						  
						  
						  //System.out.println("stuck1");	
				           piece.setStuck(true);
						   if(this.equals(Timer.focusedTetrimin)){
					        Timer.focusedTetrimin=null;
							Timer.tetriminStuck=true;//checkLines
					        }
						   
			          }	
						}else{
							 if(otherPiece.stuck && !piece.stuck && piece.positionY == otherPiece.positionY && piece.positionX == otherPiece.positionX){
								 pieceKilledList.add(otherPiece);
							 }
							
						}
					  
					  
					  
					  
					  } 
				  }
				  
				  if(!pieceKilledList.isEmpty()){
					  Piece.allPiecesList.removeAll(pieceKilledList);
				  }
				  
                  
							  
				  //////
				  for(Piece otherPiece : this.piecesList){
					  if(!piece.equals(otherPiece)){
						  
					    if(piece.stuck && !otherPiece.stuck){					  
						  otherPiece.setStuck(true);
                          //System.out.println("stuck2");						  
					  }
					  }
				  }
				  
			  }
			  
			  for(Piece piece : this.piecesList){				  
				  piece.move();
		      }
		  	
		}
		
		/////////////
		public boolean canMoveRight(){
		boolean can=true;
		
		for(Piece piece : this.piecesList){
		if(piece.positionX+Piece.SQUARES_SIZE*2>GamePanel.SIZE_X){
			can=false;
			
			//System.out.println(can+"1");
		}
		}
		
		for(Piece piece : this.piecesList){
		for(Piece otherPiece : Piece.allPiecesList){	
		    if(!piece.equals(otherPiece)&& !this.piecesList.contains(otherPiece) && piece.positionX+Piece.SQUARES_SIZE == otherPiece.positionX && piece.positionY == otherPiece.positionY ){
				can = false;
				//System.out.println(can+"2");
			}
		}
		}
			
		return can;
		
	}
	
	public boolean canMoveLeft(){
		boolean can=true;
		
		for(Piece piece : this.piecesList){
		if(piece.positionX-Piece.SQUARES_SIZE<0){
			can=false;
		}
		}
		
		for(Piece piece : this.piecesList){
		for(Piece otherPiece : Piece.allPiecesList){	
		    if(!piece.equals(otherPiece)&& !this.piecesList.contains(otherPiece) && piece.positionX-Piece.SQUARES_SIZE == otherPiece.positionX && piece.positionY == otherPiece.positionY){
				can = false;
				//System.out.println(can+"2");
			}
		}
		}
		
		return can;
	}
		
	public void moveLeft(){
		
		if(canMoveLeft()){
		  for(Piece piece : this.piecesList){	

				  piece.moveLeft();
		      }	
		}
	}
	public void moveRight(){
			
		if(canMoveRight()){
		 for(Piece piece : this.piecesList){				  
				  piece.moveRight();
		      }
		}
		
	}
	
	
	
public void rotate(int axisN){  //anticlock 90
	
			Piece axis = piecesList.get(axisN);
			
			ArrayList<Boolean> canRotateList = new ArrayList<Boolean>();
			ArrayList<Integer> newPositionsX = new ArrayList<Integer>();
			ArrayList<Integer> newPositionsY = new ArrayList<Integer>();
			
			
			
		for(Piece piece : piecesList){
			//if(!piece.equals(axis)){
			//&& piece.equals(piecesList.get(1))
			
			int distX = (piece.positionX-axis.positionX);
			int distY = (piece.positionY-axis.positionY);
			
			
			//System.out.println("posX:"+piece.positionX);
			//System.out.println("posY:"+piece.positionY);
			//System.out.println("axisX:"+axis.positionX);
			//System.out.println("axisY:"+axis.positionY);
			
			
			//System.out.println("distX:"+distX);
			//System.out.println("distY:"+distY);
			
			
			int newPositionX=piece.positionX;
			int newPositionY=piece.positionY;
		
			if(distX>0){ //RIGHT ->UP
				 
				 
				 
				 newPositionY=axis.positionY-distX;
			}
			else if(distX<0) {  //LEFT  ->  DOWN
			     
				 newPositionY=axis.positionY+(-distX);
			}
			else{
				newPositionY=axis.positionY;
			}
			
			
			if(distY>0){ //DOWN -> RIGHT
			  
			   newPositionX=axis.positionX+distY;
				
			}
			else if(distY<0){ //UP -> LEFT
			  
			   newPositionX=axis.positionX-(-distY);
			}
			else{
				newPositionX=axis.positionX;
			}
			
			//check isFree
			
			//System.out.println("newPosX:"+newPositionX);
			//System.out.println("newPosY:"+newPositionY);
	        
			boolean canRotate=true;
			
			for(Piece otherPiece : Piece.allPiecesList){
	        if(!this.piecesList.contains(otherPiece) && newPositionX==otherPiece.positionX && newPositionY==otherPiece.positionY){               
				   canRotate=false; 
			}
			}
			
			if(newPositionX<0 || newPositionX>=GamePanel.SIZE_X){
				canRotate=false;
			}
			
			if( newPositionY>=GamePanel.SIZE_Y){
				canRotate=false;
			}
			
			
			canRotateList.add(canRotate);
			newPositionsX.add(newPositionX);
			newPositionsY.add(newPositionY);
		    
	        
			
			//}
					
		}
		
		//set
		boolean canRotate=true;
		
		for(int i=0; i<piecesList.size();i++){
			if(canRotateList.get(i)==false){
			canRotate=false;
			}				
		}
		
		if(canRotate){
			int i=0;
		for(Piece piece: piecesList){
		    piece.positionX=newPositionsX.get(i);
			piece.positionY=newPositionsY.get(i);
		
		i++;
		}
		
		}
		else{
			if(axisN+1<piecesList.size()){
				rotate(axisN+1);
			}
		}

			
}
	 
}


class Piece{
	
	static java.util.List<Piece> allPiecesList = Collections.synchronizedList(new ArrayList<Piece>());
	static java.util.List<Piece> stuckList = Collections.synchronizedList(new ArrayList<Piece>());
	
	static final int SQUARES_SIZE=25; //10
	int squaresN;
	int positionX;
	int positionY;
	boolean stuck=false;
	
	Color color = Color.BLACK;
	
	boolean isBonusPiece=false;
	
	
	public Piece(){
			
		this.create();
		
	}
	
	public void initPosition(){
		if(Timer.spawnBonus){
			this.positionX = ((GamePanel.SIZE_X/SQUARES_SIZE)/2)*SQUARES_SIZE;
		    this.positionY = 0;
		}
		else{	
		  
		this.positionX = (Tetris.random.nextInt((GamePanel.SIZE_X/SQUARES_SIZE)-4)+2)*SQUARES_SIZE;
		this.positionY = 0;
			
		}
		//System.out.println("x:"+positionX+" y:"+positionY);
	}
	
	public void setPosition(int x, int y){
		this.positionX = x;
		this.positionY = y;
	}
	
	public void setColor(Color color){
		this.color=color;
	}
	
	public void move(){
		
		/*
		for(Piece piece: Piece.piecesList){
			if(piece.stuck && this.positionY+this.SQUARES_SIZE == piece.positionY && this.positionX == piece.positionX){
				this.setStuck();
			}
			
		}
		
		
		
		if(this.positionY>=GamePanel.SIZE_Y-SQUARES_SIZE){
			this.setStuck();
		}
		
		*/
		
		
		if(!stuck){
		this.positionY+=SQUARES_SIZE;
		}
	}
	
	
	public void moveRight(){	
		this.positionX+=SQUARES_SIZE;	
	}
	
	public void moveLeft(){		
		this.positionX-=SQUARES_SIZE;	
	}
	
	
	public void setStuck(boolean isStuck){
		this.stuck=isStuck;
		
		if(isStuck){
		stuckList.add(this);	
		}else{
		stuckList.remove(this);	
		}

		
	}
	
	
	
	
	synchronized void delete(){
		Piece.allPiecesList.remove(this);
	}
	
	synchronized void create(){	
	    this.initPosition();
		Piece.allPiecesList.add(this);
	}
	
}


class Timer extends Thread{

   static boolean exitGame=false;
    static boolean enableBonusPiece=true;
	
	static boolean gameover=false;
	static int highScore=0;
	static boolean isHighScore = false;
	
	GamePanel gamePanel;
	
	static final int ONE_SEC=1000;
	static final int ONE_MIN=ONE_SEC*60;
	
	static final int BONUS_TIME=ONE_MIN;
	static int bonusCounter=BONUS_TIME;
	static boolean spawnBonus=false;
	static final int SPAWN_BONUS_SCORE=100;
	static boolean showBonusTxt = false;
	
	static final int MAX_GAME_SPEED=25;
	static final int MIN_GAME_SPEED=500;	
	static final int SPEED_INCREMENT=25;
	static final int LINE_SPEED_INCREMENT = 1;
	
	static int gameSpeed=MIN_GAME_SPEED;
	static int currentMinGameSpeed=MIN_GAME_SPEED;
	
	static int savedGameSpeed=gameSpeed;
	
	static Tetrimin focusedTetrimin=null;
	static boolean tetriminStuck=false;
	
	static ArrayList<Integer> removeLinesList = new ArrayList<Integer>();
	
	static int linesCounter=0;
	
	
	
	
	static int spawnTime = 1000;
	//int spawnCounter=0;
	
	static boolean showTetrisTxt=false;
	static boolean showNewGameTxt=true;
	static final int newGameDelay=1000;
		
	public Timer(GamePanel gamePanel){
		this.gamePanel=gamePanel;
		this.start();
	}
	
	public void run(){
	while(!exitGame){

     System.out.println("start");	
	 //showNewGameTxt=true;
	 	try{
			sleep(newGameDelay);
		}catch(Exception e){}
	 
	 
		while(!gameover){
			
			
			
			//System.out.println(focusedTetrimin);
				
		try{
			//spawnCounter-=gameSpeed;
			bonusCounter-=gameSpeed;
			sleep(gameSpeed);
		}catch(Exception e){}
		
		//if(spawnCounter<=0){
			if(Timer.focusedTetrimin==null){	
            //System.out.println("spawn: "+Timer.focusedTetrimin==null);
               // if(bonusCounter<=0 && enableBonusPiece){
				   /*
				if(	enableBonusPiece && linesCounter!=0 && linesCounter%100==0){
					spawnBonus=true;
					//bonusCounter=BONUS_TIME;
			    }else{
					spawnBonus=false;
				}
				*/
								
			     Timer.focusedTetrimin = new Tetrimin();
				 
				 if(enableBonusPiece && spawnBonus==true){
					 spawnBonus=false;
					 showBonusTxt=true;
				 }else{
					 showBonusTxt=false;
				 }
				 
				 
			    
			}
		//	spawnCounter=spawnTime;
		    
			
		//}
		
		if(removeLinesList.isEmpty()){
		
		//can move only focused tetrimin instead
		//if(focusedTetrimin!=null){
		//focusedTetrimin.move();
		//}
		//
		synchronized(Tetrimin.tetriminList){
			synchronized(Piece.allPiecesList){
			for(Tetrimin tetrimin : Tetrimin.tetriminList){
				tetrimin.move();
			}
			}
		}
		
		}else{
			
			if(focusedTetrimin!=null){
			
			synchronized(Piece.allPiecesList){
			for(Piece piece : Piece.allPiecesList){
				if(!focusedTetrimin.piecesList.contains(piece) && piece.positionY/Piece.SQUARES_SIZE<=removeLinesList.get(0)){
				piece.move();
				}
			}
			}
            removeLinesList.remove(0);	
			
			if(removeLinesList.isEmpty()){
				for(Piece piece : Piece.allPiecesList){
				if(!focusedTetrimin.piecesList.contains(piece)){
				piece.setStuck(true);
				}
			}
			}
			
			}
		        			
		}
		
		
		
		////check for lines
		if(tetriminStuck){
		synchronized(Tetrimin.tetriminList){
			synchronized(Piece.allPiecesList){
			
			
				
				ArrayList<Piece> removePieceList = new ArrayList<Piece>();
				
			    int count=0;
				
				////check for lines
				for(int y =0 ; y<GamePanel.SIZE_Y/Piece.SQUARES_SIZE; y++){
					//System.out.print("Line "+y);
					for(int x =0 ; x<GamePanel.SIZE_X/Piece.SQUARES_SIZE; x++){
					
					   for(Piece piece: Piece.allPiecesList){
						   if(piece.positionY/Piece.SQUARES_SIZE==y && piece.positionX/Piece.SQUARES_SIZE==x){
							   count++;
						   }
					   }
					   
					   if(count>=GamePanel.SIZE_X/Piece.SQUARES_SIZE){
						   linesCounter++;
						   
						   if(enableBonusPiece && linesCounter%SPAWN_BONUS_SCORE==0){
							   spawnBonus=true;
						   }
						   					   
						   gameSpeed -= LINE_SPEED_INCREMENT;
						   savedGameSpeed -= LINE_SPEED_INCREMENT;
						   System.out.println(gameSpeed);
						   System.out.println(savedGameSpeed);
						   removeLinesList.add(y);
					   }
					   
				    }
					//System.out.println("   count: "+count);			
					count=0;
				}
				
				////remove full lines
				
				if(removeLinesList.size()==4){ ////////TETRIS
					linesCounter++;  //////SCORE
					showTetrisTxt = true;
				}
				else{
					showTetrisTxt=false;
				}
				
				for(int i=0; i< removeLinesList.size();i++){
					for(Piece piece: Piece.allPiecesList){
						if(piece.positionY/Piece.SQUARES_SIZE==removeLinesList.get(i)){
							removePieceList.add(piece);
							//tetriminList?
						}
					}
				}
				
				if(!removeLinesList.isEmpty()){
					
					for(int i=0;i<removeLinesList.size();i++){
					//System.out.println(removeLinesList.get(i));
					}
					
				Piece.allPiecesList.removeAll(removePieceList);
				
				for(Tetrimin tetrimin : Tetrimin.tetriminList){
				    tetrimin.piecesList.removeAll(removePieceList);
					
					for(Piece piece : tetrimin.piecesList){
						if(piece.positionY/Piece.SQUARES_SIZE <= removeLinesList.get(removeLinesList.size()-1)){
						   piece.setStuck(false);
						   //System.out.println("unstuck<="+removeLinesList.get(removeLinesList.size()-1));
						}
					}
			    }
				}				
			//}			
			}		
		}
		}
		tetriminStuck=false;
		
		
		//////////////////GAMEOVER
		for(Piece piece : Piece.allPiecesList){
			if(piece.stuck && piece.positionY<0){
				gameover = true;
			}	
		}
		
		if(gameover){
		highScore = FileRW.readHighScore();
				if(linesCounter>highScore){
					highScore = linesCounter;
					isHighScore=true;
					FileRW.writeHighScore(linesCounter);
				}
		Tetris.showPlayButton(true);
		}
		
		
		
		///////////
		
		gamePanel.repaint();
			
		}///////end of game Loop
		
		try{
			synchronized(this){
		         this.wait();
			}
		}catch(InterruptedException e){System.err.println(e);}
		
	}//
}
	
	public static void resetTimer(){	
	
	 gameover=false;
	 highScore=0;
	 isHighScore = false;	
	 bonusCounter=BONUS_TIME;	
	 spawnBonus=false;
     showBonusTxt = false;	
	 gameSpeed=MIN_GAME_SPEED;
	 currentMinGameSpeed=MIN_GAME_SPEED;	
	 savedGameSpeed=gameSpeed;
	 focusedTetrimin=null;
	 tetriminStuck=false;
	 removeLinesList.clear();
	 linesCounter=0;		
	 showTetrisTxt=false;
	 showNewGameTxt=true;
	
	}
	
	public void incrementSpeed(){
		gameSpeed+=100;
	}
	
	public void decrementSpawnTime(){
		spawnTime-=1000;
	}
	
	
	
}

class FileRW{
	
	static String mainDirPath = Tetris.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	static String resDirName="\\TetrisResources";
	static String highScoreName="\\Highscore.txt";
	
	static File path = new File(mainDirPath+resDirName+highScoreName);
	static boolean fixPathForJar=true;
	//static boolean runningFromJar=false;
	static String dirString="";
	
	
	static boolean isRunningFromJar(){
		boolean runningFromJar=false;
		
		String s = Tetris.class.getResource("Tetris.class").toString();
		System.out.println(s);
		if(s.startsWith("jar:")){
			runningFromJar=true;
		}
		
		return runningFromJar;
		
	}
	
	
	
	
	public static int readHighScore(){
		int highscore=0;
		
		if(isRunningFromJar() && fixPathForJar){
			System.out.println("isRunningFromJar ...");
			String s = path.toString();
			path = path.getParentFile().getParentFile().getParentFile();
			System.out.println(path.toString());
			dirString= path.toString();
			path = new File(path.toString()+resDirName+highScoreName);
			System.out.println(path.toString());
			System.out.println(".. isRunningFromJar fixed path");
			fixPathForJar=false;
		}
		
	try{
	      BufferedReader reader =new BufferedReader(new FileReader(path));
		  highscore = Integer.parseInt(reader.readLine());
		  reader.close();
	}catch(Exception e){
		System.out.println(e);
		if(e instanceof FileNotFoundException){
			try{
				if(isRunningFromJar()){
					File newDir = new File(dirString+resDirName);
					boolean success=false;
					//try{
					success = newDir.mkdirs();
					//}catch(Exception mkdirex){System.out.println("mkdir EXC"+mkdirex);}
					System.out.println("Created File "+ success + " : " + newDir.toString());
				}else{
					File newDir = new File(mainDirPath+resDirName);
					boolean success=false;
					//try{
					success = newDir.mkdirs();
					//}catch(Exception mkdirex){System.out.println("mkdir EXC"+mkdirex);}
					System.out.println("Created File "+ success + " : " + newDir.toString());
					
				}
			path.createNewFile();
			writeHighScore(0);
			System.out.println("Created File: "+ path.toString());
			}catch(Exception e1){System.out.println("mkdir: "+e1);}
		}
		}
	
		
			
			
			
		
		
		
		return highscore;
	}
	
	public static void writeHighScore(int highscore){
		
	try{
	      FileWriter writer = new FileWriter(path);
		  writer.write(Integer.toString(highscore));
		  writer.close();
	}catch(Exception e){System.out.println(e);}
		
			
	}
	
	
	
	
}