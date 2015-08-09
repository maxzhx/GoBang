package Client;

import javax.swing.JFrame;

import Server.ServerGobangTest;

public class ClientGobangTest extends JFrame{
	public ClientGobangTest(){
		super("Client");
		ClientGobang clientGobang=new ClientGobang(this);
		add(clientGobang);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		clientGobang.runClient();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ClientGobangTest();
//		app.serverGobang.runServer();
	}
}