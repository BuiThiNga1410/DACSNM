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
import java.util.Random;
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

public class DoVuiClient extends JFrame implements MouseListener {
	Socket socket;
	final static int start=0;
	final static int playing=1;
	final static int correct=2;
	final static int incorrect =3;
	final static int finish =4;
	final static int win=5;
	Random random;
	int id_image;
	DataInputStream din;
	DataOutputStream dout;
	String question,a,b,c,d;
	int stt,state,time;
	BufferedImage bufImg;
	Graphics g;
	boolean volume;
	int w = 500, h = 650;
	Image background, play, question_img, play_button, dapan,win_img,word,exit,volume_btn,again;
	Timer time_play;
	ArrayList<Image>correct_img;
	ArrayList<Image>incorrect_img;
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
			color_A = Color.black;
			color_B = Color.black;
			color_C = Color.black;
			color_D = Color.black;
			volume = true;
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
        play = getToolkit().getImage("img\\background2.jpg");
        play_button = getToolkit().getImage("img\\play.png");
        question_img = getToolkit().getImage("img\\question1.png");
        dapan = getToolkit().getImage("img\\answer1.png");
        win_img = getToolkit().getImage("img\\win.png");
        word = getToolkit().getImage("img\\word.png");
        exit = getToolkit().getImage("img\\exit.png");
        again = getToolkit().getImage("img\\again.png");
        volume_btn = getToolkit().getImage("img\\VolumeNormalBlue.png");
        
        correct_img = new ArrayList<Image>();
        correct_img.add(getToolkit().getImage("img\\1.png"));
        correct_img.add(getToolkit().getImage("img\\2.png"));
        correct_img.add(getToolkit().getImage("img\\3.png"));
        correct_img.add(getToolkit().getImage("img\\4.png"));
        correct_img.add(getToolkit().getImage("img\\5.png"));
        correct_img.add(getToolkit().getImage("img\\6.png"));
        
        incorrect_img= new ArrayList<Image>();
        incorrect_img.add(getToolkit().getImage("img\\7.png"));
        incorrect_img.add(getToolkit().getImage("img\\8.png"));
        incorrect_img.add(getToolkit().getImage("img\\9.png"));
        incorrect_img.add(getToolkit().getImage("img\\10.png"));
        
        random = new Random();
        
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
				
//				question = din.readUTF();
//				a = din.readUTF();
//				b = din.readUTF();
//				c = din.readUTF();
//				d = din.readUTF();
				
				this.pauseSound(play_game);
				
					
			}
			else if(t.equals("true")&&stt==5) {
				state = correct;
				this.pauseSound(play_game);
			}
			else if(t.equals("false")) {
				state = incorrect;
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
		if(state != start && state != finish && x>400 && x<460 && y>60 && y<120) {
			volume = !volume;
			if(volume) {
				volume_btn = getToolkit().getImage("img\\VolumeNormalBlue.png");
				if (state==playing) playSound(play_game);
				else if(state == correct || state ==win) playSound(win_game);
			}
			
			else {
				pauseSound(play_game);
				pauseSound(win_game);
				volume_btn = getToolkit().getImage("img\\mute1.png");
			}
		}
		else if((state==start && x>50 && x<110 && y>350 && y<410) || (x>10 && x<70 && y>50 && y<110 && (state==finish || state==win)) ) {
			
			try {
				dout.writeUTF("stop");
				dout.flush();
				dout.close();
				din.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			state = finish;
			System.exit(1);
			
		}
		else if((state==finish || state==win) && x>430 && x<490 && y>50 && y<110) {
			volume = !volume;
			if(volume) {
				if(state==finish)playSound(die_game);
				else playSound(win_game);
				volume_btn = getToolkit().getImage("img\\VolumeNormalBlue.png");
			}
			
			else {
				if(state==finish)pauseSound(die_game);
				else pauseSound(win_game);
				volume_btn = getToolkit().getImage("img\\mute1.png");
			}
		}
		else if(state==start && x>390 && x<450 && y>350 && y<410 ) {
			volume = !volume;
			if(volume) {
				playSound(start_game);
				volume_btn = getToolkit().getImage("img\\VolumeNormalBlue.png");
			}
			
			else {
				pauseSound(start_game);
				volume_btn = getToolkit().getImage("img\\mute1.png");
			}
		}
		else if((x>200 && x<350 && y>500 && y<560 && state==start) || (x>200 && x<350 && y>500 && y<560 && (state==finish || state==win))) {
			time = 20;
			stt = 1;
			try {
				dout.writeUTF("play");
				dout.flush();
				question = din.readUTF();
				a = din.readUTF();
				b = din.readUTF();
				c = din.readUTF();
				d = din.readUTF();
				
				if(volume) {
					this.pauseSound(start_game);
					this.pauseSound(die_game);
					this.pauseSound(win_game);
					this.playSound(play_game);
				}
					
			} catch (IOException e1) {
					// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			state = playing;
			
		}
		else if(state==playing) {
			
			if(x>30 && x<240 && y>355 && y<455) {
				try {
					color_A = Color.white;
					dout.writeUTF("A");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>260 && x<470 && y>355 && y<455) {
				try {
					color_B = Color.white;
					dout.writeUTF("B");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>30 && x<240 && y>485 && y<585 ) {
				try {
					color_C = Color.white;
					dout.writeUTF("C");
					dout.flush();
					answer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(x>260 && x<470 && y>485 && y<585) {
				try {
					color_D = Color.white;
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
			for(int i=0;i<correct_img.size();i++) {
				g.drawImage(correct_img.get(i), 150, 150,1,1, null);
			}
			for(int i=0;i<incorrect_img.size();i++) {
				g.drawImage(incorrect_img.get(i), 150, 150,1,1, null);
			}
			g.drawImage(win_img, 150, 150,1,1, null);
			g.drawImage(word, 150, 150,200,200, null);
			g.drawImage(again, 50, 350,0,0, null);
			g.drawImage(background, 0, 0,w,h, this);
				
			g.drawImage(play_button, 200, 500,150,60, this);	
			
			g.drawImage(exit, 50, 350,60,60, this);
			g.drawImage(volume_btn, 390, 350,60,60, this);
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
			if(stt==5) {
				state = win;
				color_A=Color.black;
				color_B=Color.black;
				color_C=Color.black;
				color_D=Color.black;
				if(volume)this.playSound(win_game);
				g1.drawImage(bufImg, 0, 0, w, h, null);
				repaint();
			}
			else {
				try {
					question = din.readUTF();
					a = din.readUTF();
					b = din.readUTF();
					c = din.readUTF();
					d = din.readUTF();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				id_image = random.nextInt(correct_img.size());
				g.drawImage(correct_img.get(id_image), 0, 0,w,h, this);
				g1.drawImage(bufImg, 0, 0, w, h, null);
				if(volume)this.playSound(win_game);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				color_A=Color.black;
				color_B=Color.black;
				color_C=Color.black;
				color_D=Color.black;
				
				state = playing;
				
				time=20;
				stt++;
				if(volume) {
					this.pauseSound(win_game);
					this.playSound(play_game);
				}
				repaint();	
			}
		}
		else if(state == incorrect) {
			paintPlay(g1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			state = finish;
			color_A=Color.black;
			color_B=Color.black;
			color_C=Color.black;
			color_D=Color.black;
			id_image = random.nextInt(incorrect_img.size());
			g1.drawImage(bufImg, 0, 0, w, h, null);
			if(volume)this.playSound(die_game);
			repaint();
		}		
		else if(state == finish) {
			g.drawImage(play, 0, 0,w,h, this);
			g.drawImage(word, 50, 50,400,60, this);
			g.drawImage(incorrect_img.get(id_image),0,0,w,h, this);
			g.drawImage(again, 200, 500,150,60, this);	
			
			g.drawImage(exit, 10, 50,60,60, this);
			g.drawImage(volume_btn, 430, 50,60,60, this);	
			g1.drawImage(bufImg, 0, 0, w, h, null);
			repaint();	
		}
		else if(state == win) {
			g.drawImage(play, 0, 0,w,h, this);
			g.drawImage(win_img, 0, 0,w,h, this);
		
			
			g.drawImage(again, 200, 500,150,60, this);	
			g.drawImage(exit, 10, 50,60,60, this);
			g.drawImage(volume_btn, 430, 50,60,60, this);	
			
			g1.drawImage(bufImg, 0, 0, w, h, null);
			repaint();
		}
	}
	void drawString(Graphics g,int len, String text, int x, int y) {
		String[] s = text.split(" ");
		int line = s.length/len;
		y= y-g.getFontMetrics().getHeight()*line/2;
		for(int i=0;i<s.length;i+=len) {
			String t = s[i];
			
			for(int j=1;j<len;j++) {
				if(i+j<s.length) {
					t = t.concat(" "+s[i+j]);
				}
			}
			g.drawString(t, x, y += g.getFontMetrics().getHeight());
		}
	  
	}
	public void paintPlay(Graphics g1) {
		g.drawImage(play, 0, 0,w,h, this);
		g.drawImage(volume_btn, 400, 60,60,60, this);
		
		g.setColor(Color.PINK);
		g.setFont(new Font("Georgia",Font.BOLD,30));
		g.drawString("QUESTION: ", 150, 100);
		
		g.setColor(Color.red);
		g.setFont(new Font("Georgia",Font.BOLD,40));
		g.drawString(String.valueOf(stt), 350, 100);
		
		g.setColor(Color.green);
		g.setFont(new Font("arial",Font.BOLD,40));
		g.drawString(String.valueOf(time), 20,100);

		g.drawImage(question_img, 30, 80,440,250, this);
		g.setColor(new Color(24, 5, 58));
		g.setFont(new Font("UTF-8",Font.BOLD,16));
		drawString(g,10,question,70,240);
			
		g.drawImage(dapan, 30, 355,210,100, this);
		g.setColor(color_A);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		drawString(g,5,"A. "+a,60,388);
			
		g.drawImage(dapan, 260, 355,210,100, this);
		g.setColor(color_B);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		drawString(g,5,"B. "+b,290,388);
		
		g.drawImage(dapan, 30, 485,210,100, this);
		g.setColor(color_C);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		drawString(g,5,"C. "+c,60,518);
			
		g.drawImage(dapan, 260, 485,210,100, this);
		g.setColor(color_D);
		g.setFont(new Font("UTF-8",Font.BOLD,15));
		drawString(g,5,"D. "+d,290,518);
		
		g1.drawImage(bufImg, 0, 0, w, h, null);	
	}
	
}
