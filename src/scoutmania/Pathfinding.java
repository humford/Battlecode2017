package scoutmania;
import battlecode.common.*;

public class Pathfinding extends Globals {
	
	public static Direction lastWander;
	
	/**
     * Attempts to move in a given direction, while avoiding small obstacles and bullets directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,5);
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
    
    private static boolean tryMoveWithoutBulletDetection(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

    	if(rc.hasMoved())return false;
        // First, try intended direction
    
        if (!rc.hasMoved() && rc.canMove(dir)) {
        	
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean moved = rc.hasMoved();
        int currentCheck = 1;
        
        while(currentCheck <= checksPerSide) {
            // Try the offset of the left side
        	Direction curDir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
        	
            if(!rc.hasMoved() && rc.canMove(curDir)) {
                rc.move(curDir);
                return true;
            }
            // Try the offset on the right side
        	curDir = dir.rotateRightDegrees(degreeOffset*currentCheck);

            if(!rc.hasMoved() && rc.canMove(curDir)) {
            	rc.move(curDir);
            	return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        return false;
    }
    
    
    
    private static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

    	if(rc.hasMoved())return false;
        // First, try intended direction
    	BulletInfo bullets[] = rc.senseNearbyBullets();
    	
    	boolean isSafe = true;
    	MapLocation loc = rc.getLocation().add(dir, rc.getType().strideRadius);
    	
    	
        if (!rc.hasMoved() && rc.canMove(dir)) {
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
        		rc.move(dir);
        		return true;
        	}
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
        if(tryMoveWithoutBulletDetection(dir, degreeOffset, checksPerSide))
        	return true;
        else
        	return false;
    }
    
    
    public static void wander() throws GameActionException {
    	float angle = 0;
    	
    	if(rc.onTheMap(rc.getLocation().add(lastWander, rc.getType().strideRadius), (float) ((rc.getType().sensorRadius - rc.getType().strideRadius)*0.75)))
    		angle = myRand.nextFloat()*(float)Math.PI/4;
    	else
    		angle = (myRand.nextFloat() + 1)*(float)Math.PI/2;
    	
    	if(myRand.nextBoolean())
			lastWander = lastWander.rotateLeftRads(angle);
		else
			lastWander = lastWander.rotateRightRads(angle);
    	
    	tryMove(lastWander);
		return;
    }
    
    // Placeholder for now, should have pathfinding later
    public static void moveTo(MapLocation location) throws GameActionException {
    	tryMove(rc.getLocation().directionTo(location));
    }
}
