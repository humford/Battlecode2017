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
                
                if(rc.canWater())
                {
                	TreeInfo[] trees = rc.senseNearbyTrees(-1, myTeam);
              
                	if(trees.length != 0)
                	{
                		float maxHealth = 0;
                		int IDtoWater = trees[0].getID();
                		for(TreeInfo tree : trees)
                		{
                			if(maxHealth < tree.getHealth());
                			{
                				IDtoWater = tree.getID(); 
                				maxHealth = tree.getHealth();
                			}
                			
                		}
                		
                		rc.water(IDtoWater);
                	}
                	else wander();
                }
                
                
                if (rc.getRoundNum() < 500) {
                    int prevNumGard = rc.readBroadcast(LUMBERJACK_CHANNEL);
                    if (prevNumGard <= LUMBERJACK_MAX && rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                        rc.broadcast(LUMBERJACK_CHANNEL, prevNumGard + 1);
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
