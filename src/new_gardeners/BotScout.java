package new_gardeners;
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
	        
	       	 	if(! rc.hasAttacked()) {
	       	 		RobotInfo[] enemyBots = rc.senseNearbyRobots(rc.getType().sensorRadius, them);
	      	  		if(enemyBots.length > 0)
	      	  		{
	      	  			Direction run = rc.getLocation().directionTo(bots[0].getLocation()).rotateLeftDegrees(180);
	      	  			Pathfinding.tryMove(run);
	      	  		}

	      	  		if(!rc.hasMoved())
	            	{
	            		Pathfinding.tryMove(rc.getLocation().directionTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL)));
	            	}
	        	}
				Clock.yield();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}
