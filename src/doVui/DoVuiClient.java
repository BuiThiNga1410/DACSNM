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
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
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
	DataInputStream din;
	DataOutputStream dout;
	String question,a,b,c,d;
	int stt,state;
	int time;
	BufferedImage bufImg;
	Graphics g;
	int w = 500;
	int h = 600;
	Image background, play;
	Timer time_play;
	public static void main(String[] args)  {
	 new DoVuiClient();
		
	}
	public void playSound () 
	{ 
		try{
		     AudioInputStream audioInputStream =AudioSystem.getAudioInputStream(new File("E:\\hi.wav").getAbsoluteFile());
		     Clip clip = AudioSystem.getClip();
		     clip.open(audioInputStream);
		     clip.start( );
		    }
		   catch(Exception ex)
		   {  
			   System.out.println(ex);
		   }
	}
	
	
	public DoVuiClient() {
		try {
			time_play=new Timer();
//			this.playSound();
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
        background = getToolkit().getImage("D:\\background.jpg");
        play = getToolkit().getImage("D:\\play.jpg");
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
			if(t.equals("true")) {
				stt++;
				state = correct;
				time=20;
				
				question = din.readUTF();
				a = din.readUTF();
				b = din.readUTF();
				c = din.readUTF();
				d = din.readUTF();
							
			}
			else if(t.equals("false")) {
				state = incorrect;
				dout.close();
				din.close();
				socket.close();
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
		if(x>250 && x<400 && y>350 && y<430 && state==start) {
			
			stt = 1;
			try {
				dout.writeUTF("play");
				dout.flush();
				question = din.readUTF();
				a = din.readUTF();
				b = din.readUTF();
				c = din.readUTF();
				d = din.readUTF();
				
					
			} catch (IOException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			state = playing;
			
		}
		else if(state==playing) {
			
			if(x>80 && x<250 && y>330 && y<400) {
				try {
					dout.writeUTF("A");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>255 && x<425 && y>330 && y<400) {
				try {
					dout.writeUTF("B");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>80 && x<250 && y>410 && y<480 ) {
				try {
					dout.writeUTF("C");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>255 && x<425 && y>410 && y<480) {
				try {
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
			g.drawImage(background, 0, 0,w,h, this);
			g.setColor(Color.red);
			g.setFont(new Font("Georgia",Font.BOLD,30));
			g.drawString("DO VUI", 200, 100);
			
			g.setColor(Color.yellow);
			g.fillOval(250, 350, 150, 80);
			g.setColor(Color.green);
			g.setFont(new Font("arial",Font.BOLD,25));	
			g.drawString("PLAY", 280, 400);
		}
		else if(state == playing){
			
			paintPlay(g1);
			
		}
		else if(state == correct) {
			g.drawImage(background, 0, 0,w,h, this);
			g.setColor(Color.red);
			g.setFont(new Font("Georgia",Font.BOLD,30));
			g.drawString("CORRECT", 200, 300);
			state = playing;
		}
		else if(state == incorrect) {
			g.drawImage(background, 0, 0,w,h, this);
			g.setColor(Color.red);
			g.setFont(new Font("Georgia",Font.BOLD,30));
			g.drawString("INCORRECT", 200, 300);
			state = finish;
		}
		
		g1.drawImage(bufImg, 0, 0, w, h, null);
		
		repaint();
		
	}
	public void paintPlay(Graphics g1) {
	
		g.drawImage(play, 0, 0,w,h, this);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Georgia",Font.BOLD,30));
		g.drawString("QUESTION", 150, 100);
		
		g.setColor(Color.yellow);
		g.setFont(new Font("Georgia",Font.BOLD,40));
		g.drawString(String.valueOf(time), 10,100);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Georgia",Font.BOLD,40));
		g.drawString(String.valueOf(stt), 225, 155);
		
		g.setColor(Color.blue);
		g.fillRect(80, 200, 345, 100);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString(question,100,250);
			
		g.setColor(Color.yellow);
		g.fillRect(80, 330, 170, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("A. "+a,100,360);
			
		g.setColor(Color.yellow);
		g.fillRect(255, 330, 170, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("B. "+b,280,360);
			
		g.setColor(Color.yellow);
		g.fillRect(80, 410, 170, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("C. "+c,100,440);
			
		g.setColor(Color.yellow);
		g.fillRect(255, 410, 170, 70);
		g.setColor(Color.white);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		g.drawString("D. "+d,280,440);
		
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