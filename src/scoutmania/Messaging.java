package scoutmania;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class Messaging extends Globals{
    public static void broadcastLocation(MapLocation loc, int CHANNEL) throws GameActionException
    {
    	rc.broadcast(CHANNEL, (int) (loc.x * 1000)); 
    	rc.broadcast(CHANNEL+1, (int) (loc.y * 1000));
    }
    
    public static MapLocation recieveLocation(int CHANNEL) throws GameActionException
    {
    	float x = ((float) rc.readBroadcast(CHANNEL)) / 1000;
    	float y = ((float) rc.readBroadcast(CHANNEL+1)) / 1000;
    	
    	return new MapLocation(x,y);
    }
}
