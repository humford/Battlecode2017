package scoutmania;
import battlecode.common.*;
import java.util.*;

public class Globals {
	
	static RobotController rc;
    static Random myRand;
    static MapLocation birthLoc;
    static MapLocation gridStart;
    static ArrayList<MapLocation> treesToAdd = new ArrayList<MapLocation>();
    static int current_round;
    
    static int GARDENER_UPPER_LIMIT;
    static final int MAX_RUSH_DISTANCE = 25;
    static final int LIST_HAZARD_LENGTH = 75;
    
    // Keep broadcast channels
    static final int GARDENER_COUNT_CHANNEL = 1;
    static final int GARDENER_SUM_CHANNEL = 2;
    static final int GARDENER_INPRODUCTION_CHANNEL = 4;
    
    static final int SOLDIER_COUNT_CHANNEL = 17;
    static final int SOLDIER_SUM_CHANNEL = 18;
    
    //LOC_CHANNELS use next integer channel as well
    static final int STRIKE_LOC_CHANNEL = 5;
    static final int DEFENSE_LOC_CHANNEL = 7;
    static final int SCOUT_LOC_CHANNEL = 9;
    
    //FLAG_CHANNELS
    static final int ARCHON_TARGETING_CHANNEL = 11;
    static final int GARDENER_TARGETING_CHANNEL = 12;
    static final int DEFENSE_CHANNEL = 13;
    static final int ANIT_RUSH_CHANNEL = 14;
    static final int CANNOT_BUILD_CHANNEL = 14;


	static TreeSet<Integer> seenSoldiers;
	static TreeSet<Integer> seenTanks;
	static final int ENEMY_SOLDIER_COUNT_CHANNEL = 15;
	static final int ENEMY_TANK_COUNT_CHANNEL = 16;
    
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
    
    //INITIALIZATION CHANNELS
    
    static final int START_LOC_CHANNEL = 100;
	static final int NUM_INIT_TREES_CHANNEL = 102;
	static final int BEST_ARCHON_ID_CHANNEL = 99;
	static final int IS_DENSITY_CHANNEL = 98; 
    
    
    // LOCATION LISTS
    //USES CHANNELS 0945 - 1847
    static LocationList treeList;
    //USES CHANNELS 1848 - 2750
    static LocationList plantingList;
    //USES CHANNELS 2751 - 3653
    static LocationList trashList;
    //USES CHANNELS 3654 - 4556
    static LocationList helpList;
	

    public static void init(RobotController theRC) throws GameActionException {

    	rc = theRC;
    	
    	myTeam = rc.getTeam();
    	them = myTeam.opponent();
    	initialArchonLocations = rc.getInitialArchonLocations(them);
    	birthLoc = rc.getLocation();

    	RobotPlayer.rc = theRC;
        myRand = new Random(rc.getID());
        Pathfinding.lastWander = randomDirection();
        
        treeList = new LocationList(945, 1847);
        plantingList = new LocationList(1848, 2750);
        trashList = new LocationList(2751, 3653);
        helpList = new LocationList(3654, 4556);
        
        gridStart = Messaging.recieveLocation(START_LOC_CHANNEL);
        GARDENER_UPPER_LIMIT = 5 * initialArchonLocations.length;

		seenSoldiers = new TreeSet<Integer>();
		seenTanks = new TreeSet<Integer>();
       
    }


    public static Direction randomDirection() {
        return(new Direction(myRand.nextFloat()*2*(float)Math.PI));
    }
    
    public static boolean buildWhereFree(RobotType bot, int check, Direction dir) throws GameActionException {
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

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
    static float distToCollidingBullet(BulletInfo bullet, MapLocation myLocation) {

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2 && distToRobot > rc.getType().bodyRadius) {
            return -1;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)
        if(perpendicularDist <= rc.getType().bodyRadius)
        	return perpendicularDist;
        else
        	return -1;
    }
    
    static float ClosestBulletWillHit(MapLocation location)
    {
    	BulletInfo[] bullets = rc.senseNearbyBullets();
    	if(bullets.length == 0)
    		return -1;
    	float closest = distToCollidingBullet(bullets[0], location);
    	
    	for(BulletInfo b : bullets)
    	{
    		float curDist = distToCollidingBullet(b, location);
    		if(curDist < closest || closest == -1)
    		{
    			closest = curDist;
    		}
    	}
    	return closest;
    }
    
    static boolean bulletBlocked(Direction dir, float distTo) {

        //For each friendly or neutral object within distTo check if the bullet would hit it
    	RobotInfo friendlyRobots[] = rc.senseNearbyRobots(distTo, myTeam);
    	for(RobotInfo bot : friendlyRobots)
    	{
    		Direction directionToRobot = rc.getLocation().directionTo(bot.getLocation());
            float distToRobot = rc.getLocation().distanceTo(bot.getLocation());
            float theta = dir.radiansBetween(directionToRobot);
            
            // If theta > 90 degrees, then the bullet is traveling away from the robot and we can ignore it
            if (Math.abs(theta) > Math.PI / 2 && distToRobot + bot.getRadius() > rc.getType().bodyRadius + GameConstants.BULLET_SPAWN_OFFSET) 
                continue;
            
            // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
            // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
            // This corresponds to the smallest radius circle centered at our location that would intersect with the
            // line that is the path of the bullet.
            
            float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta));
            if(perpendicularDist <= bot.getRadius() * 1.1)
            	return true;
    	}
    	
    	TreeInfo nonEnemyTrees[] = rc.senseNearbyTrees(distTo);
    	for(TreeInfo tree : nonEnemyTrees)
    	{
    		if(tree.getTeam() != them)
    		{
        		Direction directionToRobot = rc.getLocation().directionTo(tree.getLocation());
                float distToRobot = rc.getLocation().distanceTo(tree.getLocation());
                float theta = dir.radiansBetween(directionToRobot);
                
                // If theta > 90 degrees, then the bullet is traveling away from the robot and we can ignore it
                if (Math.abs(theta) > Math.PI / 2 && distToRobot + tree.getRadius() > rc.getType().bodyRadius + GameConstants.BULLET_SPAWN_OFFSET) 
                    continue;
                
                // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
                // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
                // This corresponds to the smallest radius circle centered at our location that would intersect with the
                // line that is the path of the bullet.
                
                float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta));
                if(perpendicularDist <= tree.getRadius() * 1.25)
                	return true;
    		}
    	}
    	
    	return false;
    	
    }
    
    public static boolean PentadShotOpen(RobotInfo target) throws GameActionException
    {
    	Direction dirTarget = rc.getLocation().directionTo(target.getLocation());
    	float distTarget = rc.getLocation().distanceTo(target.getLocation());
    	
    	//false if forward direction blocked 
    	
    	if(bulletBlocked(dirTarget, distTarget))
    		return false;
    	
    	//false if left directions blocked 
    	
    	for(int i = 1; i <= 2; i ++)
    	{
    		Direction dir = dirTarget.rotateLeftDegrees(15 * i);
    		if(bulletBlocked(dir, rc.getType().sensorRadius))
    			return false;
    	}
    	
    	//false if right directions blocked 
    	
    	for(int i = 1; i <= 2; i ++)
    	{
    		Direction dir = dirTarget.rotateRightDegrees(15 * i);
    		if(bulletBlocked(dir, rc.getType().sensorRadius))
    			return false;
    	}
    	
    	return true;
    }
    
    public static boolean TriadShotOpen(RobotInfo target) throws GameActionException
    {
    	Direction dirTarget = rc.getLocation().directionTo(target.getLocation());
    	float distTarget = rc.getLocation().distanceTo(target.getLocation());
    	
    	//false if forward direction blocked 
    	
    	if(bulletBlocked(dirTarget, distTarget))
    		return false;
    	
    	//false if left direction blocked 
    	
    	Direction dir = dirTarget.rotateLeftDegrees(20);
		if(bulletBlocked(dir, rc.getType().sensorRadius))
			return false;
    	
    	//false if right directions blocked 
    	
		dir = dirTarget.rotateRightDegrees(20);
		if(bulletBlocked(dir, rc.getType().sensorRadius))
			return false;
    	
    	return true;
    }
    
    public static boolean SingleShotOpen(RobotInfo target) throws GameActionException
    {
    	Direction dirTarget = rc.getLocation().directionTo(target.getLocation());
    	float distTarget = rc.getLocation().distanceTo(target.getLocation());
    	
    	//false if forward direction blocked 
    	
    	if(bulletBlocked(dirTarget, distTarget))
    		return false;
    	else
    		return true;
    }
 
	public static void locateType(RobotType type, int targetChannel, int locChannel) throws GameActionException 
	{
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, them);
		for (RobotInfo enemy : nearbyEnemies) {
			if (enemy.type == type) {
				Messaging.broadcastLocation(enemy.getLocation(), locChannel);
				if(rc.readBroadcast(targetChannel) == -1)
					rc.broadcast(targetChannel, initialArchonLocations.length);
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
    		if((rc.readBroadcast(GARDENER_COUNT_CHANNEL) < GARDENER_UPPER_LIMIT && !plantingList.IsEmpty()) || (rc.readBroadcast(GARDENER_COUNT_CHANNEL) < 2*GARDENER_UPPER_LIMIT && plantingList.getLength() >= 4))
        	{
    			BuildQueue.enqueue(RobotType.GARDENER);
    			BuildQueue.enqueue(RobotType.SOLDIER);
        	}
    		else if(!treeList.IsEmpty())
    		{
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SOLDIER);
    		}
    		else
    		{
    			BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.SCOUT);
    		}
    	}
		int num_soldiers = rc.readBroadcast(SOLDIER_COUNT_CHANNEL);
		int num_enemy_soldiers = rc.readBroadcast(ENEMY_SOLDIER_COUNT_CHANNEL);
		if (num_soldiers < num_enemy_soldiers) {
			BuildQueue.enqueue(RobotType.SOLDIER);
		}
	}
	
	public static void loop_common() throws GameActionException // things that all robots do in loop
	{
		current_round = rc.getRoundNum();
		donate();
		refillQueue();
		
		locateType(RobotType.ARCHON, ARCHON_TARGETING_CHANNEL, STRIKE_LOC_CHANNEL);
		locateType(RobotType.GARDENER, GARDENER_TARGETING_CHANNEL, SCOUT_LOC_CHANNEL);
		
		TreeInfo[] trees = rc.senseNearbyTrees();
    	for (TreeInfo t : trees) {
    		if(rc.canShake(t.getLocation()) && t.containedBullets > 0){
    			rc.shake(t.getLocation());
    			break;
    		}
    	}
	}
	
	public static void end_loop_common() throws GameActionException // things that all robots do at end of loop
	{
		BotGardener.addGridLocation();
		
		//GET OUT OF THE WAY OF GARDENERS
		if(!rc.hasMoved())
		{
			RobotInfo[] myBots = rc.senseNearbyRobots(BotGardener.GARDENER_PATCH_RADIUS, myTeam);
			
			if(rc.getType() != RobotType.GARDENER)
			{
				for(RobotInfo bot : myBots)
				{
					if(bot.getType() == RobotType.GARDENER)
					{
						Pathfinding.tryMove(rc.getLocation().directionTo(bot.getLocation()).opposite());
						break;
					}
				}
			}
		}

		if(treeList.getLength() < LIST_HAZARD_LENGTH)
		{
			TreeInfo[] trees = rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL);
			for (TreeInfo tree : trees) {
				if (tree.getContainedRobot() != null)
				{
					treeList.addLocation(tree.location);
					break;
				}
			}
		}

		// Count enemy soldiers/tanks
		RobotInfo[] theirBots = rc.senseNearbyRobots(rc.getType().sensorRadius, them);
		for (RobotInfo bot : theirBots) {
			if (bot.getType() == RobotType.SOLDIER)
				seenSoldiers.add(bot.getID());
			if (bot.getType() == RobotType.TANK)
				seenTanks.add(bot.getID());
		}

		if (rc.readBroadcast(ENEMY_SOLDIER_COUNT_CHANNEL) < seenSoldiers.size())
			rc.broadcast(ENEMY_TANK_COUNT_CHANNEL, seenSoldiers.size());
		if (rc.readBroadcast(ENEMY_TANK_COUNT_CHANNEL) < seenTanks.size())
			rc.broadcast(ENEMY_TANK_COUNT_CHANNEL, seenTanks.size());

		System.out.println("Seen " + Integer.toString(seenSoldiers.size()) + " soldiers");
		
		if(current_round == rc.getRoundNum())
			Clock.yield();
		else
			System.out.println("RAN OUT OF BYECODE");
			
	}
}


