package scoutmania_real;
import battlecode.common.*;

class BotArchon extends Globals {
	
	public static final boolean ARCHON_DEBUG_MODE = true;
	public static int last_gardener_num = 0, last_soldier_num = 0;
	
	public static void makeInitialQueue() throws GameActionException
	{
		BuildQueue.clearQueue();
		TreeInfo[] trees = rc.senseNearbyTrees();
		
		if(initialArchonLocations.length == 1 && rc.getLocation().distanceTo(initialArchonLocations[0]) <= RobotType.ARCHON.sensorRadius)
		{
			System.out.println("CLOSE RUSH");
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SCOUT);
	
			for(int i = 0; i < 2; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
			}
		}
		
		if(isDense()) //DENSE
		{
			System.out.println("DENSE");
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
		
			for(int i = 0; i < 3; i ++)
			{
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
			}
		}
		
		
		else if(rc.readBroadcastBoolean(IS_DENSITY_CHANNEL))
		{
			System.out.println("PART DENSE");
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SCOUT);
		
			for(int i = 0; i < 2; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
			}
		}
		
		else if(initialArchonLocations.length == 1   && (rc.getLocation().distanceTo(initialArchonLocations[0]) <= MAX_RUSH_DISTANCE || trees.length == 0))
		{
			System.out.println("RUSH");
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.SCOUT);
	
			for(int i = 0; i < 2; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
			}
		}
		
		else //OPEN
		{
			System.out.println("OPEN");
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
	
			for(int i = 0; i < 2; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SOLDIER);
			}
		}
	}
	
	public static void initializeChannels() throws GameActionException
	{
		
		Messaging.broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);
		Messaging.broadcastLocation(initialArchonLocations[0], SCOUT_LOC_CHANNEL);
		BuildQueue.clearQueue();
		
	}
	
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			initializeChannels();
			BuildQueue.enqueue(RobotType.GARDENER);
		}
		
		if(isDense())
			rc.broadcastBoolean(IS_DENSITY_CHANNEL, true);
		
		MapLocation bestLoc = BotGardener.getBestLocation();
		if(bestLoc != null)
		{
			int numTreesCanPlant = BotGardener.getOpenTreeSpotsAbout(bestLoc, rc.senseNearbyTrees());
			System.out.println("MY: " + numTreesCanPlant + " BEST: " + rc.readBroadcast(NUM_INIT_TREES_CHANNEL));
		
			if(rc.readBroadcast(NUM_INIT_TREES_CHANNEL) < numTreesCanPlant)
			{
				Messaging.broadcastLocation(bestLoc, START_LOC_CHANNEL);
				rc.broadcast(NUM_INIT_TREES_CHANNEL, numTreesCanPlant);
				rc.broadcast(BEST_ARCHON_ID_CHANNEL, rc.getID());
			}
			rc.setIndicatorDot(bestLoc, 0, 0, 127);
		}
		
		Clock.yield();
		
		
		if(rc.readBroadcast(BEST_ARCHON_ID_CHANNEL) == rc.getID())
		{
			System.out.println("ITS MEE");
			BuildQueue.printQueue();
			plantingList.addLocation(bestLoc);
			if(BuildQueue.tryBuildFromQueue(birthLoc.directionTo(bestLoc)))
        		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
			
			makeInitialQueue();
		}
		
		else if(rc.readBroadcast(BEST_ARCHON_ID_CHANNEL) == 0) //BEAT LILMAZE BITCHES
		{
			for(int i = 360; !buildWhereFree(RobotType.GARDENER, i, rc.getLocation().directionTo(initialArchonLocations[0])); i+= 1);
			rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
			makeInitialQueue();
		}
		
		gridStart = Messaging.recieveLocation(START_LOC_CHANNEL);
		
		Clock.yield();
		
        while (true) {
            try {
            	
            	loop_common();
            	
            	rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) - last_gardener_num);
            	last_gardener_num = rc.readBroadcast(GARDENER_SUM_CHANNEL);
            	
            	rc.broadcast(SOLDIER_COUNT_CHANNEL, rc.readBroadcast(SOLDIER_SUM_CHANNEL) - last_soldier_num);
            	last_soldier_num = rc.readBroadcast(SOLDIER_SUM_CHANNEL);
            	
            	if(ARCHON_DEBUG_MODE)
            	{
            		BuildQueue.printQueue();
            		System.out.println("NUM GARD: " + rc.readBroadcast(GARDENER_COUNT_CHANNEL));
            		System.out.println("ARC: " + rc.readBroadcast(ARCHON_TARGETING_CHANNEL) + " SCO: " + rc.readBroadcast(GARDENER_TARGETING_CHANNEL) + " DEF: " + rc.readBroadcast(DEFENSE_CHANNEL));
            		plantingList.printList();
                	treeList.debug_drawList();
                	System.out.println("NUM TREES TO DIE: " + treeList.getLength());
            		//plantingList.debug();
                	
                	rc.setIndicatorDot(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL), 127, 127, 127);
                	rc.setIndicatorDot(Messaging.recieveLocation(STRIKE_LOC_CHANNEL), 127, 0, 0);
                	rc.setIndicatorDot(Messaging.recieveLocation(SCOUT_LOC_CHANNEL), 100, 20, 30);
            	}
            	
     	
            	//Pathfinding.wander();
            	
            	Micro.dodge();
            	
            	RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
            	Messaging.broadcastDefendMeIF(enemyBots.length >= 1);
                
            	if(rc.readBroadcast(GARDENER_COUNT_CHANNEL) == 0 && BuildQueue.peak() != RobotType.GARDENER && !rc.readBroadcastBoolean(GARDENER_INPRODUCTION_CHANNEL))
            	{
            		BuildQueue.clearQueue();
            		BuildQueue.enqueue(RobotType.GARDENER);
            		System.out.println("FUCK");
            		
            	}
            	
            	
            	if(BuildQueue.tryBuildFromQueue())
            	{
            		rc.broadcast(CANNOT_BUILD_CHANNEL, 0);
            		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
            	}
            	
            	else if(rc.hasRobotBuildRequirements(BuildQueue.peak()) && rc.isBuildReady()) //IF NO SPOTS TO BUILD
            	{
	            	rc.broadcast(CANNOT_BUILD_CHANNEL, rc.readBroadcast(CANNOT_BUILD_CHANNEL) + 1);
            	
            		if(BuildQueue.peak() == RobotType.GARDENER && rc.readBroadcast(CANNOT_BUILD_CHANNEL) >= initialArchonLocations.length) //IF NO ARCHONS CAN BUILD
            		{
            			if(rc.readBroadcast(GARDENER_COUNT_CHANNEL) == 0 && !rc.readBroadcastBoolean(GARDENER_INPRODUCTION_CHANNEL))
            			{
            				for(int i = 360; !buildWhereFree(RobotType.GARDENER, i, rc.getLocation().directionTo(initialArchonLocations[0])); i+= 1);
            				rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
            			}
            			BuildQueue.dequeue();
            			rc.broadcast(CANNOT_BUILD_CHANNEL, 0);
            		}
            	}
                
                
                if(!rc.hasMoved()) //move back to birth location CHANGE THIS!!!!
                {
                	if(rc.getLocation().distanceTo(birthLoc) > 8)
                	{
                		Pathfinding.moveTo(birthLoc);
                	}
                }
                
                end_loop_common();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	public static boolean isDense() throws GameActionException
	{
		Direction dir = Direction.NORTH;
		int count = 0;
		
		for(int i = 0; i < 4; i++)
		{
			TreeInfo[] trees = rc.senseNearbyTrees(birthLoc.add(dir, 4), 3, null);
			if(trees.length >= 2 || !rc.onTheMap(birthLoc.add(dir, 4), 1.99f))
				count ++;
			dir = dir.rotateLeftDegrees(90);
		}
		
		return (count >= 3);
	}
}
