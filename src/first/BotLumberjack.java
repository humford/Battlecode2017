package first;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	
	public static void loop() throws GameActionException {
        while (true) {
            try {
            	locateArchon();
            	
            	int prev = rc.readBroadcast(LUMBER_ALIVE_CHANNEL);
		    	rc.broadcast(LUMBER_ALIVE_CHANNEL, prev+1);
		    	
                Pathfinding.dodge();
                
                RobotInfo[] bots = rc.senseNearbyRobots();
                for (RobotInfo b : bots) {
                    if (b.getTeam() != myTeam && rc.canStrike()) {
                        rc.strike();
                        Direction chase = rc.getLocation().directionTo(b.getLocation());
                        Pathfinding.tryMove(chase);
                        break;
                    }
                }
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
                	if(rc.canShake(t.getLocation()) && t.containedBullets > 0){
                		rc.shake(t.getLocation());
                        break;
                	}
                    if (t.getTeam() != myTeam && rc.canChop(t.getLocation())) {
                        rc.chop(t.getLocation());
                        break;
                    }
                }
                if (! rc.hasAttacked()) {
                	moveTwardArchon();
                }
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
