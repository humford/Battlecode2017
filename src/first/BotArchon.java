package first;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			rc.broadcast(CHARGE_CHANNEL, 1);
			rc.broadcast(BuildQueue.POINTER_CHANNEL, BuildQueue.low_endpoint);

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
                
               /* int prevNumGard = rc.readBroadcast(GARDENER_CHANNEL);
                if (prevNumGard < GARDENER_MAX && rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                    rc.broadcast(GARDENER_CHANNEL, prevNumGard + 1);
                }*/
                
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
