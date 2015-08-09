package MainPane;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import Client.*;
import Server.*;
public class GobangTest extends JFrame{
	/**
	 * @param args
	 */
	GobangTest(){
		setVisible(true);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		new GobangTest();
		new GobangTest();
		Object[]   options   =   { "As server",   "As client" , "Exit"};
		 int   n   =   JOptionPane.showOptionDialog(null,
                 "Would you like start a game as server or as client?", 
                 "Gobang",
                 JOptionPane.YES_NO_CANCEL_OPTION , 
                 JOptionPane.QUESTION_MESSAGE, 
                 null, 
                 options,
                 options[0]);
//		 System.out.println(n);
		if (n==0) {
			new ServerGobangTest();
		}
		else if (n==1){
			new ClientGobangTest();
		}
		else {
			System.exit(0);
		}
	}
}
