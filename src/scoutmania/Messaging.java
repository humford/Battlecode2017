package scoutmania;

import battlecode.common.*;


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
    
    public static boolean wasUnderAttack = false;
    
    public static void broadcastDefendMeIF(boolean isUnderAttack) throws GameActionException
    {	
    	if(isUnderAttack)
    	{
    		System.out.println("UNDER ATTACK");
    		Messaging.broadcastLocation(rc.getLocation(), DEFENSE_LOC_CHANNEL);
    		rc.broadcastBoolean(DEFENSE_CHANNEL, true);
    		wasUnderAttack = true;
    	}
    	
    	else if(wasUnderAttack)
    	{
    		System.out.println("NOT UNDER ATTACK");
    		rc.broadcastBoolean(DEFENSE_CHANNEL, false);
    		wasUnderAttack = false;
    	}
    }
}
