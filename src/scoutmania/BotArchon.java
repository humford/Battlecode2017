package scoutmania;
import battlecode.common.*;

class BotArchon extends Globals {
	
	public static final boolean ARCHON_DEBUG_MODE = true;
	
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
		}
		
        while (true) {
            try {
            	loop_common();
            	
            	if(!rc.readBroadcastBoolean(HAS_COUNTED_CHANNEL))
            	{
            		rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL));
            		rc.broadcast(GARDENER_SUM_CHANNEL, 0);
            		rc.broadcastBoolean(HAS_COUNTED_CHANNEL, true);
            	}
            	
            	if(ARCHON_DEBUG_MODE)
            	{
            		BuildQueue.printQueue();
            		System.out.println("NUM GARD: " + rc.readBroadcast(GARDENER_COUNT_CHANNEL));
            		System.out.println("ARC: " + rc.readBroadcast(ARCHON_TARGETING_CHANNEL) + " SCO: " + rc.readBroadcast(GARDENER_TARGETING_CHANNEL));
            	}
            	
            	rc.setIndicatorDot(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL), 0, 0, 0);
            	rc.setIndicatorDot(Messaging.recieveLocation(STRIKE_LOC_CHANNEL), 127, 0, 0);
            	rc.setIndicatorDot(Messaging.recieveLocation(SCOUT_LOC_CHANNEL), 100, 20, 30);
     	
            	//Pathfinding.wander();
            	
            	Micro.dodge();
            	
            	RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
            	Messaging.broadcastDefendMeIF(enemyBots.length > 2);
                
            	if(rc.readBroadcast(GARDENER_COUNT_CHANNEL) == 0 && BuildQueue.peak() != RobotType.GARDENER && !rc.readBroadcastBoolean(GARDENER_INPRODUCTION_CHANNEL))
            	{
            		BuildQueue.clearQueue();
            		BuildQueue.enqueue(RobotType.GARDENER);
            		System.out.println("FUCK");
            		
            	}
            	
            	
            	if(BuildQueue.tryBuildFromQueue())rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, true);
                
                
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
