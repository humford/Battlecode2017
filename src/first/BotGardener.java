package first;
import battlecode.common.*;

enum GardenerState {
	PLANTING,
	WATERING,
	PRODUCING
}

public class BotGardener extends Globals {
	public static final int MAX_WANDER_TURNS = 5;
	
	public static void loop() throws GameActionException {
		for (int i = 0; i < MAX_WANDER_TURNS; i++) {
			MapLocation location = rc.getLocation();
			if (rc.isCircleOccupiedExceptByThisRobot(location, 3.8f));
				macro();
			Clock.yield();
		}
		macro();
	}
	
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
		treeDirs = new Direction[5];
		Direction dir = Direction.NORTH;
		for (int i = 0; i < 5; i++) {
			treeDirs[i] = dir;
			dir = dir.rotateLeftDegrees(60);
		}
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
			// Plant missing trees
			for (int i = 0; i < 5; i++) {
				MapLocation treeSpot = getTreeSpot(i);
				System.out.println("Looking at tree in spot " + treeSpot.toString());
				if (rc.isLocationOccupiedByTree(treeSpot)){
					System.out.println("Occupied");
					continue;
				}
				if (rc.canPlantTree(treeDirs[i])) {
					System.out.println("Planting tree in direcion " + Integer.toString(i));
					rc.plantTree(treeDirs[i]);
					break;
				}
			}
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
