package scoutmania;
import battlecode.common.*;

public class BotGardener extends Globals {
	public static final int MAX_WANDER_TURNS = 100;
	public static final int GARDENER_CIRCLE_RADIUS = 2;
	public static final int GARDENER_PATCH_RADIUS = 4;
	
	static MapLocation plantLoc = null;
	
	public static void gardener_common() throws GameActionException {
		loop_common();
		
		if(plantLoc != null)
		{
			rc.setIndicatorDot(plantLoc, 0, 0, 127);
		}
		
		rc.broadcast(GARDENER_SUM_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL) + 1);
		
		RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
    	Messaging.broadcastDefendMeIF(enemyBots.length >= 1);
    	
    	if(!treesToAdd.isEmpty())
    		treeList.addLocation(treesToAdd.remove(0));
	}
	
	public static void loop() throws GameActionException {
		
		rc.broadcastBoolean(GARDENER_INPRODUCTION_CHANNEL, false);
		
		for (int i = 0; i < MAX_WANDER_TURNS; i++) {
			
			gardener_common();
			MapLocation location = rc.getLocation();
			 
			//production build away from motion
			
			if(plantLoc != null && !location.equals(plantLoc))
				BuildQueue.tryBuildFromQueue(location.directionTo(plantLoc).opposite());
			else
				BuildQueue.tryBuildFromQueue();
			//if very open then start building 
			
			if (!rc.isCircleOccupiedExceptByThisRobot(location, rc.getType().sensorRadius))
				macro();
			
			//get location from list of open spots
            
			if(plantLoc == null)
			{
				plantLoc = plantingList.getNearest(location);
				if(plantLoc != null)
				{
					System.out.println(plantLoc.toString());
					trashList.addLocation(plantLoc);
				}
			}
			
			if(plantLoc == null)
			{
				if (!rc.isCircleOccupiedExceptByThisRobot(location, GARDENER_PATCH_RADIUS))
					macro();
				Pathfinding.gardenerWander(); // NEED BETTER FUNCTION TO MOVE
			}
			else
			{
				if(rc.canMove(plantLoc) && location.distanceTo(plantLoc) <= rc.getType().strideRadius)
				{
					rc.move(plantLoc);
					macro();
				}
				else
					Pathfinding.moveTo(plantLoc);
				
				MapLocation nearest  = plantingList.peakNearest(location);
				if(nearest != null && location.distanceTo(nearest) < location.distanceTo(plantLoc))
				{
					System.out.println("FUCK THIS");
					trashList.getNearest(plantLoc);
					plantingList.addLocation(plantLoc);
					plantLoc = plantingList.getNearest(location);
					if(plantLoc != null)
						trashList.addLocation(plantLoc);
				}
				
				if(rc.canSenseAllOfCircle(plantLoc, GARDENER_PATCH_RADIUS) && rc.onTheMap(plantLoc, GARDENER_PATCH_RADIUS))
				{
					if(rc.senseNearbyTrees(plantLoc, GARDENER_PATCH_RADIUS, myTeam).length > 0)
					{
						plantLoc = plantingList.getNearest(location);
					}
				}
				
			}
			end_loop_common();
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
		return rc.getLocation().directionTo(initialArchonLocations[0]);
	}
	
	public static MapLocation[] getTreeSpotsAbout(MapLocation location) throws GameActionException {
		MapLocation ret[] = new MapLocation[5];
		if (treeDirs == null) 
			treeDirs = getTreeDirs();
		for (int i = 0; i < 5; i++) {
			ret[i] = location.add(treeDirs[i], GARDENER_CIRCLE_RADIUS);
		}
		return ret;
	}
	
	public static void queueNearbyTrees(float radius) throws GameActionException {
		TreeInfo[] nearbyTrees = rc.senseNearbyTrees(radius);
		for (TreeInfo tree : nearbyTrees) {
			if (tree.team != myTeam) 
				treesToAdd.add(tree.location);
		}
	}
	
	public static void init() throws GameActionException {
		myLocation = rc.getLocation();
		treeDirs = getTreeDirs();
		treeLocations = getTreeSpotsAbout(myLocation);
		productionDirs = getProductionDirection();
		
		queueNearbyTrees(GARDENER_PATCH_RADIUS);
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
            	else
            		BuildQueue.tryBuildFromQueue();
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
						
			end_loop_common();
		}
		
	}
	
	public static int getOpenTreeSpotsAbout(MapLocation location, TreeInfo[] trees) throws GameActionException {
		if (!rc.onTheMap(location)) 
			return 0;
		if(rc.isCircleOccupied(location, RobotType.GARDENER.bodyRadius)) 
			return -1;
		MapLocation[] spots = getTreeSpotsAbout(location);
		int ret = 0;

		boolean collision;
		
		for (int i = 0; i < 5; i++) {
			collision = false;
			if (!rc.onTheMap(spots[i], GameConstants.BULLET_TREE_RADIUS))
				continue;
			for (TreeInfo tree : trees) {
				if (MapLocation.doCirclesCollide(spots[i], GameConstants.BULLET_TREE_RADIUS, tree.getLocation(), tree.getRadius())) {
					collision = true;
					break;
				}
			}
			if (!collision) 
				ret++;
		}
		return ret;
	}
	
	public static final float gridSpacing = 1.5f;
	
	public static MapLocation[] getGridLocations(MapLocation location) throws GameActionException {
		float testRadius = rc.getType().sensorRadius - GARDENER_PATCH_RADIUS;
		float minRadius = 3;
		float boxWidth = testRadius * 2;
		int gridWidth = (int)(boxWidth / gridSpacing + 2);
		MapLocation[] ret = new MapLocation[gridWidth * gridWidth];
		MapLocation start = location.translate(-testRadius, -testRadius);
		int k = 0;
		for (float dx = 0; dx < boxWidth; dx += gridSpacing) {
			for (float dy = 0; dy < boxWidth; dy += gridSpacing) {
				MapLocation l = start.translate(dx,dy);
				float dist = l.distanceTo(location);
				if (dist > testRadius || dist < minRadius) continue;
				ret[k++] = l;
			}
		}
		return ret;
	}
	
	// Gets the best starting location within sensor radius
	public static MapLocation getBestLocation() throws GameActionException {
		//int start = Clock.getBytecodeNum();
		MapLocation[] locations = getGridLocations(rc.getLocation());
		//int diff = Clock.getBytecodeNum() - start;
		//System.out.println("Cost of getting grid: " + Integer.toString(diff));
		MapLocation ret = null;
		//System.out.println("MyLocation: " + rc.getLocation().toString());
		int max_trees = -1;
		
		TreeInfo[] trees = rc.senseNearbyTrees();
		
		for (MapLocation location : locations) {
			if (location == null) 
				continue;
			
			rc.setIndicatorDot(location, 0, 0, 255);
			
			//start = Clock.getBytecodeNum();
			int num_spots = getOpenTreeSpotsAbout(location, trees);
			//diff = Clock.getBytecodeNum() - start;
			//System.out.println("Cost of getting grid: " + Integer.toString(diff));
			
			if(num_spots == 5)
				return location;
			
			if (num_spots > max_trees) {
				ret = location;
				max_trees = num_spots;
			}
		}
		return ret;
	}
	
	public static final float spacing = 2f * GARDENER_PATCH_RADIUS;
	
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
	
	public static void addGridLocation() throws GameActionException {
		if(plantingList.getLength() > LIST_HAZARD_LENGTH)
			return;
		
		MapLocation botL = rc.getLocation();
		int x = (int) Math.round((botL.x - gridStart.x) / spacing);
		int y = (int) Math.round((botL.y - gridStart.y) / spacing);
		
		MapLocation firstGrid = gridStart.translate(x * spacing, y * spacing);
		
		if(rc.canSenseAllOfCircle(firstGrid, GARDENER_PATCH_RADIUS) && rc.onTheMap(firstGrid, GARDENER_PATCH_RADIUS))
		{
			if(!rc.isCircleOccupiedExceptByThisRobot(firstGrid, GARDENER_PATCH_RADIUS))
			{
				if(!trashList.isIn(firstGrid))
				{
					plantingList.addLocation(firstGrid);
					rc.setIndicatorDot(firstGrid, 0, 127, 0);
				}
			}
		}
	}
}
