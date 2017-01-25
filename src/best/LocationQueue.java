package best;
import battlecode.common.*;

public class LocationQueue extends Globals {
	public final int start, length;
	
	public final int start_pointer_channel;
	public final int end_pointer_channel;
	
	public LocationQueue(int _start, int _length) {
		// The start of the data occurs after the start of the queue
		start = _start + 2;
		length = _length;
		start_pointer_channel = _start;
		end_pointer_channel = _start + 1;
	}
	
	private int getStartPointer() throws GameActionException {
		return rc.readBroadcast(start_pointer_channel);
	}
	
	private int getEndPointer() throws GameActionException {
		return rc.readBroadcast(end_pointer_channel);
	}
	
	public void enqueue(MapLocation val) throws GameActionException {
		int end_pointer = getEndPointer();
		Messaging.broadcastLocation(val, end_pointer);
		rc.broadcast(end_pointer_channel, (end_pointer + 2) % length);
	}
	
	// returns null when the queue is empty
	public MapLocation dequeue() throws GameActionException {
		int end_pointer = getEndPointer();
		int start_pointer = getStartPointer();
		if (start_pointer == end_pointer) return null;
		MapLocation val = Messaging.recieveLocation(start_pointer);
		rc.broadcast(start_pointer_channel, (start_pointer + 2) % length);
		
		return val;
	}
	
	public MapLocation peak() throws GameActionException {
		int start_pointer = getStartPointer();
		if (start_pointer == getEndPointer()) return null;
		return Messaging.recieveLocation(start_pointer);
	}
	
	public void debug_print_queue() throws GameActionException {
		int start_pointer = getStartPointer();
		int end_pointer = getEndPointer();
		for (int i = start_pointer; i != end_pointer; i = (i + 1) % length) {
			System.out.println(Messaging.recieveLocation(i));
		}
	}
}
