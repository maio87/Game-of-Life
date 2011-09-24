import javax.swing.JFrame;

import org.jfugue.Player;


public class GameOfLife
{
	public static void main(String[] args)
	{
		
		GOFrame[] gof = new GOFrame[3];
		for(int i = 0 ; i<3 ; i++){
			gof[i] = new GOFrame();
			gof[i].setVisible(true);
			gof[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}
		System.out.println("end main");
	}
	
}
