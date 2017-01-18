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
                Direction dir = randomDirection();
                
                TreeInfo[] closeTrees = rc.senseNearbyTrees(1, myTeam);
                
                if(rc.canWater() && closeTrees.length != 0)
                {     		
            		float minHealth = closeTrees[0].getHealth();
            		TreeInfo lowHealthTree = closeTrees[0];

            		for(TreeInfo tree : closeTrees)
            		{
            			if(minHealth > tree.getHealth());
            			{
            				lowHealthTree = tree; 
            				minHealth = tree.getHealth();
            			}
            			
            		}
            		
            		rc.water(lowHealthTree.getID());
                }
                
                TreeInfo[] trees = rc.senseNearbyTrees(-1, myTeam);
                
            	if(trees.length != 0 && rc.getLocation().distanceTo(rc.getInitialArchonLocations(myTeam)[0]) > 5)
            	{
            		float minHealth = trees[0].getHealth();
            		TreeInfo lowHealthTree = trees[0];
            		for(TreeInfo tree : trees)
            		{
            			if(minHealth > tree.getHealth());
            			{
            				lowHealthTree = tree; 
            				minHealth = tree.getHealth();
            			}
            			
            		}
            		
            		tryMove(rc.getLocation().directionTo(lowHealthTree.getLocation())); 
            	}
            	
            	else wander();
                
                
                if (rc.getRoundNum() < ROUND_CHANGE) {
                    int prevNumGard = rc.readBroadcast(LUMBERJACK_ALIVE_CHANNEL);
                    if (prevNumGard < LUMBERJACK_MAX && rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                        rc.broadcast(LUMBERJACK_ALIVE_CHANNEL, prevNumGard + 1);
                    }
                    
                    else if (rc.canPlantTree(dir)) {
                        rc.plantTree(dir); 
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
