package first;
import battlecode.common.*;

public class BotTank extends Globals {
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
                for (RobotInfo b : bots) {
                    if (b.getTeam() != rc.getTeam()) {
                        Direction towards = rc.getLocation().directionTo(b.getLocation());
                        rc.fireSingleShot(towards);
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
