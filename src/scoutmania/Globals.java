package scoutmania;
import battlecode.common.*;
import java.util.Random;

public class Globals {
	
	static RobotController rc;
    static Random myRand;
    
    static final int GARDENER_LOWER_LIMIT = 7;
    // Keep broadcast channels
    static final int GARDENER_COUNT_CHANNEL = 5;
    static final int GARDENER_SUM_CHANNEL = 6;
    
    //LOC_CHANNELS use next integer channel as well
    static final int STRIKE_LOC_CHANNEL = 1;
    static final int DEFENSE_LOC_CHANNEL = 3;
    
    //FLAG_CHANNELS
    static final int ARCHON_TARGETING_CHANNEL = 10;
    static final int DEFENSE_CHANNEL = 11;

    // Keep important numbers here
    static Team myTeam, them;
    static MapLocation initialArchonLocations[];
    /*
    Condensed = 0->mediumCondensed
    Condensed = 1->veryCondensed
    */
    static int VICTORY_CASH = 400;

    static int GARDENER_MAX = 10;
    static int LUMBERJACK_MAX = 5;
    static int ROUND_CHANGE = 500;
    
 
    
	

    public static void init(RobotController theRC) {

    	rc = theRC;
    	
    	myTeam = rc.getTeam();
    	them = myTeam.opponent();
    	initialArchonLocations = rc.getInitialArchonLocations(them);

    	RobotPlayer.rc = theRC;
        myRand = new Random(rc.getID());
        Pathfinding.lastWander = randomDirection();
    }


    public static Direction randomDirection() {
        return(new Direction(myRand.nextFloat()*2*(float)Math.PI));
    }
    
    public static void buildWhereFree(RobotType bot, int check) throws GameActionException {
    	Direction dir = Direction.NORTH;
        for(int i = 0; i < check; i++)
        {
        	if(rc.canBuildRobot(bot, dir))
        	{
        		rc.buildRobot(bot, dir);
        		return;
        	}
        	dir = dir.rotateLeftDegrees(360f/check);
        }
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
    
	public static void locateArchon() throws GameActionException 
	{
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, them);
		for (RobotInfo enemy : nearbyEnemies) {
			if (enemy.type == RobotType.ARCHON) {
				Messaging.broadcastLocation(enemy.getLocation(), STRIKE_LOC_CHANNEL);
				return;
			}
			else if(rc.readBroadcast(ARCHON_TARGETING_CHANNEL) == -1)
			{
				Messaging.broadcastLocation(enemy.getLocation(), STRIKE_LOC_CHANNEL);
				rc.broadcast(ARCHON_TARGETING_CHANNEL, initialArchonLocations.length);
				System.out.println("TARGET");
				break;
			}
		}
		
		if(rc.getLocation().distanceTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL)) < rc.getType().sensorRadius)
		{
			int dest = rc.readBroadcast(ARCHON_TARGETING_CHANNEL);
			if(dest + 1 < initialArchonLocations.length && dest != -1) 
			{
				rc.broadcast(ARCHON_TARGETING_CHANNEL, (dest + 1));
				Messaging.broadcastLocation(initialArchonLocations[rc.readBroadcast(ARCHON_TARGETING_CHANNEL)], STRIKE_LOC_CHANNEL);
			}
			else if(nearbyEnemies.length > 0)
			{
				Messaging.broadcastLocation(rc.getLocation(), STRIKE_LOC_CHANNEL);
				rc.broadcast(ARCHON_TARGETING_CHANNEL, initialArchonLocations.length);
				System.out.println("TARGET");
			}
			else rc.broadcast(ARCHON_TARGETING_CHANNEL, -1);
		}
	}
	
	public static void locateType(RobotType type) throws GameActionException 
	{
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, them);
		for (RobotInfo enemy : nearbyEnemies) {
			if (enemy.type == type) {
				Messaging.broadcastLocation(enemy.getLocation(), STRIKE_LOC_CHANNEL);
				return;
			}
			if(rc.readBroadcast(ARCHON_TARGETING_CHANNEL) == -1)
			{
				Messaging.broadcastLocation(enemy.getLocation(), STRIKE_LOC_CHANNEL);
				rc.broadcast(ARCHON_TARGETING_CHANNEL, initialArchonLocations.length);
				return;
			}
		}
		
		if(rc.getLocation().distanceTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL)) < rc.getType().sensorRadius)
		{
			int dest = rc.readBroadcast(ARCHON_TARGETING_CHANNEL);
			if(dest + 1 < initialArchonLocations.length && dest != -1) 
			{
				rc.broadcast(ARCHON_TARGETING_CHANNEL, (dest + 1));
				Messaging.broadcastLocation(initialArchonLocations[rc.readBroadcast(ARCHON_TARGETING_CHANNEL)], STRIKE_LOC_CHANNEL);
			}
			else rc.broadcast(ARCHON_TARGETING_CHANNEL, -1);
		}
	}
	
	public static void donate_to_win() throws GameActionException // if can win by donating then do it
	{
		if((int) rc.getTeamBullets()/10 + rc.getTeamVictoryPoints() > GameConstants.VICTORY_POINTS_TO_WIN)
		{
			rc.donate(rc.getTeamBullets());
		}
		
	}
	
	public static void loop_common() throws GameActionException // things that all robots do in loop
	{
		donate_to_win();
		locateType(RobotType.GARDENER);
	}
}


