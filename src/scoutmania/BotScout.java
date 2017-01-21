package scoutmania;
import battlecode.common.*;



public class BotScout extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
			try {
				loop_common();
				
				RobotInfo[] bots = rc.senseNearbyRobots();
	    	
				TreeInfo[] trees = rc.senseNearbyTrees();
				for (TreeInfo t : trees) {
					if(t.containedBullets > 0){
						Pathfinding.tryMove(rc.getLocation().directionTo(t.getLocation()));
						if(rc.canShake(t.getLocation()))
							rc.shake(t.getLocation());
						break;
					}
				}
				
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
      	  		
      	  		if(!rc.hasMoved())
      		  	{
      			  	if(rc.readBroadcast(GARDENER_TARGETING_CHANNEL) == -1) Pathfinding.wander();
      			  	else
      		      	{
      		    	  	if(rc.readBroadcast(DEFENSE_CHANNEL) == 1)
      		    		  	Pathfinding.moveTo(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL));
      		    	  	else
      		    		  	Pathfinding.moveTo(Messaging.recieveLocation(SCOUT_LOC_CHANNEL));
      		      	}
      		  	}
	        

      	  		
      	  		for (RobotInfo b : bots) {
      	  			if (b.getTeam() != rc.getTeam() && b.getType() != RobotType.ARCHON) {
      	  				Direction towards = rc.getLocation().directionTo(b.getLocation());
      	  				if(rc.canFireSingleShot())rc.fireSingleShot(towards);
      	  				break;
      	  			}      
      	  		}
      	  		
				Clock.yield();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}

