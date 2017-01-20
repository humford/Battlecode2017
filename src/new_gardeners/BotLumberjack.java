package new_gardeners;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	
	public static void loop() throws GameActionException {
        while (true) {
            try {
            	loop_common();
	    	
            	Pathfinding.dodge();
                
                RobotInfo[] myBots = rc.senseNearbyRobots(2, myTeam);
                RobotInfo[] theirBots = rc.senseNearbyRobots(2, them);
                boolean isArchon = false;
                
                for(RobotInfo b : theirBots)
                {
                	if(b.getType() == RobotType.ARCHON)
                	{
                		isArchon = true;
                		break;
                	}
                }
                
                if((myBots.length < theirBots.length || isArchon) && rc.canStrike())rc.strike();
                
                Micro.chase();
                
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
                	if(rc.canShake(t.getLocation()) && t.containedBullets > 0){
                		rc.shake(t.getLocation());
                        break;
                	}
                    if (t.getTeam() != myTeam && rc.canChop(t.getLocation())) {
                        rc.chop(t.getLocation());
                        break;
                    }
                }
                if (! rc.hasMoved()) {
                	Pathfinding.tryMove(rc.getLocation().directionTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL)));
                }
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	// Bounds for the rectangle the lumberjack is destroying
	// The variables stored are the top left corner, the top right corner,
	// the bottom left corner, and the bottom right corner in that order
	static MapLocation[] rectangleBounds = new MapLocation[4];
	static MapLocation TopL;
	static MapLocation TopR;
	static MapLocation BotL;
	static MapLocation BotR;
	
	public static void setRectangleBounds(MapLocation bl, MapLocation tr) {
		float xMin = bl.x;
		float yMin = bl.y;
		float xMax = tr.x;
		float yMax = tr.y;
		
		if (xMin > xMax || yMin > yMax) {
			System.out.println("Impossible rectangle bounds");
		}
		
		
		TopL = (rectangleBounds[0] = new MapLocation(xMin, yMax));
		TopR = (rectangleBounds[1] = new MapLocation(xMax, yMax));
		BotL = (rectangleBounds[2] = new MapLocation(xMin, yMin));
		BotR = (rectangleBounds[3] = new MapLocation(xMax, yMin));
	}
	
	public static boolean isInRectangle(MapLocation location) {
		return location.x > BotL.x && location.y > BotL.y &&
				location.x < TopR.x && location.y < TopR.y;
	}
	
	public static void clearRectangle() throws GameActionException {
		MapLocation myLocation = rc.getLocation();
		MapLocation nearestCorner = rectangleBounds[0];
		float leastDistance = myLocation.distanceTo(nearestCorner);
		for (int i = 1; i < rectangleBounds.length; i++) {
			float dist = myLocation.distanceTo(rectangleBounds[i]);
			if (dist < leastDistance) {
				leastDistance = dist;
				nearestCorner = rectangleBounds[i];
			}
		}
		
		if (leastDistance > rc.getType().strideRadius) {
			// Still approaching the rectangle
			Pathfinding.moveTo(nearestCorner);
		}
		else {
			TreeInfo[] trees = rc.senseNearbyTrees();
			for (TreeInfo tree : trees) {
				if (tree.team == myTeam) continue;
				
			}
		}
	}
}
