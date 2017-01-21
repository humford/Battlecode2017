package scoutmania;
import battlecode.common.*;
import java.util.Random;

public class Globals {
	
	static RobotController rc;
    static Random myRand;
    static MapLocation birthLoc;
    
    static final int GARDENER_UPPER_LIMIT = 10;
    // Keep broadcast channels
    static final int GARDENER_COUNT_CHANNEL = 1;
    static final int GARDENER_SUM_CHANNEL = 2;
    static final int HAS_COUNTED_CHANNEL = 3;
    static final int GARDENER_INPRODUCTION_CHANNEL = 4;
    
    //LOC_CHANNELS use next integer channel as well
    static final int STRIKE_LOC_CHANNEL = 5;
    static final int DEFENSE_LOC_CHANNEL = 7;
    static final int SCOUT_LOC_CHANNEL = 9;
    
    //FLAG_CHANNELS
    static final int ARCHON_TARGETING_CHANNEL = 11;
    static final int GARDENER_TARGETING_CHANNEL = 12;
    static final int DEFENSE_CHANNEL = 13;

    // Keep important numbers here
    static Team myTeam, them;
    static MapLocation initialArchonLocations[];
    /*
    Condensed = 0->mediumCondensed
    Condensed = 1->veryCondensed
    */
    static final int VICTORY_CASH_CUTOFF = 400;

    static final int GARDENER_MAX = 10;
    static final int LUMBERJACK_MAX = 5;
    static final int ROUND_CHANGE = 500;
    
 
    
	

    public static void init(RobotController theRC) {

    	rc = theRC;
    	
    	myTeam = rc.getTeam();
    	them = myTeam.opponent();
    	initialArchonLocations = rc.getInitialArchonLocations(them);
    	birthLoc = rc.getLocation();

    	RobotPlayer.rc = theRC;
        myRand = new Random(rc.getID());
        Pathfinding.lastWander = randomDirection();
    }


    public static Direction randomDirection() {
        return(new Direction(myRand.nextFloat()*2*(float)Math.PI));
    }
    
    public static boolean buildWhereFree(RobotType bot, int check) throws GameActionException {
    	Direction dir = Direction.NORTH;
        for(int i = 0; i < check; i++)
        {
        	if(rc.canBuildRobot(bot, dir))
        	{
        		rc.buildRobot(bot, dir);
        		return true;
        	}
        	dir = dir.rotateLeftDegrees(360f/check);
        }
        return false;
    }

    static boolean willCollideWith(BulletInfo bullet, MapLocation myLocation) {

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2 && distToRobot > rc.getType().bodyRadius) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius * 1.25);
    }
 
	public static void locateType(RobotType type, int targetChannel, int locChannel) throws GameActionException 
	{
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, them);
		for (RobotInfo enemy : nearbyEnemies) {
			if (enemy.type == type) {
				Messaging.broadcastLocation(enemy.getLocation(), locChannel);
				if(rc.readBroadcast(targetChannel) == -1)rc.broadcast(targetChannel, initialArchonLocations.length);
				return;
			}
			if(enemy.type != RobotType.ARCHON && rc.readBroadcast(targetChannel) == -1)
			{
				Messaging.broadcastLocation(enemy.getLocation(), locChannel);
				rc.broadcast(targetChannel, initialArchonLocations.length);
				return;
			}
		}
		
		if(rc.getLocation().distanceTo(Messaging.recieveLocation(locChannel)) < rc.getType().sensorRadius)
		{
			int dest = rc.readBroadcast(targetChannel);
			if(dest + 1 < initialArchonLocations.length && dest != -1) 
			{
				rc.broadcast(targetChannel, (dest + 1));
				Messaging.broadcastLocation(initialArchonLocations[rc.readBroadcast(targetChannel)], locChannel);
			}
			else rc.broadcast(targetChannel, -1);
		}
	}
	
	public static void donate() throws GameActionException // if can win by donating then do it
	{
		if(rc.getTeamBullets() > VICTORY_CASH_CUTOFF)
    	{
    		int x = (int)rc.getTeamBullets() - VICTORY_CASH_CUTOFF;
    		rc.donate(x - x%10);
    	}
		
		if(((int) rc.getTeamBullets())/10 + rc.getTeamVictoryPoints() > GameConstants.VICTORY_POINTS_TO_WIN)
		{
			rc.donate(rc.getTeamBullets());
		}
		
	}
	
	public static void refillQueue() throws GameActionException
	{
		if(BuildQueue.getLength() <= 0)
    	{
    		if(rc.readBroadcast(GARDENER_COUNT_CHANNEL) < GARDENER_UPPER_LIMIT)
        	{
    			BuildQueue.enqueue(RobotType.GARDENER);
        	}
    		else
    		{
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SOLDIER);
    		}
    	}
	}
	
	public static void loop_common() throws GameActionException // things that all robots do in loop
	{
		donate();
		refillQueue();
		locateType(RobotType.ARCHON, ARCHON_TARGETING_CHANNEL, STRIKE_LOC_CHANNEL);
		locateType(RobotType.GARDENER, GARDENER_TARGETING_CHANNEL, SCOUT_LOC_CHANNEL);
	}
}


