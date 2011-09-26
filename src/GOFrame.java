import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfugue.Player;
import org.jfugue.Instrument;
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
	JButton bplay,breset,bnext,bsave,bopen,bcolor;
	RoundButton[][] mat;
	JColorChooser tcc = new JColorChooser();
	Instrument ins = new Instrument((byte) 0);
	JComboBox instrlist;
	String[] ins_list = new String[127]; 
	//petList.setSelectedIndex(4);


	boolean[][] old_mat;
	JPanel panel,p,pt;
	final int dim = 24;
	
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
	
	private Color buttoncolor  = new Color(255, 0, 0);
	private boolean selectedcolor = true;
	private int colorand = 0;
	static final Color buttonbkg  = new Color(2,2,2);
	static final Color windbkg  = new Color(2,2,2);

	JSlider sleep;
	JCheckBox toroButton;
	JFileChooser choose;

	
	
	public GOFrame()
	{
		for(int i=0;i<127;i++){
			ins_list[i]=ins.getInstrumentName();
			ins.setInstrument((byte) (i+1));
		}
		
		System.out.println("#ins "+ins_list.length);
		instrlist = new JComboBox(ins_list);
		instrlist.setSelectedIndex(0);

		System.out.println("selected "+instrlist.getSelectedItem());
		setLayout(new BorderLayout());
		setTitle("Game Of Life");
		setSize(600,650);
		centerFrame();
		panel = new JPanel();
		GridLayout gpanel = new GridLayout(dim,dim);
		panel.setLayout( gpanel );
		panel.setBackground(windbkg);
		p = new JPanel();
		p.setLayout(new GridLayout(1,5));
		pt = new JPanel(new GridLayout(3,5));
		mat = new RoundButton[dim][dim];
		
		 toroButton = new JCheckBox("toroidale");
		
		
		sleep = new JSlider(JSlider.HORIZONTAL,
	            RPS_MIN, RPS_MAX, RPS_INIT);

		//Turn on labels at major tick marks.
		sleep.setMajorTickSpacing(100);
		sleep.setMinorTickSpacing(1);
		sleep.setPaintTicks(true);
		sleep.setPaintLabels(true);
		Border insideBorder = BorderFactory.createRaisedBevelBorder();

		Border outsideBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED,buttonbkg,Color.WHITE);
		Border border = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
		for(int i=0; i<dim;i++)
			for(int j=0;j<dim;j++)
			{
				mat[i][j] = new RoundButton("",new Dimension(20,20));
				mat[i][j].setSelected(false);
				mat[i][j].setBackground(buttonbkg);
				mat[i][j].addActionListener( new BAction() );
				//mat[i][j].setBorder(border);
				
				panel.add(mat[i][j]);
			}
		
		bplay = new JButton(implay);
		bplay.setBackground(buttonbkg);
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
		bnext.setBackground(buttonbkg);
		bnext.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				
				prossimo_periodo();
			}
			
		});
		
		p.add(bnext);
		
		breset = new JButton(imreset);
		breset.setBackground(buttonbkg);
		breset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				
				for(int i=0; i<dim;i++)
					for(int j=0;j<dim;j++)
					{

						mat[i][j].setSelected(false);
						periodo =0;
						mat[i][j].setBackground(buttonbkg);
						vive=0;
						task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
					}
			}
			
		});
		
		p.add(breset);

		bcolor = new JButton("Choose Color");
		bcolor.setBackground(Color.LIGHT_GRAY);
		bcolor.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				//1. Create the frame.
				JFrame frame = new JFrame("Choose Color");

				//2. Optional: What happens when the frame closes?
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				//3. Create components and put them in the frame.
				//...create emptyLabel...
				frame.getContentPane().add(tcc, BorderLayout.CENTER);
				
				//4. Size the frame.
				frame.pack();

				//5. Show it.
				frame.setVisible(true);

			}
			
		});
		
		tcc.getSelectionModel().addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
			    buttoncolor = tcc.getColor();
			    selectedcolor = true;
			}
			
		});
		

		task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
		//task.setAlignmentY(Component.LEFT_ALIGNMENT);
		
		pt.add(task);
		pt.add(new JLabel("   "));
		pt.add(toroButton,BorderLayout.PAGE_END);
		
		pt.add(sleep,BorderLayout.EAST);
		pt.add(instrlist);

		pt.add(bcolor);
		
		bsave = new JButton(imsave);
		bsave.setBackground(buttonbkg);
	//	bsave.setPreferredSize(new Dimension(45,45));
		
		
		
		bsave.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
			    FileOutputStream fos = null;
			    ObjectOutputStream out = null;
			    String nome_file = "";
			    choose = new JFileChooser();
			    choose.setCurrentDirectory(new File("./"));
			    int n = choose.showSaveDialog(((JButton)e.getSource()).getParent());
			    if(n == JFileChooser.APPROVE_OPTION)
			    {

					System.out.print("SAVING...");
			    	nome_file = choose.getSelectedFile().toString();
				    String filename = new String(nome_file);
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
			    
			    

			}
			
		});
		bopen = new JButton(imopen);
		bopen.setBackground(buttonbkg);
	//	bopen.setPreferredSize(new Dimension(45,45));
		
		bopen.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				System.out.print("LOADING...");
				   FileInputStream fis = null;
				   ObjectInputStream in = null;
				   to_save app = null;
				   

				    choose = new JFileChooser();

				    choose.setCurrentDirectory(new File("./"));
				   int n = choose.showOpenDialog(((JButton)e.getSource()).getParent());
				    if(n == JFileChooser.APPROVE_OPTION)
				    {

					    String filename = choose.getSelectedFile().toString() ;
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
												mat[i][j].setBackground(buttoncolor);
												vive++;
											}
											else
											{
												mat[i][j].setBackground(buttonbkg);
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

				((JButton)e.getSource()).setBackground(getNextColor());
				vive++;
				task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
			}
			else
			{
				((JButton)e.getSource()).setSelected(false);

				((JButton)e.getSource()).setBackground(buttonbkg);
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
					if(toroButton.isSelected()){
						if(old_mat2[realPos(x)][realPos(y)]){
							ris++;
						}	
					}
					else{
						if(x>=0 && y >=0 && x < dim && y < dim)
						if(old_mat2[x][y]){
							ris++;
						}
					}
									
				}
				
			}
		}
		return ris;
		
	}
	
	public boolean[][] getMat() {
		boolean[][] ret_mat= new boolean [this.dim][this.dim];
		for(int i=0; i<dim; i++)
			for(int j=0; j<dim; j++)
			{
				ret_mat[i][j] = mat[i][j].isSelected();
				
			}
		return ret_mat;
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
					
					mat[i][j].setBackground(getNextColor());
					vive++;
				}
				else if ( mat[i][j].isSelected() &&( Adiacenze(old_mat,i,j)<2 || Adiacenze(old_mat,i,j)>3 ) )
				{

					mat[i][j].setSelected(false);
					mat[i][j].setBackground(buttonbkg);
					vive--;
				}
			}
		}
		periodo ++ ;	
		if(selectedcolor){
			colorand = 0;
			selectedcolor=false;
		}
		else{
			colorand++;
		}
		task.setText(" vive: "+vive+"     morte: "+(dim*dim-vive)+"     periodo: "+periodo);
	
	
		int len;
		
		String strplay = "I["+ins_list[instrlist.getSelectedIndex()]+"] T[Presto] ";
		String startstr = strplay;
		for(int i=0; i<dim; i++){
			for(int j=0; j<dim; j++)
			{
				if(mat[i][j].isSelected() ){//&& !strplay.contains(note[i%note.length])){

				//	System.out.println(strplay+" <-strp  "+note[i%7]);
					strplay= strplay + note[i%7]+j%10+ "q+";
					
				}
			}
			if((len=strplay.length())!=0){
			 
			//System.out.println("gen: "+strplay);
			strplay=strplay.substring(0,len-1);

			//System.out.println("gen2: "+strplay);
			strplay += " ";
			}

		}

		if(!strplay.equals(startstr)){
			len=strplay.length();
			strplay=strplay.substring(0,len-1);
			//System.out.println("genf: "+strplay+"$");
			PlaySound p = new PlaySound(strplay);
			Thread t = new Thread(p);
			
			t.start();
			
		}
		
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
	
// 70B 70S %360H
	public Color getNextColor(){
		
		
		float[] hscolor= new float[3];
		Color.RGBtoHSB(buttoncolor.getRed(), buttoncolor.getGreen(), buttoncolor.getBlue(), hscolor);
		//System.out.println("colorand "+colorand+"   "+hscolor[0]);
		return (Color.getHSBColor((((float)colorand%100)/100)+hscolor[0], 0.7F, 0.7F));
	}
	
	
	Player player = new Player();
	
	String[] note = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

	private class PlaySound implements Runnable
	{
		String toPlay;
		PlaySound(String str){
			this.toPlay = str;
		}
		public void run()
		{
				player.play(this.toPlay);
		}
	}

	public class RoundButton extends JButton {
		  public RoundButton(String label, Dimension d) {
		    super(label);

		// These statements enlarge the button so that it 
		// becomes a circle rather than an oval.
		    Dimension size = d;
		    size.width = size.height = Math.max(size.width, 
		      size.height);
		    setPreferredSize(size);

		// This call causes the JButton not to paint 
		   // the background.
		// This allows us to paint a round background.
		    setContentAreaFilled(false);
		  }

		// Paint the round background and label.
		  protected void paintComponent(Graphics g) {
		    if (getModel().isArmed()) {
		// You might want to make the highlight color 
		   // a property of the RoundButton class.
		      g.setColor(Color.lightGray);
		    } else {
		      g.setColor(getBackground());
		    }
		    g.fillOval(0, 0, getSize().width-1, 
		      getSize().height-1);

		// This call will paint the label and the 
		   // focus rectangle.
		    super.paintComponent(g);
		  }

		// Paint the border of the button using a simple stroke.
		  protected void paintBorder(Graphics g) {
		    g.setColor(getForeground());
		    g.drawOval(0, 0, getSize().width-1, 
		      getSize().height-1);
		  }

		// Hit detection.
		  Shape shape;
		  public boolean contains(int x, int y) {
		// If the button has changed size, 
		   // make a new shape object.
		    if (shape == null || 
		      !shape.getBounds().equals(getBounds())) {
		      shape = new Ellipse2D.Float(0, 0, 
		        getWidth(), getHeight());
		    }
		    return shape.contains(x, y);
		  }
	}
}
