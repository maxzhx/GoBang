package Server;

import javax.swing.JFrame;

import MainPane.GobangTest;

public class ServerGobangTest extends JFrame{
	public ServerGobang serverGobang=new ServerGobang(this);
	public ServerGobangTest(){
		super("Server");
		add(serverGobang);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		serverGobang.runServer();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServerGobangTest();
//		app.serverGobang.runServer();
	}
}
