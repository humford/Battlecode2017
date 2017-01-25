package KohsGarden;
import battlecode.common.*;

public class LocationList extends Globals {
	
	public final int start_limit, end_limit, length, HEAD_CHANNEL, TAIL_CHANNEL;
	
	public LocationList(int _start, int _end) throws GameActionException {
		// The start of the data occurs after the start of the queue
		HEAD_CHANNEL = _start - 1;
		TAIL_CHANNEL = _start - 2;
		start_limit = _start;
		end_limit = _end;
		length = end_limit - start_limit;
		
		rc.broadcast(HEAD_CHANNEL, start_limit);
	//	rc.broadcast(TAIL_CHANNEL, start_limit);
	}
	
	public int getHead() throws GameActionException
	{
		return rc.readBroadcastInt(HEAD_CHANNEL);
	}
	
/*	public int getTail() throws GameActionException
	{
		return rc.readBroadcastInt(TAIL_CHANNEL);
	}
	
	public void addLocation(MapLocation loc) throws GameActionException
	{
		int channel = getTail() + 3;
		for(int i = 0; i < length; i += 3)
		{
			if(rc.readBroadcast(channel) == 0 && rc.readBroadcast(channel + 1) == 0 && rc.readBroadcast(channel + 2) == 0)
			{
				rc.broadcast(getTail() + 2, channel + i);
				Messaging.broadcastLocation(loc, channel);
				rc.broadcast(channel + 2, 0);
				rc.broadcast(TAIL_CHANNEL, channel);
				return;
			}
			channel += 3;
			if(channel + 2 > end_limit)
				channel = start_limit;
		}
	}*/
	
	public void addLocation(MapLocation loc) throws GameActionException
	{
		int head = getHead();
		if(head == 0)
		{
			head = start_limit;
			Messaging.broadcastLocation(loc, head);
			rc.broadcast(head + 2, 0);
		}
		else
		{
			int ptrchannel = head + 2;
			
			while(rc.readBroadcast(ptrchannel) != 0)
			{
				head = rc.readBroadcast(ptrchannel);
				
				MapLocation curLoc = Messaging.recieveLocation(head);
				
				if(loc.equals(curLoc))
					return;
				
				ptrchannel = head + 2;
			}
			
			int channel = head + 3;
			
			for(int i = 0; i < length; i += 3)
			{
				if(rc.readBroadcast(channel) == 0 && rc.readBroadcast(channel + 1) == 0 && rc.readBroadcast(channel + 2) == 0)
				{
					rc.broadcast(ptrchannel, channel + i);
					Messaging.broadcastLocation(loc, channel);
					rc.broadcast(channel + 2, 0);
					return;
				}
				channel += 3;
				if(channel + 2 > end_limit)
					channel = start_limit;
			}
		}
	}
	
	public MapLocation getNearest(MapLocation loc) throws GameActionException
	{
		int head = getHead();
		if(head == 0) return null;
		int ptrchannel = head + 2;
		
		MapLocation bestLoc = Messaging.recieveLocation(head);
		float bestDist = loc.distanceTo(bestLoc);
		int bestNode = head;
		int prepointer = head;
		
		while(rc.readBroadcast(ptrchannel) != 0)
		{
			head = rc.readBroadcast(ptrchannel);
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			float curDist = loc.distanceTo(curLoc);
			
			if(curDist < bestDist)
			{
				bestLoc = curLoc;
				bestDist = curDist;
				bestNode = head;
				prepointer = ptrchannel;
			}
			ptrchannel = head + 2;
		}
		
	/*	if(rc.readBroadcast(bestNode + 2) == 0)
		{
			rc.broadcast(TAIL_CHANNEL, prepointer - 2);
		}*/
		
		if(bestNode == getHead())
		{
			rc.broadcast(HEAD_CHANNEL, rc.readBroadcast(bestNode + 2));
		}
		
		else
		{
			rc.broadcast(prepointer, rc.readBroadcast(bestNode + 2));
		}
		
		rc.broadcast(bestNode, 0);
		rc.broadcast(bestNode + 1, 0);
		rc.broadcast(bestNode + 2, 0);
		
		return bestLoc;
	}
	
	public void printList() throws GameActionException
	{
		int head = getHead();
		if(head == 0) return;
		int ptrchannel = head + 2;
		
		while(rc.readBroadcast(ptrchannel) != 0)
		{
			head = rc.readBroadcast(ptrchannel);
			ptrchannel = head + 2;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			
			System.out.println(curLoc.toString());
			
		}
	}
}
