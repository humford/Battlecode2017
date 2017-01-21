package onlyGarden;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	
	public static void loop() throws GameActionException {
        while (true) {
            try {
            	locateArchon();
	    	
            	Micro.dodge();
                
                RobotInfo[] myBots = rc.senseNearbyRobots(2, myTeam);
                RobotInfo[] theirBots = rc.senseNearbyRobots(2, them);
                boolean isArchon = false;
                
                for(RobotInfo b : theirBots)
                {
                	if(b.getType() == RobotType.ARCHON)
                	{
                		isArchon = true;
                		break;
                	}
                }
                
                if((myBots.length < theirBots.length || isArchon) && rc.canStrike())rc.strike();
                
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
