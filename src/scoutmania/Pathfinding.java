package scoutmania;
import battlecode.common.*;

public class Pathfinding extends Globals {
	
	public static Direction lastWander;
	
	public static boolean shouldMove(Direction dir) throws GameActionException {
		boolean can = rc.canMove(dir);
		if (!can || rc.getType() != RobotType.TANK) return can;
		MapLocation location = rc.getLocation().add(dir, rc.getType().strideRadius);
		TreeInfo tree = rc.senseTreeAtLocation(location);
		if (tree == null || tree.team != myTeam) return can;
		return false;
	}
	
	/**
     * Attempts to move in a given direction, while avoiding small obstacles and bullets directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    public static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,10,12);
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
    
        if (!rc.hasMoved() && shouldMove(dir)) {
        	
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        //boolean moved = rc.hasMoved();
        int currentCheck = 1;
        
        while(currentCheck <= checksPerSide) {
            // Try the offset of the left side
        	Direction curDir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
        	
            if(!rc.hasMoved() && shouldMove(curDir)) {
                rc.move(curDir);
                return true;
            }
            // Try the offset on the right side
        	curDir = dir.rotateRightDegrees(degreeOffset*currentCheck);

            if(!rc.hasMoved() && shouldMove(curDir)) {
            	rc.move(curDir);
            	return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        return false;
    }
    
    
    
  /*  private static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

    	if(rc.hasMoved())return false;
        // First, try intended direction
    	BulletInfo bullets[] = rc.senseNearbyBullets();
    	
    	boolean isSafe = true;
    	MapLocation loc = rc.getLocation().add(dir, rc.getType().strideRadius);
    	
    	
        if (!rc.hasMoved() && rc.shouldMove(dir)) {
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
        	
            if(!rc.hasMoved() && rc.shouldMove(curDir)) {
            	
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
        	
            if(!rc.hasMoved() && rc.shouldMove(curDir)) {
            	
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
    } */
    
    private static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

    	if(rc.hasMoved())
    		return false;
        // First, try intended direction
    	MapLocation loc = rc.getLocation().add(dir, rc.getType().strideRadius);
    	
    	float furthestHittingBullet = 0;
    	float currentDist;
    	MapLocation BestLocation = loc;

        if (!rc.hasMoved() && shouldMove(dir)) {
        	currentDist = ClosestBulletWillHit(loc);
        	if(currentDist == -1)
        	{
        		rc.move(dir);
        		return true;
        	}
        	else if(currentDist > furthestHittingBullet)
        	{
        		furthestHittingBullet = currentDist;
        		BestLocation = loc;
        	}
        }

        // Now try a bunch of similar angles
        //boolean moved = rc.hasMoved();
        int currentCheck = 1;
        
        while(currentCheck <= checksPerSide) {
            // Try the offset of the left side
        	Direction curDir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
        	loc = rc.getLocation().add(curDir, rc.getType().strideRadius);
        	
            if(!rc.hasMoved() && shouldMove(curDir)) {
            	currentDist = ClosestBulletWillHit(loc);
            	if(currentDist == -1)
            	{
            		rc.move(curDir);
            		return true;
            	}
            	else if(currentDist > furthestHittingBullet)
            	{
            		furthestHittingBullet = currentDist;
            		BestLocation = loc;
            	}
            }
            // Try the offset on the right side
        	curDir = dir.rotateRightDegrees(degreeOffset*currentCheck);
        	loc = rc.getLocation().add(curDir, rc.getType().strideRadius);
        	
            if(!rc.hasMoved() && shouldMove(curDir)) {
            	currentDist = ClosestBulletWillHit(loc);
            	if(currentDist == -1)
            	{
            		rc.move(curDir);
            		return true;
            	}
            	else if(currentDist > furthestHittingBullet)
            	{
            		furthestHittingBullet = currentDist;
            		BestLocation = loc;
            	}
            }
            // No move performed, try slightly further
            currentCheck++;
        }
        if(tryMoveWithoutBulletDetection(rc.getLocation().directionTo(BestLocation), degreeOffset, checksPerSide))
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
    
    
    public static void gardenerWander() throws GameActionException {
    	float angle = 0;
    	
    	MapLocation temp = rc.getLocation();
    	TreeInfo[] trees = rc.senseNearbyTrees(BotGardener.GARDENER_PATCH_RADIUS);
    	for(TreeInfo tree : trees)
    	{
    		temp = temp.add(rc.getLocation().directionTo(tree.getLocation()).opposite(), 2);
    	}
    	
    	if(!temp.equals(rc.getLocation()))
		{
    		lastWander = rc.getLocation().directionTo(temp);
		}
    	
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
    	if (rc.canMove(location) && !rc.hasMoved()) {
    		rc.move(location);
    		return;
    	}
    	tryMove(rc.getLocation().directionTo(location));
    }
}
