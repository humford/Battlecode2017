package first;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		rc.broadcast(CHARGE_CHANNEL, 1);
		
        while (true) {
            try {
            	
            	if(rc.getTeamBullets() > VICTORY_CASH)
            	{
            		int x = (int)rc.getTeamBullets() - VICTORY_CASH;
            		rc.donate(x - x%10);
            	}
            	
            	locateArchon();
            	
            	Pathfinding.wander();
                Direction dir = randomDirection();
                int prevNumGard = rc.readBroadcast(GARDENER_CHANNEL);
                rc.broadcast(GARDENER_CHANNEL, 0);
                if (prevNumGard < TREES_GARDENER_MAX && rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                    rc.broadcast(GARDENER_CHANNEL, prevNumGard + 1);
                }
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
