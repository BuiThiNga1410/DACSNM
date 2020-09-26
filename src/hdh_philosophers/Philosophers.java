package hdh_philosophers;
import java.util.*;
public class Philosophers extends Thread {
	static final int thinking = 0;
	static final int eating = 1;
	static final int waiting = 2;
	Chopstick leftChopstick;
	Chopstick rightChopstick;
	private Random time = new Random();
	private int id;
	private int state;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public Philosophers(int id, Chopstick left, Chopstick right, int state) {
		this.id = id;
		this.leftChopstick = left;
		this.rightChopstick = right;
		this.state= state;
		
	}
	public void acquire() {
		if(id != 0) {
			leftChopstick.acquire();
			rightChopstick.acquire();
		}else {
			rightChopstick.acquire();
			leftChopstick.acquire();
		}
	}
	public void release() {
		if(id != 0) {
			leftChopstick.release();
			rightChopstick.release();
		}else {
			rightChopstick.release();
			leftChopstick.release();
		}
	}
	public boolean check_free(Chopstick a) {
		if(a.mutex.availablePermits()>0)return true;
		else return false;
	}
	public void run() {
		for(int i=0; i<9; i++) {
			
          if(state == thinking) {
				System.out.println("Triet gia thu"+id+"dang nghi");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(this.check_free(leftChopstick)&&this.check_free(rightChopstick)) {
					this.acquire();
					state = eating;
				}
				else {
					state = waiting;
					
				}
				
			}
			
			if(state == eating) {
				System.out.println("Triet gia thu"+id+"dang an");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.release();
			    state =thinking;
			} 
			if(state ==waiting) {
				System.out.println("Triet gia thu"+id+"dang doi");
				this.acquire();
				state = eating;
			}
				
		}
		
	}
}

