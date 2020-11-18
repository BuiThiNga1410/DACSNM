package hdh_philosophers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class App extends JFrame{
	List<Chopstick> chopsticks;
	List<Philosophers>philosophers;
	Random stateRandom ;
	List<Image>img_states;
	Image table;
	BufferedImage bufImg;
	Graphics g;
	int w=600;
	int h=700;
	public static void main(String[] args) {
		new App();

	}
	public App() {
		this.setTitle("Philosophers");
		this.setSize(w,h);
		this.setDefaultCloseOperation(3);
		
		this.chopsticks = new ArrayList<Chopstick>();
		this.philosophers = new ArrayList<Philosophers>();
		for(int i=0;i<5;i++) {
			chopsticks.add(new Chopstick(i));
		}
		for(int j=0; j<4; j++) {
			Philosophers a= new Philosophers(j, chopsticks.get(j+1), chopsticks.get(j),0);
			philosophers.add(a);
			a.start();
			
		}
		Philosophers a = new Philosophers(4, chopsticks.get(0), chopsticks.get(4), 0);
		philosophers.add(a);
		a.start();
		bufImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		g = bufImg.getGraphics();
		this.setVisible(true);
		img_states = new ArrayList<Image>();
		table = getToolkit().getImage("img\\ban_an.png");
		img_states.add(getToolkit().getImage("img\\thinking.jpg"));
		img_states.add(getToolkit().getImage("img\\eating.png"));
		img_states.add(getToolkit().getImage("img\\waiting.jpg"));
	}
	public void paint(Graphics g1) {
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		
		g.drawImage(table, 150, 150,300,300, this);
		
		g.setColor(Color.red);
		g.drawRect(0, 529, 250,h-530 );
		
		g.setColor(Color.gray);
		g.fillRect(0, 530, 250,h-530 );
		
		g.drawImage(img_states.get(0), 50, 550,30,30, this);
		g.drawImage(img_states.get(1), 50, 600,30,30, this);
		g.drawImage(img_states.get(2), 50, 650,30,30, this);
		
		g.setColor(Color.red);
		g.setFont(new Font("Georgia",Font.BOLD,15));
		g.drawString("Thinking", 150, 570);
		
		g.setColor(Color.red);
		g.setFont(new Font("Georgia",Font.BOLD,15));
		g.drawString("Eating", 150, 620);
		
		g.setColor(Color.red);
		g.setFont(new Font("Georgia",Font.BOLD,15));
		g.drawString("Waiting", 150, 670);
		draw0(g1);
		draw1(g1);
		draw2(g1);
		draw3(g1);
		draw4(g1);
		g1.drawImage(bufImg, 0, 0, w, h, null);
	}

	public void draw0(Graphics g1) {
		g.drawImage(img_states.get(philosophers.get(0).get_State()), 275, 80,50,50, this);
		repaint();
	}
	public void draw1(Graphics g1) {
		g.drawImage(img_states.get(philosophers.get(1).get_State()), 455, 190,50,50, this);
		repaint();
	}
	public void draw2(Graphics g1) {
		g.drawImage(img_states.get(philosophers.get(2).get_State()), 455, 360,50,50, this);
		repaint();
	}
	public void draw3(Graphics g1) {
		g.drawImage(img_states.get(philosophers.get(3).get_State()), 90, 360,50,50, this);
		repaint();
	}
	public void draw4(Graphics g1) {
		g.drawImage(img_states.get(philosophers.get(4).get_State()), 90, 190,50,50, this);
		repaint();
	}
	public void deadlock(List<Philosophers> philosophers) {
		
	}

}
