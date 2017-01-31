package better_dodge;
import battlecode.common.*;

public class BotLumberjack extends Globals {
	private static MapLocation birthLoc;
	public static void loop() throws GameActionException {
		birthLoc = rc.getLocation();
        while (true) {
            try {
            	loop_common();
            	
            	Micro.chase();
                
                RobotInfo[] myBots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, myTeam);
                RobotInfo[] theirBots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, them);
                boolean isArchon = false;
                
                for(RobotInfo b : theirBots)
                {
                	if(b.getType() == RobotType.ARCHON)
                	{
                		isArchon = true;
                		break;
                	}
                }

                
                if((myBots.length < theirBots.length || isArchon) && rc.canStrike())rc.strike();
                
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
                	if(rc.canShake(t.getLocation()) && t.containedBullets > 0){
                		rc.shake(t.getLocation());
                	}
                    if (t.getTeam() != myTeam && rc.canChop(t.getLocation())) {
                        rc.chop(t.getLocation());
                    }
                    if(t.getTeam() != myTeam){
                    	Pathfinding.moveTo(t.getLocation());
                    	break;
                    }         
                } 
             
                
               /* TreeInfo[] neutralTrees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
                
                if(neutralTrees.length > 0)
                {
                    float minDist = birthLoc.distanceTo(neutralTrees[0].getLocation());
                    TreeInfo bestTree = neutralTrees[0];
                    
                    for(TreeInfo t : neutralTrees)
                    {
                    	if(birthLoc.distanceTo(t.getLocation()) < minDist)
                    	{
                    		minDist = birthLoc.distanceTo(t.getLocation());
                    		bestTree = t;
                    	}
                    }
                    
                    Pathfinding.moveTo(bestTree.getLocation());
                    
                }*/

                Micro.SolderMove();
                
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
