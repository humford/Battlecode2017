package scoutmania;
import battlecode.common.*;

public class LocationList extends Globals {
	
	public final int start_limit, end_limit, length, HEAD_CHANNEL, LENGTH_CHANNEL;
	
	public LocationList(int _start, int _end) throws GameActionException {
		// The start of the data occurs after the start of the queue
		HEAD_CHANNEL = _start;
		start_limit = _start + 1;
		end_limit = _end - 1;
		length = end_limit - start_limit;
		LENGTH_CHANNEL = _end;
	}
	
	public int getHead() throws GameActionException
	{
		return rc.readBroadcastInt(HEAD_CHANNEL);
	}
	
	public int getLength() throws GameActionException
	{
		return rc.readBroadcastInt(LENGTH_CHANNEL);
	}
	
	
	
	public void addLocation(MapLocation loc) throws GameActionException
	{
		int head = getHead();
		if(head == 0)
		{
			head = start_limit;
			rc.broadcast(HEAD_CHANNEL, head);
			Messaging.broadcastLocation(loc, head);
			rc.broadcast(head + 2, 0);
			rc.broadcast(LENGTH_CHANNEL, getLength() + 1);
		}
		else
		{
			int ptrchannel = head + 2;
			
			while(head != 0)
			{
				//if(head > end_limit || head < start_limit)
				//	return;
				
				MapLocation curLoc = Messaging.recieveLocation(head);
				if(curLoc != null)
				{
					if(loc.equals(curLoc))
						return;
				}
				
				ptrchannel = head + 2;
				head = rc.readBroadcast(head + 2);
			}
			
			int channel = ptrchannel + 1;
			if(channel + 2 > end_limit)
				channel = start_limit;
			
			for(int i = 0; i < length; i += 3)
			{
				if(rc.readBroadcast(channel) == 0 && rc.readBroadcast(channel + 1) == 0 && rc.readBroadcast(channel + 2) == 0)
				{
					rc.broadcast(ptrchannel, channel);
					Messaging.broadcastLocation(loc, channel);
					rc.broadcast(channel + 2, 0);
					rc.broadcast(LENGTH_CHANNEL, getLength() + 1);
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
		if(head == 0) 
			return null;
		int ptrchannel = head + 2;
		
		MapLocation bestLoc = Messaging.recieveLocation(head);
		float bestDist = loc.distanceTo(bestLoc);
		int bestNode = head;
		int prepointer = head;
		
		head = rc.readBroadcast(ptrchannel);
		
		while(head != 0)
		{
			//if(head > end_limit || head < start_limit)
			//	return null;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			if(curLoc == null)
				return null;
			float curDist = loc.distanceTo(curLoc);
			
			if(curDist < bestDist)
			{
				bestLoc = curLoc;
				bestDist = curDist;
				bestNode = head;
				prepointer = ptrchannel;
			}
			ptrchannel = head + 2;
			head = rc.readBroadcast(ptrchannel);
		}
		
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
		rc.broadcast(LENGTH_CHANNEL, getLength() - 1); 
		return bestLoc;
	}
	
	public MapLocation peakNearest(MapLocation loc) throws GameActionException
	{
		int head = getHead();
		if(head == 0) 
			return null;
		int ptrchannel = head + 2;
		
		MapLocation bestLoc = Messaging.recieveLocation(head);
		float bestDist = loc.distanceTo(bestLoc);
		
		head = rc.readBroadcast(ptrchannel);
		
		while(head != 0)
		{
			//if(head > end_limit || head < start_limit)
			//	return null;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			if(curLoc == null)
				return null;
			float curDist = loc.distanceTo(curLoc);
			
			if(curDist < bestDist)
			{
				bestLoc = curLoc;
				bestDist = curDist;
			}
			ptrchannel = head + 2;
			head = rc.readBroadcast(ptrchannel);
		}
		
		return bestLoc;
	}
	
	public boolean isIn(MapLocation loc) throws GameActionException
	{
		int head = getHead();
		if(head == 0) 
			return false;
		
		int ptrchannel = head + 2;
		
		while(head != 0)
		{
		//	if(head > end_limit || head < start_limit)
		//		return false;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			if(curLoc == null)
				return false;
			
			if(curLoc.equals(loc))
				return true;
			ptrchannel = head + 2;
			head = rc.readBroadcast(ptrchannel);
		}
		
		return false;
	}
	
	public void printList() throws GameActionException
	{
		int head = getHead();
		int ptrchannel = head + 2;
		
		System.out.println("LEN: " + getLength());
		
		while(head != 0)
		{	
			if(head > end_limit || head < start_limit)
				return;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			if(curLoc == null)
				return;
			
			System.out.println(curLoc.toString());
			
			rc.setIndicatorDot(curLoc, 0, 127, 0);
			
			head = rc.readBroadcast(ptrchannel);
			ptrchannel = head + 2;
			
		}
	}
	
	public void debug_drawList() throws GameActionException
	{
		int head = getHead();
		int ptrchannel = head + 2;
		
		while(head != 0)
		{	
			if(head > end_limit || head < start_limit)
				return;
			
			MapLocation curLoc = Messaging.recieveLocation(head);
			if(curLoc == null)
				return;
			
			rc.setIndicatorDot(curLoc, 255, 0, 255);
			
			head = rc.readBroadcast(ptrchannel);
			ptrchannel = head + 2;
			
		}
	}
	
	public boolean IsEmpty() throws GameActionException
	{
		return (getHead() == 0);
	}
	
	public void debug() throws GameActionException
	{
		System.out.println("HEAD: " + rc.readBroadcast(HEAD_CHANNEL));
		if(getHead() != 0)
		{
			for(int i = getHead(); i < getHead() + 30; i++)
			{
				System.out.println(rc.readBroadcast(i));
			}
		}
	}
}
