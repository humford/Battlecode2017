package first;
import battlecode.common.*;

enum ScoutState {
	WANDER,
	FOLLOW_ARCHON,
}

public class BotScout extends Globals {
	
	static ScoutState state;
	
	// The location of the archon this scout is following if state is FOLLOW_ARCHON
	static MapLocation lastKnownArchonLocation;
	
	public static void loop() throws GameActionException {
		Globals.init(rc);
		
		state = ScoutState.FOLLOW_ARCHON;
		lastKnownArchonLocation = Globals.initialArchonLocations[0];
		//lastKnownArchonLocation = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
		while (true) {
			switch (state) {
			case WANDER:
				Pathfinding.wander();
				break;
			case FOLLOW_ARCHON:
				follow_archon();
				break;
			}
			Clock.yield();
		}
	}
	
	public static void follow_archon() throws GameActionException {
		RobotInfo robotAtLastKnownLocation = null;
		if (rc.canSenseLocation(lastKnownArchonLocation))
			robotAtLastKnownLocation = rc.senseRobotAtLocation(lastKnownArchonLocation);
		
		if (robotAtLastKnownLocation != null && robotAtLastKnownLocation.getType() == RobotType.ARCHON) {
			// The archon might move but still be occupying it's last location
			lastKnownArchonLocation = robotAtLastKnownLocation.getLocation();
		}
		else {
			RobotInfo nearbyRobots[] = rc.senseNearbyRobots();
			for (RobotInfo robot : nearbyRobots) {
				if (robot.team.equals(Globals.myTeam)) continue;
				if (robot.type.equals(RobotType.ARCHON))
				{
					lastKnownArchonLocation = robot.getLocation();
					break;
				}
			}
		}
		if (rc.getLocation().distanceTo(lastKnownArchonLocation) > 0.001)
		{
			System.out.println("Attempting to move to (" + lastKnownArchonLocation.toString() + ")");
			Pathfinding.tryMove(rc.getLocation().directionTo(lastKnownArchonLocation));
		}
		else // We've lost track of the archon
		{
			System.out.println("Wandering");
			Pathfinding.wander();
		}
	}
}
