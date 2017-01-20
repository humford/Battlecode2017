package scoutmania;
import battlecode.common.*;


public class BotScout extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
			try {
				loop_common();
				
				RobotInfo[] bots = rc.senseNearbyRobots();

				for (RobotInfo b : bots) {
					if (b.getTeam() != rc.getTeam() && b.getType() != RobotType.ARCHON) {
						Direction towards = rc.getLocation().directionTo(b.getLocation());
						if(rc.canFireSingleShot())rc.fireSingleShot(towards);
						break;
					}      
				}
				
				if(!rc.hasAttacked() && bots.length > 0)
				{
					for (RobotInfo b : bots) {
						if (b.getTeam() != rc.getTeam()) {
							Direction towards = rc.getLocation().directionTo(b.getLocation());
							if(rc.canFireSingleShot())rc.fireSingleShot(towards);
							break;
						}      
					} 
				}
				
				Pathfinding.dodge();
	    	
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
				
				boolean is_gardener = false;
      	  		for(RobotInfo b : enemyBots)
      	  		{
      	  			if(b.getType() == RobotType.GARDENER)
      	  			{
      	  				Direction chase = rc.getLocation().directionTo(b.getLocation());
      	  				if(rc.getLocation().distanceTo(b.getLocation().add(chase)) < rc.getType().strideRadius)
      	  					rc.move(rc.getLocation().add(chase, rc.getLocation().distanceTo(b.getLocation().add(chase)) -  1));
      	  				else Pathfinding.tryMove(chase);
      	  				is_gardener = true;
      	  				break;
      	  			}
      	  		}
	        
      	  		if(!is_gardener && !rc.hasMoved())
      	  		{
      	  			Pathfinding.moveTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL));
      	  		}
				Clock.yield();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}
