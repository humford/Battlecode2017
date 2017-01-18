package first;
import battlecode.common.*;

public class BotGardener extends Globals {
	public static void loop() throws GameActionException {
        while (true) {
            try {
                dodge();
                System.out.println(Clock.getBytecodesLeft());
                int prev = rc.readBroadcast(GARDENER_CHANNEL);
                rc.broadcast(GARDENER_CHANNEL, prev+1);
                wander();
                Direction dir = randomDirection();
                if (rc.getRoundNum() < 500) {
                    int prevNumGard = rc.readBroadcast(LUMBERJACK_CHANNEL);
                    if (prevNumGard <= LUMBERJACK_MAX && rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                        rc.broadcast(LUMBERJACK_CHANNEL, prevNumGard + 1);
                    }
                }
                else {
                    if (rc.canBuildRobot(RobotType.SOLDIER, Direction.getEast())) {
                        rc.buildRobot(RobotType.SOLDIER, Direction.getEast());
                    }
                }

                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
