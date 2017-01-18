package first;
import battlecode.common.*;
import java.util.Random;

public class Globals {
	
	static RobotController rc;
	static Team myTeam;
    static Random myRand;
    
    // Keep broadcast channels
    static int GARDENER_CHANNEL = 5;
    static int LUMBERJACK_CHANNEL = 6;
	static int LUMBER_ALIVE_CHANNEL = 7;
    
    //LOC_CHANNELS use next integer channel as well
    static int ARCHON_LOC_CHANNEL = 1;

    // Keep important numbers here
    static int GARDENER_MAX = 6;
    static int LUMBERJACK_MAX = 5;
static int ROUND_CHANGE = 500;
    
    static boolean chargeActivate = true;
    static boolean archonDetected = false;
    static boolean defenseActivte = false;
	

    public static void init(RobotController theRC) {

    	RobotPlayer.rc = theRC;
    	myTeam = rc.getTeam();
        myRand = new Random(rc.getID());
    }
    
    public static void wander() throws GameActionException {
        Direction dir = randomDirection();
        tryMove(dir);
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

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }


    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (!rc.hasMoved() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean moved = rc.hasMoved();
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(!rc.hasMoved() && rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(! rc.hasMoved() && rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    static boolean trySidestep(BulletInfo bullet) throws GameActionException{

        Direction towards = bullet.getDir();
        MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
        MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);

        return(tryMove(towards.rotateRightDegrees(90)) || tryMove(towards.rotateLeftDegrees(90)));
    }

    static void dodge() throws GameActionException {
        BulletInfo[] bullets = rc.senseNearbyBullets();
        for (BulletInfo bi : bullets) {
            if (willCollideWithMe(bi)) {
                trySidestep(bi);
            }
        }

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
				chargeActivate = true;
				archonDetected = true;
				return;
			}
		}
		
		if(rc.getLocation().distanceTo(recieveLocation(ARCHON_LOC_CHANNEL)) < rc.getType().sensorRadius)
		{
			chargeActivate = true;
		}
	}
	
	public static void moveTwardArchon() throws GameActionException 
	{
		if(chargeActivate)
		{
			if(archonDetected)
			{
				tryMove(rc.getLocation().directionTo(recieveLocation(ARCHON_LOC_CHANNEL)));
			}
			else tryMove(rc.getLocation().directionTo(rc.getInitialArchonLocations(myTeam.opponent())[0]));
		}
		else wander();
	}
}


