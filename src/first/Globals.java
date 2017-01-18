package first;
import battlecode.common.*;
import java.util.Random;

public class Globals {
	
	static RobotController rc;
    static Random myRand;
    
    // Keep broadcast channels
    static int ARCHON_CHANNEL = 4; 	
    static int GARDENER_CHANNEL = 5;
    static int LUMBERJACK_CHANNEL = 6;
	static int LUMBER_ALIVE_CHANNEL = 7;
    
    //LOC_CHANNELS use next integer channel as well
    static int ARCHON_LOC_CHANNEL = 1;
    
    //FLAG_CHANNELS
    static int CHARGE_CHANNEL = 10;
    static int DEFENSE_CHANNEL = 11;
    static int DETECTED_CHANNEL = 12;

    // Keep important numbers here
    static Team myTeam, them;
    static MapLocation initialArchonLocations[];

    static int VICTORY_CASH = 400;

    static int GARDENER_MAX = 6;
    static int LUMBERJACK_MAX = 5;
    static int ROUND_CHANGE = 500;
    
	

    public static void init(RobotController theRC) {

    	rc = theRC;
    	
    	myTeam = rc.getTeam();
    	them = myTeam.opponent();
    	initialArchonLocations = rc.getInitialArchonLocations(them);

    	RobotPlayer.rc = theRC;
        myRand = new Random(rc.getID());
    }


    public static Direction randomDirection() {
        return(new Direction(myRand.nextFloat()*2*(float)Math.PI));
    }

    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
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
    
	public static void locateArchon() throws GameActionException 
	{
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, myTeam.opponent());
		for (RobotInfo enemy : nearbyEnemies) {
			if (enemy.type == RobotType.ARCHON) {
				broadcastLocation(enemy.getLocation(), ARCHON_LOC_CHANNEL);
				rc.broadcast(CHARGE_CHANNEL, 1);
				rc.broadcast(DETECTED_CHANNEL, 1);
				return;
			}
		}
		
		if(rc.readBroadcast(DETECTED_CHANNEL) == 1 && rc.getLocation().distanceTo(recieveLocation(ARCHON_LOC_CHANNEL)) < rc.getType().sensorRadius)
		{
			rc.broadcast(CHARGE_CHANNEL, 0);
		}
	}
	
	public static void moveTwardArchon() throws GameActionException 
	{
		if(rc.readBroadcast(CHARGE_CHANNEL) == 1)
		{
			if(rc.readBroadcast(DETECTED_CHANNEL) == 1)
			{
				Pathfinding.tryMove(rc.getLocation().directionTo(recieveLocation(ARCHON_LOC_CHANNEL)));
			}
			else
			{
				Pathfinding.tryMove(rc.getLocation().directionTo(rc.getInitialArchonLocations(myTeam.opponent())[0]));
			}
		}
		else Pathfinding.wander();
	}
}


