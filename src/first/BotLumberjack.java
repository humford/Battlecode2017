package first;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	
	public static void loop() throws GameActionException {
        while (true) {
            try {
            	locateArchon();
            	
            	int prev = rc.readBroadcast(LUMBERJACK_CHANNEL);
		    	rc.broadcast(LUMBERJACK_CHANNEL, prev+1);
		    	
                Pathfinding.dodge();
                
                RobotInfo[] myBots = rc.senseNearbyRobots(2, myTeam);
                RobotInfo[] theirBots = rc.senseNearbyRobots(2, them);
                if(myBots.length < theirBots.length && rc.canStrike())rc.strike();
                
                Micro.chase();
                
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
                if (! rc.hasMoved()) {
                	Pathfinding.tryMove(rc.getLocation().directionTo(recieveLocation(STRIKE_LOC_CHANNEL)));
                }
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
