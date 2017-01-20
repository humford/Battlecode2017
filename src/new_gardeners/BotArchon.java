package new_gardeners;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			rc.broadcast(BuildQueue.POINTER_CHANNEL, BuildQueue.low_endpoint);
			
			Messaging.broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);
			
			if(rc.senseNearbyTrees().length > 3)
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
					BuildQueue.enqueue(RobotType.GARDENER);
					BuildQueue.enqueue(RobotType.SOLDIER);
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
					BuildQueue.enqueue(RobotType.LUMBERJACK);
					BuildQueue.enqueue(RobotType.GARDENER);
					BuildQueue.enqueue(RobotType.SOLDIER);
				}
			}
			
			BuildQueue.printQueue();
		}
		
        while (true) {
            try {
            	
            	loop_common();
            	rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL));
            	rc.broadcast(GARDENER_SUM_CHANNEL, 0);

            	if(BuildQueue.getLength() <= 0)
            	{
            		if(rc.readBroadcast(GARDENER_COUNT_CHANNEL) < GARDENER_LOWER_LIMIT)
                	{
            			BuildQueue.enqueue(RobotType.GARDENER);
                	}
            		else
            		{
            			BuildQueue.enqueue(RobotType.GARDENER);
    					BuildQueue.enqueue(RobotType.LUMBERJACK);
    					BuildQueue.enqueue(RobotType.SOLDIER);
    					BuildQueue.enqueue(RobotType.SOLDIER);
            		}
            	}
            	
            	if(rc.getTeamBullets() > VICTORY_CASH)
            	{
            		int x = (int)rc.getTeamBullets() - VICTORY_CASH;
            		rc.donate(x - x%10);
            	}
            	
            	
            	//Pathfinding.wander();
            	
            	Pathfinding.dodge();
            	
            	RobotInfo[] enemyBots = rc.senseNearbyRobots(rc.getType().sensorRadius, them);
            	
            	if(enemyBots.length > 2)
            	{
            		Messaging.broadcastLocation(rc.getLocation(), DEFENSE_LOC_CHANNEL);
            		rc.broadcast(DEFENSE_CHANNEL, 1);
            	}
            	
            	else rc.broadcast(DEFENSE_CHANNEL, 0);
                
            	Direction dir = randomDirection();
            	
                if(BuildQueue.getLength() > 0)
                {
                	if(rc.canBuildRobot(BuildQueue.peak(), dir))
                	{
                		rc.buildRobot(BuildQueue.dequeue(), dir);
                	}
                }
         
                	
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
