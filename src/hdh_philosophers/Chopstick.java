package hdh_philosophers;
import java.util.*;
import java.util.concurrent.Semaphore;
public class Chopstick {
	static int id;
	Semaphore mutex;
	public static void main(String[] args) {
		

	}
	public Chopstick(int id) {
		this.id = id;
		this.mutex = new Semaphore(1);
	}
	public void  acquire() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void release() {
		mutex.release();
	}
}
