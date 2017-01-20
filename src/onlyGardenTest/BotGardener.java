package onlyGardenTest;
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
			
			locateArchon();
			
			rc.broadcast(GARDENER_SUM_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) + 1);
			 
			//production
			
			Direction dir = randomDirection();
        	
            if(BuildQueue.getLength() > 0)
            {
            	if(rc.canBuildRobot(BuildQueue.peak(), dir))
            	{
            		rc.buildRobot(BuildQueue.dequeue(), dir);
            	}
            }
			
			Pathfinding.wander(); // NEED BETTER FUNCTION TO MOVE
			MapLocation location = rc.getLocation();
			if (!rc.isCircleOccupiedExceptByThisRobot(location, 4f))
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
		treeDirs = new Direction[6];
		Direction dir = rc.getLocation().directionTo(initialArchonLocations[0]).rotateLeftDegrees(60);
		for (int i = 0; i < 6; i++) {
			treeDirs[i] = dir;
			dir = dir.rotateLeftDegrees(60);
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
			
			locateArchon();
			
			rc.broadcast(GARDENER_SUM_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) + 1);
			
			//production 
			
			if(BuildQueue.getLength() > 0)
            {
            	if(rc.canBuildRobot(BuildQueue.peak(), productionDirs))
            	{
            		rc.buildRobot(BuildQueue.dequeue(), productionDirs);
            	}
            }
			
			// Plant missing trees
			for (int i = 0; i < 6; i++) {
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
