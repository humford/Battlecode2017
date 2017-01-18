package first;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	
	public static void loop() throws GameActionException {
        while (true) {
            try {
            	locateArchon();
            	
                dodge();
                
                RobotInfo[] bots = rc.senseNearbyRobots();
                for (RobotInfo b : bots) {
                    if (b.getTeam() != myTeam && rc.canStrike()) {
                        rc.strike();
                        Direction chase = rc.getLocation().directionTo(b.getLocation());
                        tryMove(chase);
                        break;
                    }
                }
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
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
