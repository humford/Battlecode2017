package best;
import battlecode.common.*;

public class BroadcastQueue extends Globals {
	public int start, length;
	
	public final int start_pointer_loc;
	public final int end_pointer_loc;
	
	public BroadcastQueue(int _start, int _length) {
		start = _start;
		length = _length;
		start_pointer_loc = start;
		end_pointer_loc = start + 1;
	}
	
	public static void increment() throws GameActionException {
		
	}
	
	public static void decrement() throws GameActionException {
		
	}
	
	public static void enqueue(int val) {
		
	}
	
	public static int dequeue() {
		return 0;
	}
	
	public static int peak() {
		return 0;
	}
	
	public static void print_queue() {
		
	}
}
