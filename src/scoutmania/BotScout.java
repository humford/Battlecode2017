package scoutmania;
import battlecode.common.*;



public class BotScout extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
			try {
				loop_common();
				
				RobotInfo[] bots = rc.senseNearbyRobots();
	    	
				//get bullets from neutral trees
				
				TreeInfo[] trees = rc.senseNearbyTrees();
				for (TreeInfo t : trees) {
					if(t.containedBullets > 0){
						Pathfinding.tryMove(rc.getLocation().directionTo(t.getLocation()));
						if(rc.canShake(t.getLocation()))
							rc.shake(t.getLocation());
						break;
					}
				}
				
				//stick to gardeners
				
				RobotInfo[] enemyBots = rc.senseNearbyRobots(-1, them);
				
      	  		for(RobotInfo b : enemyBots)
      	  		{
      	  			if(b.getType() == RobotType.GARDENER)
      	  			{
      	  				Direction chase = rc.getLocation().directionTo(b.getLocation());
      	  				if(rc.getLocation().distanceTo(b.getLocation()) < rc.getType().strideRadius)
      	  				{
      	  					System.out.println("REEEEE NORMIES");
      	  					if(rc.canMove(b.getLocation().add(chase.rotateLeftDegrees(180), 2))) rc.move(b.getLocation().add(chase.rotateLeftDegrees(180), 2));
      	  				}
      	  				else Pathfinding.tryMove(chase);

      	  				break;
      	  			}
      	  		}
      	  		
      	  		//flee from lumberjacks
      	  		
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
      	  		
      	  		existLumberjacks = false;
      	  		
      	  		if(!rc.hasMoved() && rc.readBroadcast(GARDENER_TARGETING_CHANNEL) != -1)
  	  			{
  	  				RobotInfo[] closeBots = rc.senseNearbyRobots(RobotType.SCOUT.bodyRadius + 3*RobotType.SCOUT.strideRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS, them);
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
  	  					Direction targetLoc;
  	  				
  	  					targetLoc = rc.getLocation().directionTo(Messaging.recieveLocation(SCOUT_LOC_CHANNEL));
  	  				
  	  					temp = temp.add(targetLoc, 2);
  	  					
  	  					rc.setIndicatorLine(rc.getLocation(), temp, 0, 127, 0);
  	  					rc.setIndicatorDot(temp, 0, 127, 0);
  	  			
  	  					if(!temp.equals(rc.getLocation()))
  	  					{
  	  						Pathfinding.tryMove(rc.getLocation().directionTo(temp));
  	  					}
  	  				}
  	  			}
      	  		
      	  		//move to a target location
      	  		
      	  		if(!rc.hasMoved())
      		  	{
      	  			if(rc.readBroadcast(GARDENER_TARGETING_CHANNEL) == -1) 
      			  		Pathfinding.wander();
      		    	  else
      		    		  Pathfinding.moveTo(Messaging.recieveLocation(SCOUT_LOC_CHANNEL));
      		  	}
      	  		
      	  		//fire bullets prioritize gardeners
      	  		
      	  		for(RobotInfo b : enemyBots)
      	  		{
      	  			if(b.getType() == RobotType.GARDENER)
      	  			{
      	  				Direction towards = rc.getLocation().directionTo(b.getLocation());
  	  					if(rc.canFireSingleShot() && SingleShotOpen(b))
  	  					{
  	  						rc.fireSingleShot(towards);
  	  						rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
  	  						break;
  	  					}
      	  			}
  	  			}
  	  			
      	  		if(!rc.hasAttacked())
      	  		{
      	  			for (RobotInfo b : bots) {
      	  				if (b.getTeam() != rc.getTeam() && b.getType() != RobotType.ARCHON) {
      	  					Direction towards = rc.getLocation().directionTo(b.getLocation());
      	  					if(rc.canFireSingleShot() && SingleShotOpen(b))
      	  					{
      	  						rc.fireSingleShot(towards);
      	  						rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
      	  						break;
      	  					}
      	  				}      
      	  			}	       
      	  		}
      	  		
      	  		end_loop_common();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}

