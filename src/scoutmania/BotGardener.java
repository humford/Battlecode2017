package scoutmania;
import battlecode.common.*;

public class BotGardener extends Globals {
	public static final int MAX_WANDER_TURNS = 100;
	public static final int GARDENER_CIRCLE_RADIUS = 2;
	public static final int GARDENER_PATCH_RADIUS = 4;
	
	public static void gardener_common() throws GameActionException {
		loop_common();
		
		rc.broadcast(GARDENER_SUM_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) + 1);
		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, false);
		rc.broadcastBoolean(HAS_COUNTED_CHANNEL, false);
	}
	
	public static void loop() throws GameActionException {
		for (int i = 0; i < MAX_WANDER_TURNS; i++) {
			
			gardener_common();
			 
			//production
			
			BuildQueue.tryBuildFromQueue();
            
            
            Micro.dodge();
			
			Pathfinding.wander(); // NEED BETTER FUNCTION TO MOVE
			MapLocation location = rc.getLocation();
			if (!rc.isCircleOccupiedExceptByThisRobot(location, GARDENER_PATCH_RADIUS))
				macro();
			Clock.yield();
		}
		macro();
	}
	
	static MapLocation myLocation;
	static Direction treeDirs[];
	static Direction productionDirs;
	
	public static MapLocation getTreeSpot(int idx) {
		return rc.getLocation().add(treeDirs[idx], GARDENER_CIRCLE_RADIUS);
	}
	
	public static MapLocation getProductionSpot() {
		return rc.getLocation().add(productionDirs, GARDENER_CIRCLE_RADIUS);
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
		for (TreeInfo tree : rc.senseNearbyTrees(GARDENER_CIRCLE_RADIUS, myTeam)) {
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
			gardener_common();
			
			RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
        	Messaging.broadcastDefendMeIF(enemyBots.length > 1);
        	
			//production 
			
			if(BuildQueue.getLength() > 0)
            {
            	if(rc.canBuildRobot(BuildQueue.peak(), productionDirs))
            	{
            		rc.buildRobot(BuildQueue.dequeue(), productionDirs);
            	}
            }
			 
			// Plant missing trees
			for (int i = 0; i < 5; i++) {
				MapLocation treeSpot = getTreeSpot(i);
				//System.out.println("Looking at tree in spot " + treeSpot.toString());
				if (rc.isLocationOccupiedByTree(treeSpot)){
					//System.out.println("Occupied");
					continue;
				}
				if (rc.canPlantTree(treeDirs[i])) {
					//System.out.println("Planting tree in direcion " + Integer.toString(i));
					rc.plantTree(treeDirs[i]);
					break;
				}
			}
	
			
			// Water the lowest health tree
			TreeInfo lowHealthTree = getLowHealthTree();
			if (lowHealthTree != null) {
				MapLocation treeSpot = lowHealthTree.getLocation();
				//System.out.println("ATTEMPTING TO WATER");
				if (rc.canWater(treeSpot))
				{
					rc.water(treeSpot);
					//System.out.println("WATERING");
				}
			}
			Clock.yield();
		}
	}
}
