package KohsGarden;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class Messaging extends Globals{
    public static void broadcastLocation(MapLocation loc, int CHANNEL) throws GameActionException
    {
    	rc.broadcastFloat(CHANNEL, loc.x); 
    	rc.broadcastFloat(CHANNEL+1, loc.y);
    }
    
    public static MapLocation recieveLocation(int CHANNEL) throws GameActionException
    {
    	float x = rc.readBroadcastFloat(CHANNEL);
    	float y = rc.readBroadcastFloat(CHANNEL+1);
    	
    	return new MapLocation(x,y);
    }
}
