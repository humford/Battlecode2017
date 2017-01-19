package first;
import battlecode.common.*;

enum GardenerState {
	PLANTING,
	WATERING,
	PRODUCING
}

public class BotGardener extends Globals {
	public static final int MAX_WANDER_TURNS = 100;
	
	public static void loop() throws GameActionException {
		for (int i = 0; i < MAX_WANDER_TURNS; i++) {
			Pathfinding.wander(); // NEED BETTER FUNCTION TO MOVE
			MapLocation location = rc.getLocation();
			if (!rc.isCircleOccupiedExceptByThisRobot(location, 4f))
				macro();
			Clock.yield();
		}
		macro();
	
	}
	
	// The number of trees + production areas about the gardener
	static final int NUM_SLOTS = 6;
	static final int NUM_TREE_SLOTS = NUM_SLOTS - 1;
	
	static MapLocation myLocation;
	static Direction treeDirs[];
	static Direction productionDirs;
	
	public static MapLocation getTreeSpot(int idx) {
		return rc.getLocation().add(treeDirs[idx], 2);
	}
	
	public static MapLocation getProductionSpot() {
		return rc.getLocation().add(productionDirs, 2);
	}
	
	public static void init() throws GameActionException {
		myLocation = rc.getLocation();
		treeDirs = new Direction[NUM_TREE_SLOTS];
		Direction dir = Direction.NORTH;
		final float stepDegrees = 360.0f / NUM_SLOTS;
		for (int i = 0; i < NUM_TREE_SLOTS; i++) {
			treeDirs[i] = dir;
			dir = dir.rotateLeftDegrees(stepDegrees);
		}
		
		productionDirs = dir;
	}
	
	public static TreeInfo getLowHealthTree() {
		TreeInfo ret = null;
		float minHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
		for (TreeInfo tree : rc.senseNearbyTrees()) {
			if (!tree.team.equals(myTeam)) continue;
			if (tree.health < minHealth) {
				minHealth = tree.health;
				ret = tree;
			}
		}
		return ret;
	}
	
	static void macro() throws GameActionException {
		init();
		while (true) {
			//production 
			
			if(BuildQueue.getLength() > 0)
            {
            	if(rc.canBuildRobot(BuildQueue.peak(), productionDirs))
            	{
            		rc.buildRobot(BuildQueue.dequeue(), productionDirs);
            	}
            }
			
			// Plant missing trees
			for (int i = 0; i < NUM_TREE_SLOTS; i++) {
				MapLocation treeSpot = getTreeSpot(i);
				//System.out.println("Looking at tree in spot " + treeSpot.toString());
				if (rc.isLocationOccupiedByTree(treeSpot)){
					//System.out.println("Occupied");
					continue;
				}
				if (rc.canPlantTree(treeDirs[i])) {
					//System.out.println("Planting tree in direction " + Integer.toString(i));
					rc.plantTree(treeDirs[i]);
					break;
				}
			}
			
			/*if(rc.getRoundNum() < 500 && rc.canBuildRobot(RobotType.LUMBERJACK, productionDirs))
			{
				rc.buildRobot(RobotType.LUMBERJACK, productionDirs);
			}
			
			else if(rc.canBuildRobot(RobotType.SOLDIER, productionDirs))
			{
				rc.buildRobot(RobotType.SOLDIER, productionDirs);
			}*/
			
	
			
			// Water the lowest health tree
			TreeInfo lowHealthTree = getLowHealthTree();
			if (lowHealthTree != null) {
				MapLocation treeSpot = lowHealthTree.getLocation();
				if (rc.canWater(treeSpot))
					rc.water(treeSpot);
			}
			Clock.yield();
		}
	}
}
