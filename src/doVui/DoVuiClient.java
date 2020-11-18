package doVui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.JavaBean;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.io.File;

public class DoVuiClient extends JFrame implements MouseListener, MouseMotionListener {
	Socket socket;
	final static int start=0;
	final static int playing=1;
	final static int correct=2;
	final static int incorrect =3;
	final static int finish =4;
	final static int win=5;
	DataInputStream din;
	DataOutputStream dout;
	String question,a,b,c,d;
	int stt,state;
	int time;
	BufferedImage bufImg;
	Graphics g;
	int w = 500;
	int h = 650;
	Image background, play, question_img, play_button, dapan, correct_img, incorrect_img,win_img;
	Timer time_play;
	Color color_A,color_B,color_C,color_D;
	long currentFrame;
	Clip win_game,die_game,start_game,play_game;
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		new DoVuiClient();
	}
	public void playSound (Clip clip) 
	{ 
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		    clip.start();
		   
	}
	
	public void pauseSound(Clip clip) {
		this.currentFrame =  clip.getMicrosecondPosition(); 
		clip.stop();
		
	}
	public void stopSound(Clip clip) {
		this.currentFrame = 0L;
		clip.stop();
		clip.close();
		
	}
	public DoVuiClient() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		try {
			color_A = Color.yellow;
			color_B = Color.yellow;
			color_C = Color.yellow;
			color_D = Color.yellow;

			time_play=new Timer();
			stt = 0;
			time=20;
			state = start;
			question = "";
			a = "";
			b = "";
			c = "";
			d = "";
			this.socket = new Socket("localhost",7111);
			System.out.println("da ket noi");
			dout = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setTitle("DO VUI");
		this.setSize(w,h);
		this.setDefaultCloseOperation(3);
		this.setLayout(null);
		
        bufImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		g = bufImg.getGraphics();
        this.setVisible(true);
        this.addMouseListener(this);
        background = getToolkit().getImage("img\\background.png");
        play = getToolkit().getImage("img\\play.jpg");
        play_button = getToolkit().getImage("img\\play.png");
        question_img = getToolkit().getImage("img\\question.jpg");
        dapan = getToolkit().getImage("img\\dapan.jpg");
        correct_img = getToolkit().getImage("img\\correct.png");
        incorrect_img = getToolkit().getImage("img\\incorrect.png");
        win_img = getToolkit().getImage("img\\win.jpg");
        
        AudioInputStream audioInputStream1 =AudioSystem.getAudioInputStream(new File("sound\\win.wav").getAbsoluteFile());
	    win_game = AudioSystem.getClip();
	    win_game.open(audioInputStream1);
	    
	    AudioInputStream audioInputStream2 =AudioSystem.getAudioInputStream(new File("sound\\start.wav").getAbsoluteFile());
	    start_game = AudioSystem.getClip();
	    start_game.open(audioInputStream2);
	    
	    AudioInputStream audioInputStream3 =AudioSystem.getAudioInputStream(new File("sound\\die.wav").getAbsoluteFile());
	    die_game = AudioSystem.getClip();
	    die_game.open(audioInputStream3);
	    
	    AudioInputStream audioInputStream4 =AudioSystem.getAudioInputStream(new File("sound\\play.wav").getAbsoluteFile());
	    play_game = AudioSystem.getClip();
	    play_game.open(audioInputStream4);
	    
	    this.playSound(start_game);
        time_play.schedule(new TimerTask() {
			  @Override
			  public void run() {
			    if(state==playing && time>0) {
			    	time--;
			    	
			    }
			    if(time==0 && state==playing) {
			    	try {
						dout.writeUTF("time");
						answer();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    }
			  }

			
			},0,1000);
        
	}
	public void answer() {
		try {
			String t = din.readUTF();
			System.out.println(t);
			if(t.equals("true")&&stt<5) {
				state = correct;
				
				question = din.readUTF();
				a = din.readUTF();
				b = din.readUTF();
				c = din.readUTF();
				d = din.readUTF();
				
				this.pauseSound(play_game);
				
					
			}
			else if(t.equals("true")&&stt==5) {
				state = win;
				dout.close();
				din.close();
				socket.close();
				this.pauseSound(play_game);
			}
			else if(t.equals("false")) {
				state = incorrect;
				dout.close();
				din.close();
				socket.close();
				
				this.pauseSound(play_game);
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(x>230 && x<380 && y>450 && y<510 && state==start) {
			
			stt = 1;
			try {
				dout.writeUTF("play");
				dout.flush();
				question = din.readUTF();
				a = din.readUTF();
				b = din.readUTF();
				c = din.readUTF();
				d = din.readUTF();
				
				this.pauseSound(start_game);
				this.playSound(play_game);
					
			} catch (IOException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			state = playing;
			
		}
		else if(state==playing) {
			
			if(x>50 && x<210 && y>370 && y<440) {
				try {
					color_A = Color.red;
					dout.writeUTF("A");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>280 && x<440 && y>370 && y<440) {
				try {
					color_B = Color.red;
					dout.writeUTF("B");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>50 && x<210 && y>500 && y<570 ) {
				try {
					color_C = Color.red;
					dout.writeUTF("C");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>280 && x<440 && y>500 && y<570) {
				try {
					color_D = Color.red;
					dout.writeUTF("D");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void paint(Graphics g1) {
		if(state == start) {
			g.drawImage(correct_img, 150, 150,200,200, null);
			g.drawImage(incorrect_img, 150, 150,200,200, null);
			g.drawImage(win_img, 150, 150,200,200, null);
			g.drawImage(background, 0, 0,w,h, this);
			
			g.setColor(Color.yellow);
			g.setFont(new Font("UTF-8",Font.BOLD,45));
			g.drawString("FUNNY \nQUESTION", 50, 170);
//			g.getFontMetrics().getStringBounds(question, g).getWidth();
			
			
			
			g.drawImage(play_button, 230, 450,150,60, this);
			
			g1.drawImage(bufImg, 0, 0, w, h, null);
			repaint();
		}
		else if(state == playing){
			paintPlay(g1);
			g1.drawImage(bufImg, 0, 0, w, h, null);
			repaint();
		}
		else if(state == correct) {
			paintPlay(g1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.drawImage(play, 0, 0,w,h, this);
			g.drawImage(correct_img, 150, 150,200,200, this);
			
			g1.drawImage(bufImg, 0, 0, w, h, null);
			this.playSound(win_game);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			color_A=Color.yellow;
			color_B=Color.yellow;
			color_C=Color.yellow;
			color_D=Color.yellow;
			
			state = playing;
			
			time=20;
			stt++;
			
			this.pauseSound(win_game);
			this.playSound(play_game);
			
			repaint();
			
		}
		else if(state == incorrect) {
			paintPlay(g1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.drawImage(play, 0, 0,w,h, this);
			g.drawImage(incorrect_img, 150, 150,200,200, this);
			
			g.setColor(Color.black);
			g.setFont(new Font("Georgia",Font.BOLD,40));
			g.drawString("INCORRECT", 120, 300);
			
			state = finish;
			g1.drawImage(bufImg, 0, 0, w, h, null);
			this.playSound(die_game);
		}		
		else if(state == win) {
			paintPlay(g1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g.drawImage(play, 0, 0,w,h, this);
			g.drawImage(win_img, 50, 50,w-100,h-200, this);
			
			g.setColor(Color.GREEN);
			g.setFont(new Font("Georgia",Font.BOLD,30));
			g.drawString("WINNER", 170, 70);
			
			state = finish;
			g1.drawImage(bufImg, 0, 0, w, h, null);
			this.playSound(win_game);
		}
	}
	void drawString(Graphics g, String text, int x, int y) {
		String[] s = text.split(" ");
		int line = s.length/9;
		y= y-g.getFontMetrics().getHeight()*line/2;
		for(int i=0;i<s.length;i+=9) {
			String t = s[i];
			
			for(int j=1;j<9;j++) {
				if(i+j<s.length) {
					t = t.concat(" "+s[i+j]);
				}
			}
			g.drawString(t, x, y += g.getFontMetrics().getHeight());
		}
	  
	}
	public void paintPlay(Graphics g1) {
		g.drawImage(play, 0, 0,w,h, this);
		
		g.setColor(Color.PINK);
		g.setFont(new Font("Georgia",Font.BOLD,30));
		g.drawString("QUESTION", 150, 100);
		
		g.setColor(Color.yellow);
		g.setFont(new Font("arial",Font.BOLD,40));
		g.drawString(String.valueOf(time), 20,100);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial",Font.BOLD,40));
		g.drawString(String.valueOf(stt), 225, 155);
		
		g.drawImage(question_img, 50, 180,400,150, this);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,16));
		drawString(g,question,80,235);
			
		g.drawImage(dapan, 35, 355,190,100, this);
		g.setColor(color_A);
		g.fillRect(50, 370, 160, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("A. "+a,70,410);
			
		g.drawImage(dapan, 265, 355,190,100, this);
		g.setColor(color_B);
		g.fillRect(280, 370, 160, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("B. "+b,300,410);
		
		g.drawImage(dapan, 35, 485,190,100, this);
		g.setColor(color_C);
		g.fillRect(50, 500, 160, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("C. "+c,70,540);
			
		g.drawImage(dapan, 265, 485,190,100, this);
		g.setColor(color_D);
		g.fillRect(280, 500, 160, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("D. "+d,300,540);
		
		g1.drawImage(bufImg, 0, 0, w, h, null);	
		
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
