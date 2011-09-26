import javax.swing.JFrame;

import org.jfugue.Player;


public class GameOfLife
{
	public static void main(String[] args)
	{
		
		int nmax = 1;
		
		GOFrame[] gof = new GOFrame[nmax];
		for(int i = 0 ; i<nmax ; i++){
			gof[i] = new GOFrame();
			gof[i].setVisible(true);
			gof[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}
		System.out.println("end main");
	}
	
}
