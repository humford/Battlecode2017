package onlyGarden;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			rc.broadcast(BuildQueue.POINTER_CHANNEL, BuildQueue.low_endpoint);
			
			broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);
			
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.GARDENER);

		}
		
        while (true) {
            try {
            	
            	rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL));
            	rc.broadcast(GARDENER_SUM_CHANNEL, 0);

            	if(BuildQueue.getLength() <= 0)
            	{
            		BuildQueue.enqueue(RobotType.GARDENER);
            	}
            	
            	if(rc.getTeamBullets() > VICTORY_CASH)
            	{
            		int x = (int)rc.getTeamBullets() - VICTORY_CASH;
            		rc.donate(x - x%10);
            	}
            	
            	locateArchon();
            	
            	//Pathfinding.wander();
            	
            	Pathfinding.dodge();
            	
            	RobotInfo[] enemyBots = rc.senseNearbyRobots(5, them);
            	
            	if(enemyBots.length > 2)
            	{
            		broadcastLocation(rc.getLocation(), DEFENSE_LOC_CHANNEL);
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
