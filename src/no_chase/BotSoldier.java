package no_chase;
import battlecode.common.*;

public class BotSoldier extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
            try {
            	loop_common();

            	rc.broadcast(SOLDIER_SUM_CHANNEL, rc.readBroadcast(SOLDIER_SUM_CHANNEL) + 1);
            	
            	boolean existLumberjacks = false;
      	  		
      	  		if(!rc.hasMoved())
      	  		{
      	  			RobotInfo[] closeBots = rc.senseNearbyRobots(RobotType.SCOUT.bodyRadius + RobotType.SCOUT.strideRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS, them);
      	  			MapLocation temp = rc.getLocation();
      	  			for(RobotInfo bot : closeBots)
      	  			{
      	  				if(bot.getType() == RobotType.LUMBERJACK)
      	  				{
      	  					temp = temp.add(rc.getLocation().directionTo(bot.getLocation()).opposite(), 2);
      	  					rc.setIndicatorDot(bot.getLocation(), 127, 127, 0);
      	  					existLumberjacks = true;
      	  				}
      	  			}
      	  			
      	  			if(existLumberjacks)
      	  			{
      	  			
      	  				rc.setIndicatorLine(rc.getLocation(), temp, 0, 0, 127);
      	  				rc.setIndicatorDot(temp, 0, 0, 127);
      	  			
      	  				if(!temp.equals(rc.getLocation()))
      	  				{
      	  					Pathfinding.tryMove(rc.getLocation().directionTo(temp));
      	  				}
      	  			}
      	  		}
            	
            	Micro.SoldierFight();
                
            	end_loop_common();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
