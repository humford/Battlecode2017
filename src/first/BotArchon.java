package first;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			rc.broadcast(BuildQueue.POINTER_CHANNEL, BuildQueue.low_endpoint);
			
			broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);

			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.SCOUT);
		
			for(int i = 0; i < 5; i ++)
			{
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.LUMBERJACK);
				BuildQueue.enqueue(RobotType.GARDENER);
				BuildQueue.enqueue(RobotType.SOLDIER);
			}
			
			BuildQueue.printQueue();
		}
		
        while (true) {
            try {
            	
            	if(BuildQueue.getLength() <= 0)
            	{
            		BuildQueue.enqueue(RobotType.GARDENER);
    				BuildQueue.enqueue(RobotType.LUMBERJACK);
    				BuildQueue.enqueue(RobotType.SOLDIER);
    				BuildQueue.enqueue(RobotType.SOLDIER);
            	}
            	
            	if(rc.getTeamBullets() > VICTORY_CASH)
            	{
            		int x = (int)rc.getTeamBullets() - VICTORY_CASH;
            		rc.donate(x - x%10);
            	}
            	
            	locateArchon();
            	
            	//Pathfinding.wander();
            	
            	Pathfinding.dodge();
            	
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
