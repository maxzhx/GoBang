package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGobang extends JPanel{
	//GUI
	JFrame mainFrame;
	JTextField inputField=new JTextField(10);
	JTextArea outputArea=new JTextArea(10, 30);
	JScrollPane outputPane=new JScrollPane(outputArea);
	Container controlContainer=new Container();
	ChessBoard chessBoard=new ChessBoard();
	
	//network
	Socket clientSocket;
	ObjectInputStream inputStream;
	ObjectOutputStream outputStream;
	
	//game
	MyChessPoint myChessPoint[][]=new MyChessPoint[20][20];
	boolean moveFlag=false;
	
	ClientGobang(JFrame frame){
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
				displayMessage("Client >> "+inputField.getText());
				sendData("Client >> "+inputField.getText());
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
							setChess(i,j,2);
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
			if (flag==2) {
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
				if (myChessPoint[i][j].state==2) {
					if (chessJudge(i, j)) {
						return;
					}
				}
			}
		}
	}
	boolean chessJudge(int x,int y){
		if (y>=5&&myChessPoint[x][y-1].state==2&&myChessPoint[x][y-2].state==2
				&&myChessPoint[x][y-3].state==2&&myChessPoint[x][y-4].state==2) {
			win();
			return true;
		}
		else if (x>=5&&myChessPoint[x-1][y].state==2&&myChessPoint[x-2][y].state==2
				&&myChessPoint[x-3][y].state==2&&myChessPoint[x-4][y].state==2) {
			win();
			return true;
		}
		else if (y<=15&&myChessPoint[x][y+1].state==2&&myChessPoint[x][y+2].state==2
				&&myChessPoint[x][y+3].state==2&&myChessPoint[x][y+4].state==2) {
			win();
			return true;
		}
		else if (x<=15&&myChessPoint[x+1][y].state==2&&myChessPoint[x+2][y].state==2
				&&myChessPoint[x+3][y].state==2&&myChessPoint[x+4][y].state==2) {
			win();
			return true;
		}
		
		else if (y>=5&&x>=5&&myChessPoint[x-1][y-1].state==2&&myChessPoint[x-2][y-2].state==2
				&&myChessPoint[x-3][y-3].state==2&&myChessPoint[x-4][y-4].state==2) {
			win();
			return true;
		}
		else if (x<=15&&y<=15&&myChessPoint[x+1][y+1].state==2&&myChessPoint[x+2][y+2].state==2
				&&myChessPoint[x+3][y+3].state==2&&myChessPoint[x+4][y+4].state==2) {
			win();
			return true;
		}
		else if (y>=5&&x<=15&&myChessPoint[x+1][y-1].state==2&&myChessPoint[x+2][y-2].state==2
				&&myChessPoint[x+3][y-3].state==2&&myChessPoint[x+4][y-4].state==2) {
			win();
			return true;
		}
		else if (y<=15&&x>=5&&myChessPoint[x-1][y+1].state==2&&myChessPoint[x-2][y+2].state==2
				&&myChessPoint[x-3][y+3].state==2&&myChessPoint[x-4][y+4].state==2) {
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
	
	
	//联网的总体思路就是传递string,然后根据这个string的不同,来实现不同功能.
	public void runClient(){
		try {
			//这些代码是建立连接的 
			displayMessage( "Attempting connection" );
			clientSocket=new Socket(InetAddress.getByName("127.0.0.1"), 12345);
			outputStream=new ObjectOutputStream(clientSocket.getOutputStream());
			outputStream.flush();
			inputStream=new ObjectInputStream(clientSocket.getInputStream());
			displayMessage( "Connected to: " + 
			         clientSocket.getInetAddress().getHostName() );
			inputField.setEditable(true);//这句可以忽略 是我的程序里的 跟联网无关
			displayMessage("Connect Success!");
			displayMessage("You are WHITE!");
			String message;
			
			//建立成功以后会用下面这个循环不断接收信息
			do{
				message=(String)inputStream.readObject();
				//每次收到的信息会存在message这个string里 然后你再根据你自己的定义来处理这个string,刚开始你就直接输出它 看能不能连接上
				
				//下面这些就是我自己对string的处理 我用string的第一位来表示是什么信息 
				if (message.charAt(0)=='C'||message.charAt(0)=='S') {//如果是聊天的信息,就直接输出
					displayMessage(message);
				}
				else if (message.charAt(0)=='w') {//如果是胜负的信息, 就执行胜负的处理
					lose();
				}
				else {//其他的话就是走棋的信息 进行走棋子的处理 
					String s[]=message.split(",");
					setChess(Integer.parseInt(s[0]), Integer.parseInt(s[1]), 1);
				}
			}while(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//这个函数是用于输出各种信息的 你要是用控制台就system.out.printf(m);就得了
	void displayMessage(String m){		
		outputArea.append(m+'\n');
	}
	
	//这个是发送信息的 每次你要发送信息你就直接调用就得了 会传过去一个string
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
