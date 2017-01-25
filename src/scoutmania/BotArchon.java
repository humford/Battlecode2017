package scoutmania;
import battlecode.common.*;

class BotArchon extends Globals {
	
	public static final boolean ARCHON_DEBUG_MODE = true;
	public static int last_gardener_num = 0;
	
	public static void makeInitialQueue() throws GameActionException
	{
		BuildQueue.clearQueue();
		if(rc.senseNearbyTrees().length > 3) //DENSE
		{
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
		
			for(int i = 0; i < 5; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SCOUT);
			}
		}
		
		else
		{
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.SOLDIER);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.LUMBERJACK);
	
			for(int i = 0; i < 5; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SCOUT);
				BuildQueue.enqueue(RobotType.SCOUT);
			}
		}
	}
	
	public static void initializeChannels() throws GameActionException
	{
		
		Messaging.broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);
		Messaging.broadcastLocation(initialArchonLocations[0], SCOUT_LOC_CHANNEL);
		
		
	}
	
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			initializeChannels();
			makeInitialQueue();
			rc.broadcast(BEST_ARCHON_ID_CHANNEL, rc.getID());
		}
		
		MapLocation bestLoc = BotGardener.getBestLocation();
		int numTreesCanPlant = BotGardener.getOpenTreeSpotsAbout(bestLoc);
		
		if(rc.readBroadcast(NUM_INIT_TREES_CHANNEL) < numTreesCanPlant)
		{
			Messaging.broadcastLocation(bestLoc, START_LOC_CHANNEL);
			rc.broadcast(NUM_INIT_TREES_CHANNEL, numTreesCanPlant);
			rc.broadcast(BEST_ARCHON_ID_CHANNEL, rc.getID());
		}
		
		rc.setIndicatorDot(bestLoc, 0, 0, 127);
		rc.setIndicatorDot(Messaging.recieveLocation(START_LOC_CHANNEL), 0, 127, 0);
		System.out.println(rc.readBroadcast(NUM_INIT_TREES_CHANNEL));
		System.out.println(rc.readBroadcast(BEST_ARCHON_ID_CHANNEL));
		
		Clock.yield();
		
		if(rc.readBroadcast(BEST_ARCHON_ID_CHANNEL) == rc.getID())
		{
			plantingList.addLocation(bestLoc);
			if(BuildQueue.tryBuildFromQueue(birthLoc.directionTo(bestLoc)))
        		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
		}
		
		gridStart = Messaging.recieveLocation(START_LOC_CHANNEL);
		
        while (true) {
            try {
            	loop_common();
            	
            	rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) - last_gardener_num);
            	last_gardener_num = rc.readBroadcast(GARDENER_SUM_CHANNEL);
            	
            	if(ARCHON_DEBUG_MODE)
            	{
            		BuildQueue.printQueue();
            		System.out.println("NUM GARD: " + rc.readBroadcast(GARDENER_COUNT_CHANNEL));
            		System.out.println("ARC: " + rc.readBroadcast(ARCHON_TARGETING_CHANNEL) + " SCO: " + rc.readBroadcast(GARDENER_TARGETING_CHANNEL) + " DEF: " + rc.readBroadcast(DEFENSE_CHANNEL));
            		//plantingList.printList();
            		//plantingList.debug();
            	}
            	
            	rc.setIndicatorDot(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL), 127, 127, 127);
            	rc.setIndicatorDot(Messaging.recieveLocation(STRIKE_LOC_CHANNEL), 127, 0, 0);
            	rc.setIndicatorDot(Messaging.recieveLocation(SCOUT_LOC_CHANNEL), 100, 20, 30);
     	
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
            		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
                
                
                if(!rc.hasMoved()) //move back to birth location CHANGE THIS!!!!
                {
                	if(rc.getLocation().distanceTo(birthLoc) > 1)
                	{
                		Pathfinding.moveTo(birthLoc);
                	}
                }
                
                	
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
