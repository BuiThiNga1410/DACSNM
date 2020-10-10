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
	public static void main(String[] args) {
		new App();

	}
	public App() {
		this.setTitle("Philosophers");
		this.setSize(600, 700);
		this.setDefaultCloseOperation(3);
		this.setLayout(null);
		this.setVisible(true);
		img_states = new ArrayList<Image>();
		table = getToolkit().getImage("D:\\ban_an.png");
		img_states.add(getToolkit().getImage("D:\\thinking.jpg"));
		img_states.add(getToolkit().getImage("D:\\eating.png"));
		img_states.add(getToolkit().getImage("D:\\waiting.jpg"));
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
			
	}
	public void paint(Graphics g) {
		g.drawImage(table, 150, 150,300,300, this);
		g.drawImage(img_states.get(0), 50, 550,30,30, this);
		g.drawImage(img_states.get(1), 50, 600,30,30, this);
		g.drawImage(img_states.get(2), 50, 650,30,30, this);
		g.drawString("Thinking", 150, 570);
		g.drawString("Eating", 150, 620);
		g.drawString("Waiting", 150, 670);
		paintState(g);
		
	}
	public void paintState(Graphics g) {
		draw0(g);
		draw1(g);
		draw2(g);
		draw3(g);
		draw4(g);
	}
	public void draw0(Graphics g) {
		g.drawImage(img_states.get(philosophers.get(0).get_State()), 275, 80,50,50, this);
		repaint();
	}
	public void draw1(Graphics g) {
		g.drawImage(img_states.get(philosophers.get(1).get_State()), 455, 190,50,50, this);
		repaint();
	}
	public void draw2(Graphics g) {
		g.drawImage(img_states.get(philosophers.get(2).get_State()), 455, 360,50,50, this);
		repaint();
	}
	public void draw3(Graphics g) {
		g.drawImage(img_states.get(philosophers.get(3).get_State()), 90, 360,50,50, this);
		repaint();
	}
	public void draw4(Graphics g) {
		g.drawImage(img_states.get(philosophers.get(4).get_State()), 90, 190,50,50, this);
		repaint();
	}
	public void deadlock(List<Philosophers> philosophers) {
		
	}

}
