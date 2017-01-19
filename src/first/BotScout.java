package first;
import battlecode.common.*;


public class BotScout extends Globals {
	public static void loop() throws GameActionException {
		while (true) {
			try {
				locateArchon();
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
	        
				RobotInfo[] bots = rc.senseNearbyRobots();

				for (RobotInfo b : bots) {
					if (b.getTeam() != rc.getTeam()) {
						Direction towards = rc.getLocation().directionTo(b.getLocation());
						switch(Micro.isCondensed())
						{
						case -1:
							if(rc.canFirePentadShot())rc.firePentadShot(towards);
							break;
						case 0:
							if(rc.canFireTriadShot())rc.fireTriadShot(towards);
							break;
						case 1:
							if(rc.canFireSingleShot())rc.fireSingleShot(towards);
							break;
	                	
						}
						break;
					}      
				}
	       	 	if(! rc.hasAttacked()) {
	       	 		RobotInfo[] enemyBots = rc.senseNearbyRobots(5, them);
	      	  		if(enemyBots.length > 0)
	      	  		{
	      	  			Direction run = rc.getLocation().directionTo(bots[0].getLocation()).rotateLeftDegrees(180);
	      	  			Pathfinding.tryMove(run);
	      	  		}

	      	  		if(!rc.hasMoved())
	            	{
	            		Pathfinding.tryMove(rc.getLocation().directionTo(recieveLocation(STRIKE_LOC_CHANNEL)));
	            	}
	        	}
				Clock.yield();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}
