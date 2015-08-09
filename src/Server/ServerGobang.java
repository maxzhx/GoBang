	package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGobang extends JPanel{
	//GUI
	JFrame mainFrame;
	JTextField inputField=new JTextField(10);
	JTextArea outputArea=new JTextArea(10, 30);
	JScrollPane outputPane=new JScrollPane(outputArea);
	Container controlContainer=new Container();
	ChessBoard chessBoard=new ChessBoard();
	
	//network
	Socket serverSocket;
	ObjectInputStream inputStream;
	ObjectOutputStream outputStream;
	ServerSocket network;
	int connectionCounter=0;
	
	//game
	MyChessPoint myChessPoint[][]=new MyChessPoint[20][20];
	boolean moveFlag=false;
	
	ServerGobang(JFrame frame){
		mainFrame=frame;
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				myChessPoint[i][j]=new MyChessPoint();
			}
		}
		setLayout(new BorderLayout());
		inputField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				displayMessage("Server >> "+inputField.getText());
				sendData("Server >> "+inputField.getText());
				inputField.setText("");
			}
		});
		
		inputField.setEditable(false);
		outputArea.setEditable(false);
		controlContainer.setLayout(new BorderLayout());
		controlContainer.add(inputField,BorderLayout.NORTH);
		controlContainer.add(outputPane,BorderLayout.CENTER);
		add(controlContainer,BorderLayout.CENTER);
		add(chessBoard,BorderLayout.WEST);
	}
	
	class MyChessPoint extends Point{
		public int state=0;
		//0 for empty,1 for black, 2 for white
		MyChessPoint(){
			state=0;
		}
	}
	
	class ChessBoard extends JPanel{
		int locX,locY;
		ChessBoard(){
			setBackground(Color.PINK);
			setPreferredSize(new Dimension(600, 600));
			addMouseListener(mouseAdapter);
		}
		
		MouseAdapter mouseAdapter=new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
//				System.out.println(e.getX()+" "+e.getY());
				if (moveFlag) {
					locX=e.getX();locY=e.getY();
					if (locX>15&&locX<30*19+15&&locY>15&&locY<30*19+15) {
						int i=0,j=0;
						int tempX,tempY;
						for (i = 1; i<20 ; i++) {
							if (Math.abs(locX-30*i)<12) {
								break;
							}
						}
//						System.out.println(i);
						tempX=i*30;
						for (j = 1; j<20 ; j++) {
							if (Math.abs(locY-30*j)<12) {
								break;
							}
						}
//						System.out.println(j);
						if (i!=20&&j!=20) {
							setChess(i,j,1);
						}
					}
				}
			};
		};
		
		@Override
		
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			for (int i = 1; i*30<this.getSize().width ; i++) {
				g.drawLine(30, i*30, this.getSize().width-30, i*30);
			}
			for (int i = 1; i*30<this.getSize().height ; i++) {
				g.drawLine(i*30, 30, i*30, this.getSize().width-30);
			}
			for (int i = 1; i < 20; i++) {
				for (int j = 1; j < 20; j++) {
					if (myChessPoint[i][j].state==1) {
						g.setColor(Color.BLACK);
						g.fillOval(i*30-14, j*30-14, 28, 28);
					}
					else if(myChessPoint[i][j].state==2){
						g.setColor(Color.WHITE);
						g.fillOval(i*30-14, j*30-14, 28, 28);
					}
				}
			}
		}
	}
	
	boolean setChess(int x,int y, int flag){
		if (myChessPoint[x][y].state==0) {
			myChessPoint[x][y].state=flag;
			moveFlag=!moveFlag;
			chessBoard.repaint();
			if (flag==1) {
				judge();
				sendData(x+","+y);
			}
			return true;
		}
		return false;
	}
	
	void judge(){
		for (int i = 1; i < myChessPoint.length; i++) {
			for (int j = 1; j < myChessPoint.length; j++) {
				if (myChessPoint[i][j].state==1) {
					if (chessJudge(i, j)) {
						return;
					}
				}
			}
		}
	}
	boolean chessJudge(int x,int y){
		if (y>=5&&myChessPoint[x][y-1].state==1&&myChessPoint[x][y-2].state==1
				&&myChessPoint[x][y-3].state==1&&myChessPoint[x][y-4].state==1) {
			win();
			return true;
		}
		else if (x>=5&&myChessPoint[x-1][y].state==1&&myChessPoint[x-2][y].state==1
				&&myChessPoint[x-3][y].state==1&&myChessPoint[x-4][y].state==1) {
			win();
			return true;
		}
		else if (y<=15&&myChessPoint[x][y+1].state==1&&myChessPoint[x][y+2].state==1
				&&myChessPoint[x][y+3].state==1&&myChessPoint[x][y+4].state==1) {
			win();
			return true;
		}
		else if (x<=15&&myChessPoint[x+1][y].state==1&&myChessPoint[x+2][y].state==1
				&&myChessPoint[x+3][y].state==1&&myChessPoint[x+4][y].state==1) {
			win();
			return true;
		}
		
		else if (y>=5&&x>=5&&myChessPoint[x-1][y-1].state==1&&myChessPoint[x-2][y-2].state==1
				&&myChessPoint[x-3][y-3].state==1&&myChessPoint[x-4][y-4].state==1) {
			win();
			return true;
		}
		else if (x<=15&&y<=15&&myChessPoint[x+1][y+1].state==1&&myChessPoint[x+2][y+2].state==1
				&&myChessPoint[x+3][y+3].state==1&&myChessPoint[x+4][y+4].state==1) {
			win();
			return true;
		}
		else if (y>=5&&x<=15&&myChessPoint[x+1][y-1].state==1&&myChessPoint[x+2][y-2].state==1
				&&myChessPoint[x+3][y-3].state==1&&myChessPoint[x+4][y-4].state==1) {
			win();
			return true;
		}
		else if (y<=15&&x>=5&&myChessPoint[x-1][y+1].state==1&&myChessPoint[x-2][y+2].state==1
				&&myChessPoint[x-3][y+3].state==1&&myChessPoint[x-4][y+4].state==1) {
			win();
			return true;
		}
		return false;
	}
	
	void win(){
		JOptionPane.showMessageDialog(null, "You win!");
		sendData("win");
	}
	
	void lose(){
		JOptionPane.showMessageDialog(null, "You lose!");
	}
	
	
	public void runServer(){
		try {
			network=new ServerSocket(12345,100);
//			waitForConnection();
			displayMessage("Waiting for connection..");
			serverSocket=network.accept();
			//server建立以后 会停在这里 等待client连接  
			
			moveFlag=true;
			inputField.setEditable(true);
			outputStream=new ObjectOutputStream(serverSocket.getOutputStream());
			outputStream.flush();
			inputStream=new ObjectInputStream(serverSocket.getInputStream());
			displayMessage( "Connection " + (++connectionCounter) + " received from: " +
			         serverSocket.getInetAddress().getHostName() );
			displayMessage("Connect Success!");
			displayMessage("You are BLACK!");
			String message;
			do{
				message=(String)inputStream.readObject();
				if (message.charAt(0)=='C'||message.charAt(0)=='S') {
					displayMessage(message);
				}
				else if (message.charAt(0)=='w') {
					lose();
				}
				else {
					String s[]=message.split(",");
					setChess(Integer.parseInt(s[0]), Integer.parseInt(s[1]), 2);
				}
			}while(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void displayMessage(String m){		
		outputArea.append(m+'\n');
	}
	void sendData(String m){
		try {
			outputStream.writeObject(m);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}