package scoutmania;
import battlecode.common.*;

public class BotLumberjack extends Globals {

	public static void loop() throws GameActionException {
        while (true) {
            try {
            	loop_common();
            	
            	Micro.chase();
                
                RobotInfo[] myBots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, myTeam);
                RobotInfo[] theirBots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, them);
                boolean isSoldier = false;
                
                for(RobotInfo b : theirBots)
                {
                	if(b.getType() == RobotType.SOLDIER)
                	{
                		isSoldier = true;
                		break;
                	}
                }

                
                if((myBots.length < theirBots.length || isSoldier) && rc.canStrike())rc.strike();
                
                clearTrees();
                
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
                	if(rc.canShake(t.getLocation()) && t.containedBullets > 0){
                		rc.shake(t.getLocation());
                	}
                    if (t.getTeam() != myTeam && rc.canChop(t.getLocation())) {
                        rc.chop(t.getLocation());
                    }
                    if(t.getTeam() != myTeam){
                    	if(!rc.hasMoved());
                    		Pathfinding.moveTo(t.getLocation());
                    }         
                }
                

                Micro.SolderMove();
                
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	public static MapLocation targetTree;
	
	public static void clearTrees() throws GameActionException {
		if (targetTree == null) 
			targetTree = treeList.getNearest(rc.getLocation());
		
		// There are no trees to target
		if (targetTree == null)
			return;
		
		if (rc.canSenseLocation(targetTree)) {
			TreeInfo tree = rc.senseTreeAtLocation(targetTree);
			if (tree == null || tree.getTeam() == myTeam) {
				targetTree = null;
			}
		}
		if (targetTree != null) {
			Pathfinding.moveTo(targetTree);
			rc.setIndicatorLine(rc.getLocation(), targetTree, 0, 255, 255);
		}
	}
}
