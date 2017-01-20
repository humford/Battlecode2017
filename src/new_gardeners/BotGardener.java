package new_gardeners;
import battlecode.common.*;

public class BotGardener extends Globals {
	public static boolean plantFirstTree() throws GameActionException {
		float angleDiff = 4;
		Direction dir = Direction.NORTH;
		for (int i = 0; i * angleDiff < 360.0f; i++) {
			if (rc.canPlantTree(dir)) {
				rc.plantTree(dir);
				return true;
			}
			dir = dir.rotateLeftDegrees(angleDiff);
		}
		return false;
	}
	
	public static void loop() throws GameActionException {
		TreeInfo[] trees = rc.senseNearbyTrees();
		if (trees.length == 0) {
			// We are the first gardener
			while(!plantFirstTree()) {}
			do {
				Clock.yield(); // This might be unnecessary
				trees = rc.senseNearbyTrees();
			} while (trees.length == 0);
		}
		followingTree = trees[0];
		
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
		Direction dir = rc.getLocation().directionTo(initialArchonLocations[0]).rotateLeftDegrees(60);
		for (int i = 0; i < 5; i++) {
			treeDirs[i] = dir;
			dir = dir.rotateLeftDegrees(60);
		}
		
		productionDirs = dir;
	}
	
	public static TreeInfo getLowHealthTree() {
		TreeInfo ret = null;
		float minHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
		for (TreeInfo tree : rc.senseNearbyTrees(2, myTeam)) {
			if (tree.health < minHealth) {
				minHealth = tree.health;
				ret = tree;
			}
		}
		return ret;
	}
	
	public static void waterLowHealthTree() throws GameActionException {
		// Water the lowest health tree
		TreeInfo lowHealthTree = getLowHealthTree();
		if (lowHealthTree != null) {
			MapLocation treeSpot = lowHealthTree.getLocation();
			System.out.println("ATTEMPTING TO WATER");
			if (rc.canWater(treeSpot))
			{
				rc.water(treeSpot);
				System.out.println("WATERING");
			}
		}
	}
	
	public static void production() throws GameActionException {
		if(BuildQueue.getLength() > 0)
        {
			Direction dir = randomDirection();
        	if(rc.canBuildRobot(BuildQueue.peak(), dir))
        	{
        		rc.buildRobot(BuildQueue.dequeue(), dir);
        	}
        }
	}
	
	public static TreeInfo followingTree;
	// The number of degrees about the tree the gardener should attempt to move
	public static final float moveDegrees = 20;
	// Whether we are moving counter-clockwise
	public static boolean counterClockwise = false;
	
	// Gets next location moving about the tree
	public static MapLocation getNextLocation() {
		Direction currentAngle = followingTree.location.directionTo(rc.getLocation());
		Direction newAngle;
		if (counterClockwise)
			newAngle = currentAngle.rotateLeftDegrees(moveDegrees);
		else
			newAngle = currentAngle.rotateRightDegrees(moveDegrees);
		return followingTree.getLocation().add(newAngle, rc.getType().bodyRadius + followingTree.radius);
	}
	
	// Probability the gardener will turn around each turn
	public static final float reverseProb = 0.2f;
	
	public static void moveAround() throws GameActionException {
		if (myRand.nextFloat() < reverseProb)
			counterClockwise = !counterClockwise;
		
		MapLocation nextLocation = getNextLocation();
		System.out.println(nextLocation);
		Pathfinding.moveTo(nextLocation);
	}
	
	public static final float maxAngleDiff = 30;
	
	// Switches tree that we're following
	public static TreeInfo getOtherTree() {
		TreeInfo[] trees = rc.senseNearbyTrees();
		Direction currentDir = followingTree.location.directionTo(rc.getLocation());
		for (TreeInfo tree : trees) {
			if (tree.ID == followingTree.ID) continue;
			Direction dir = followingTree.location.directionTo(tree.getLocation());
			if (currentDir.degreesBetween(dir) < maxAngleDiff)
				return tree;
		}
		return null;
	}
	
	public static final float newTreeDist = 5;
	public static final float minTreeSeparation = 4.5f;
	
	public static boolean canBuildNewTree() {
		Direction dir = followingTree.getLocation().directionTo(rc.getLocation());
		MapLocation location = followingTree.getLocation().add(dir, newTreeDist);
		TreeInfo[] trees = rc.senseNearbyTrees();
		for (TreeInfo tree : trees) {
			if (tree.getLocation().distanceTo(location) < minTreeSeparation)
				return false;
		}
		return rc.canPlantTree(dir);
	}
	
	public static boolean buildNewTree() throws GameActionException {
		Direction dir = followingTree.getLocation().directionTo(rc.getLocation());
		MapLocation plantLocation = followingTree.getLocation().add(dir, newTreeDist - rc.getType().bodyRadius - GameConstants.BULLET_TREE_RADIUS);
		Pathfinding.moveTo(plantLocation);
		if (rc.canPlantTree(dir)) {
			rc.plantTree(dir);
			return true;
		}
		return false;
	}
	
	// Probability the gardener will change trees each turn
	public static final float changeProb = 0.01f;
	
	public static void macro() throws GameActionException {
		init();
		while (true) {
			
			loop_common();
			
			if (myRand.nextFloat() < changeProb) {
				TreeInfo nextTree = getOtherTree();
				if (nextTree != null) {
					followingTree = nextTree;
				}
			}
			
			if (myRand.nextFloat() < 0.1 && canBuildNewTree())
				buildNewTree();
			
			moveAround();
			
			rc.broadcast(GARDENER_SUM_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) + 1);
			
			production();
			waterLowHealthTree();
			Clock.yield();
		}
	}
}
