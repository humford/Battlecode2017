package scoutmania;
import java.util.ArrayList;
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
		
		RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
    	Messaging.broadcastDefendMeIF(enemyBots.length > 1);
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
	static MapLocation treeLocations[];
	static Direction productionDirs;
	
	public static MapLocation getTreeSpot(int idx) {
		return treeLocations[idx];
	}
	
	public static MapLocation getProductionSpot() {
		return rc.getLocation().add(productionDirs, GARDENER_CIRCLE_RADIUS);
	}
	
	public static Direction[] getTreeDirs() throws GameActionException {
		Direction[] ret = new Direction[5];
		Direction dir = rc.getLocation().directionTo(initialArchonLocations[0]).rotateLeftDegrees(60);
		for (int i = 0; i < 5; i++) {
			ret[i] = dir;
			dir = dir.rotateLeftDegrees(60);
		}
		return ret;
	}
	
	public static Direction getProductionDirection() throws GameActionException {
		return rc.getLocation().directionTo(initialArchonLocations[0]).rotateLeftDegrees(60);
	}
	
	public static MapLocation[] getTreeSpotsAbout(MapLocation location) throws GameActionException {
		MapLocation ret[] = new MapLocation[5];
		if (treeDirs == null) treeDirs = getTreeDirs();
		for (int i = 0; i < 5; i++) {
			ret[i] = location.add(treeDirs[i], GARDENER_CIRCLE_RADIUS);
		}
		return ret;
	}
	
	public static void init() throws GameActionException {
		myLocation = rc.getLocation();
		treeDirs = getTreeDirs();
		treeLocations = getTreeSpotsAbout(myLocation);
		
		productionDirs = getProductionDirection();
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
	
	public static int getOpenTreeSpotsAbout(MapLocation location) throws GameActionException {
		if (!rc.onTheMap(location)) return 0;
		if(rc.isCircleOccupied(location, RobotType.GARDENER.bodyRadius)) return -1;
		MapLocation[] spots = getTreeSpotsAbout(location);
		TreeInfo[] trees = rc.senseNearbyTrees(location, GARDENER_PATCH_RADIUS, myTeam);
		int ret = 0;
		
		for (MapLocation spot : spots) {
			boolean collision = false;
			if (!rc.onTheMap(spot)) continue;
			for (TreeInfo tree : trees) {
				if (MapLocation.doCirclesCollide(spot, GameConstants.BULLET_TREE_RADIUS, tree.getLocation(), tree.getRadius())) {
					collision = true;
					break;
				}
			}
			if (!collision) ret++;
		}
		return ret;
	}
	
	public static final float gridSpacing = 1.7f;
	
	public static ArrayList<MapLocation> getGridLocations(MapLocation location) throws GameActionException {
		float testRadius = rc.getType().sensorRadius - GARDENER_PATCH_RADIUS;
		float minRadius = 3;
		float boxWidth = testRadius * 2;
		int gridWidth = (int)(boxWidth / gridSpacing + 2);
		ArrayList<MapLocation> ret = new ArrayList<>(gridWidth * gridWidth);
		MapLocation start = location.translate(-testRadius, -testRadius);
		for (float dx = 0; dx < boxWidth; dx += gridSpacing) {
			for (float dy = 0; dy < boxWidth; dy += gridSpacing) {
				MapLocation l = start.translate(dx,dy);
				float dist = l.distanceTo(location);
				if (dist > testRadius || dist < minRadius) continue;
				ret.add(start.translate(dx,  dy));
			}
		}

		return ret;
	}
	
	// Gets the best starting location within sensor radius
	public static MapLocation getBestLocation() throws GameActionException {
		ArrayList<MapLocation> locations = getGridLocations(rc.getLocation());
		MapLocation ret = null;
		//System.out.println("MyLocation: " + rc.getLocation().toString());
		int max_trees = -1;
		for (MapLocation location : locations) {
			//rc.setIndicatorDot(location, 0, 0, 255);
			if (location == null) continue;
			int num_spots = getOpenTreeSpotsAbout(location);
			if (num_spots > max_trees) {
				ret = location;
				max_trees = num_spots;
			}
		}
		return ret;
	}
	
	public static MapLocation gridStart;
	public static final float spacing = 2 * GARDENER_PATCH_RADIUS;
	
	// Some of the returned location will be null
	public static MapLocation[] gridSpotsInSensorRadius() throws GameActionException {
		int maxGridWidth = (int)(2 * rc.getType().sensorRadius / spacing + 2);
		MapLocation[] ret = new MapLocation[maxGridWidth * maxGridWidth];
		float halfGridDist = maxGridWidth * spacing / 2;
		MapLocation botL = rc.getLocation().translate(-halfGridDist, -halfGridDist);
		int x = (int) Math.floor((botL.x - gridStart.x) / spacing);
		int y = (int) Math.floor((botL.y - gridStart.y) / spacing);
		
		MapLocation firstGrid = gridStart.translate(x * spacing, y * spacing);
		
		int k = 0;
		for (float dx = 0; dx < 2 * halfGridDist; dx += spacing) {
			for (float dy = 0; dy < 2 * halfGridDist; dy += spacing) {
				MapLocation l = firstGrid.translate(dx, dy);
				if (l.distanceTo(myLocation) > rc.getType().sensorRadius)
					continue;
				ret[k++] = l;
			}
		}
		
		return ret;
	}
}
