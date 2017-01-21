package better_dodge;
import battlecode.common.*;

public class Pathfinding extends Globals {
	/**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public static boolean tryMove(Direction dir) throws GameActionException {
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
    
    
    
    private static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
    	BulletInfo bullets[] = rc.senseNearbyBullets();
    	
    	boolean isSafe = true;
    	MapLocation loc = rc.getLocation().add(dir);
    	
    	for(BulletInfo b : bullets)
    	{
    		if(willCollideWith(b, loc))
    		{
    			isSafe = false;
    			break;
    		}
    	}
    	
        if (!rc.hasMoved() && rc.canMove(dir) && isSafe) {
        	
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean moved = rc.hasMoved();
        int currentCheck = 1;
        
        while(currentCheck <= checksPerSide) {
            // Try the offset of the left side
        	Direction curDir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
        	loc = rc.getLocation().add(curDir, rc.getType().strideRadius);
        	
            if(!rc.hasMoved() && rc.canMove(curDir)) {
            	
            	isSafe = true;
            	
            	for(BulletInfo b : bullets)
            	{
            		if(willCollideWith(b, loc))
            		{
            			isSafe = false;
            			break;
            		}
            	}
            	
                if(isSafe)
                {
                	rc.move(curDir);
                	return true;
                }
            }
            // Try the offset on the right side
        	curDir = dir.rotateRightDegrees(degreeOffset*currentCheck);
        	loc = rc.getLocation().add(curDir, rc.getType().strideRadius);
        	
            if(!rc.hasMoved() && rc.canMove(curDir)) {
            	
            	isSafe = true;
            	
            	for(BulletInfo b : bullets)
            	{
            		if(willCollideWith(b, loc))
            		{
            			isSafe = false;
            			break;
            		}
            	}
            	
                if(isSafe)
                {
                	rc.move(curDir);
                	return true;
                }
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
       // Micro.dodge();
        return false;
    }
    
    
    public static void wander() throws GameActionException {
        Direction dir = randomDirection();
        tryMove(dir);
    }
    
    // Placeholder for now, should have pathfinding later
    public static void moveTo(MapLocation location) throws GameActionException {
    	tryMove(rc.getLocation().directionTo(location));
    }
}
