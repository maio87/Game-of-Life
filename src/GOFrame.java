import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


class to_save implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean[][] save_mat;
	
	public boolean[][] getSave_mat() {
		return save_mat;
	}

	to_save(JButton[][] mat,int dim){
		save_mat = new boolean[dim][dim];
		for(int i=0; i<dim; i++)
			for(int j=0; j<dim; j++)
			{
				save_mat[i][j] = mat[i][j].isSelected();
				
			}
		
	}
	
}


public class GOFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	
	Thread t;
	JButton bplay,breset,bnext,bsave,bopen;
	JButton[][] mat;
	boolean[][] old_mat;
	JPanel panel,p,pt;
	final int dim = 30;
	
	JLabel task = new JLabel();
	ImageIcon implay = new ImageIcon("image/media-playback-start.png"); 
	ImageIcon impause = new ImageIcon("image/media-playback-pause.png"); 
	ImageIcon imreset = new ImageIcon("image/player_stop.png"); 
	ImageIcon imnext = new ImageIcon("image/gtk-goto-last-ltr.png");

	ImageIcon imsave = new ImageIcon("image/gtk-save.png");

	ImageIcon imopen = new ImageIcon("image/document-open.png");
	int vive =0;
	int periodo =0;
	static final int RPS_MIN = 50;
	static final int RPS_MAX = 500;
	static final int RPS_INIT = 150;    //initial frames per second

	JSlider sleep;


	public GOFrame()
	{
		
		setLayout(new BorderLayout());
		setTitle("Game Of Life");
		setSize(800,600);
		centerFrame();
		
		panel = new JPanel();
		panel.setLayout( new GridLayout(dim,dim) );
		p = new JPanel();
		p.setLayout(new GridLayout(1,5));
		pt = new JPanel(new BorderLayout());
		mat = new JButton[dim][dim];
		
		
		sleep = new JSlider(JSlider.HORIZONTAL,
	            RPS_MIN, RPS_MAX, RPS_INIT);

		//Turn on labels at major tick marks.
		sleep.setMajorTickSpacing(100);
		sleep.setMinorTickSpacing(1);
		sleep.setPaintTicks(true);
		sleep.setPaintLabels(true);
		
		for(int i=0; i<dim;i++)
			for(int j=0;j<dim;j++)
			{
				mat[i][j] = new JButton();
				mat[i][j].setSelected(false);
				mat[i][j].setBackground(Color.WHITE);
				mat[i][j].addActionListener( new BAction() );
				panel.add(mat[i][j]);
			}
		
		bplay = new JButton(implay);
		bplay.setBackground(Color.WHITE);
		bplay.addActionListener(new ActionListener(){
		
			public void actionPerformed(ActionEvent e)
			{
				if(!bplay.isSelected()){
					((JButton)e.getSource()).setIcon(impause);
					Run r = new Run();
					t = new Thread(r);
					t.start();
					bplay.setOpaque(false);
					bplay.setSelected(true);
				}
				else{
					((JButton)e.getSource()).setIcon(implay);

					t.interrupt();
					bplay.setOpaque(true);
					bplay.setSelected(false);
				}
				
			}
			
		});
		
		p.add(bplay);
		
		bnext = new JButton(imnext);
		bnext.setBackground(Color.WHITE);
		bnext.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				
				prossimo_periodo();
			}
			
		});
		
		p.add(bnext);
		
		breset = new JButton(imreset);
		breset.setBackground(Color.WHITE);
		breset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				
				for(int i=0; i<dim;i++)
					for(int j=0;j<dim;j++)
					{

						mat[i][j].setSelected(false);
						periodo =0;
						mat[i][j].setBackground(Color.WHITE);
						vive=0;
						task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
					}
			}
			
		});
		
		p.add(breset);
		

		task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
		//task.setAlignmentY(Component.LEFT_ALIGNMENT);
		pt.add(task);
		pt.add(sleep,BorderLayout.EAST);
		bsave = new JButton(imsave);
		bsave.setBackground(Color.WHITE);
	//	bsave.setPreferredSize(new Dimension(45,45));
		
		
		
		bsave.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				System.out.print("SAVING...");
			    FileOutputStream fos = null;
			    ObjectOutputStream out = null;
			    String filename = new String("out.txt");
			    try
			         {
			           fos = new FileOutputStream(filename);
			           out = new ObjectOutputStream(fos);
			           out.writeObject(new to_save(mat,dim));
			           out.close();
			         }
			         catch(IOException ex)
			         {
			          ex.printStackTrace();
			         }				

				System.out.println("DONE");
			}
			
		});
		bopen = new JButton(imopen);
		bopen.setBackground(Color.WHITE);
	//	bopen.setPreferredSize(new Dimension(45,45));
		
		bopen.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				System.out.print("LOADING...");
				   FileInputStream fis = null;
				   ObjectInputStream in = null;
				   to_save app = null;
				    String filename = new String("out.txt");
				   try
				   {
				     fis = new FileInputStream(filename);
				     in = new ObjectInputStream(fis);
				     app = (to_save)in.readObject();
				     in.close();
				     boolean[][]saved_mat=app.getSave_mat();
				     vive = 0;
				     for(int i=0; i<dim; i++)
							for(int j=0; j<dim; j++)
							{
								  mat[i][j].setSelected(saved_mat[i][j]);

									if (mat[i][j].isSelected()){
										mat[i][j].setBackground(Color.RED);
										vive++;
									}
									else
									{
										mat[i][j].setBackground(Color.WHITE);
									}

							}
					
				     
				 	task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
					 
				   }
				   catch(IOException ex)
				   {
				     ex.printStackTrace();
				   }
				   catch(ClassNotFoundException ex)
				   {
				     ex.printStackTrace();
				   }
				   
				   
				System.out.println("DONE");
			}
			
		});
		
		
		p.add(bopen);
		p.add(bsave);
		add(panel,BorderLayout.CENTER);
		add(p,BorderLayout.SOUTH);
		add(pt,BorderLayout.NORTH);
		
		
	}
	
	private class BAction implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			if(!((JButton)e.getSource()).isSelected())
			{

				((JButton)e.getSource()).setSelected(true);

				((JButton)e.getSource()).setBackground(Color.RED);
				vive++;
				task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
			}
			else
			{
				((JButton)e.getSource()).setSelected(false);

				((JButton)e.getSource()).setBackground(Color.WHITE);
				vive--;
				task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
			}
		}
		
	}

	public void centerFrame()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
		Dimension frameSize = getSize();
		setLocation ((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
	
	public int realPos(int i)
	{
		if (i < 0) return dim-1 ;
		if (i > dim-1) return 0;
		else return i;
	}
	
	public int Adiacenze(boolean[][] old_mat2, int i, int j)
	{
		
		int ris = 0;
		for(int x = i-1; x < i +2 ;x++){
			for(int y = j-1; y < j +2 ;y++){
				if(x != i || y != j){
					if(old_mat2[realPos(x)][realPos(y)]){
						ris++;
					}
				}
				
			}
		}
		return ris;
		
	}
	
	private void prossimo_periodo(){
		old_mat = new boolean[dim][dim];
		for(int i=0; i<dim; i++)
			for(int j=0; j<dim; j++)
			{
				old_mat[i][j] = mat[i][j].isSelected();
				
			}
		
		for(int i=0; i<dim; i++)
		{
			for(int j=0; j<dim; j++)
			{
				
				if ( !mat[i][j].isSelected() && (Adiacenze(old_mat,i,j) == 3) ){
					mat[i][j].setSelected(true);
					mat[i][j].setBackground(Color.RED);
					vive++;
				}
				else if ( mat[i][j].isSelected() &&( Adiacenze(old_mat,i,j)<2 || Adiacenze(old_mat,i,j)>3 ) )
				{

					mat[i][j].setSelected(false);
					mat[i][j].setBackground(Color.WHITE);
					vive--;
				}
			}
		}
		periodo ++ ;
		task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
		

		
	}
	
	
	private class Run implements Runnable
	{
		public void run()
		{
			while(true){
				prossimo_periodo();			
				try {
					Thread.sleep(sleep.getValue());
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	
}
