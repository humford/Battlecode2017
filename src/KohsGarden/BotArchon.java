package KohsGarden;
import battlecode.common.*;

class BotArchon extends Globals {
	public static void loop() throws GameActionException {
		
		if(rc.getLocation() == rc.getInitialArchonLocations(myTeam)[0])
		{
			rc.broadcast(BuildQueue.POINTER_CHANNEL, BuildQueue.low_endpoint);
			
			broadcastLocation(initialArchonLocations[0], STRIKE_LOC_CHANNEL);
			
			BuildQueue.enqueue(RobotType.GARDENER);
			BuildQueue.enqueue(RobotType.GARDENER);
			
			LocationList locs = new LocationList(1000, 9000);
			
			locs.addLocation(new MapLocation(5, 5));
			
			locs.addLocation(new MapLocation(5, 5));
			
			System.out.println("");
			
			locs.printList();
			
			System.out.println("");
			
			System.out.println("\n\n" + locs.getNearest(new MapLocation(5,5)) + "\n\n");
			
			locs.printList();
			
			System.out.println("");
			
			int start = Clock.getBytecodeNum();
			
			for(int i = 0; i < 100; i ++)
			{
				locs.addLocation(new MapLocation(i, i));
			}
			
			locs.printList();
			
			System.out.println(locs.isIn(new MapLocation(0,0)));
			
			System.out.println("CODES: " + (Clock.getBytecodeNum() - start));
			
			Clock.yield();
			
			start = Clock.getBytecodeNum();
			
			locs.addLocation(new MapLocation(-1, -1));
			
			System.out.println("ADD CODES: " + (Clock.getBytecodeNum() - start));
			
			start = Clock.getBytecodeNum();
			
			System.out.println("\n\n" + locs.getNearest(new MapLocation(5,5)) + "\n\n");
			
			System.out.println("GET CODES: " + (Clock.getBytecodeNum() - start));
			
			Clock.yield();
			
			start = Clock.getBytecodeNum();
			
			System.out.println("\n\n" + locs.peakNearest(new MapLocation(5,5)) + "\n\n");
			
			System.out.println("PEAK CODES: " + (Clock.getBytecodeNum() - start));
			
		/*	locs.printList();
			
			locs.addLocation(new MapLocation(5,5));
			
			locs.addLocation(new MapLocation(5.5f, 6));
			
			locs.printList();
			
			System.out.println("\n\n" + locs.getNearest(new MapLocation(0,0)) + "\n\n");
			
			locs.addLocation(new MapLocation(5,5));
			
			
			locs.addLocation(new MapLocation(15,15));
			
			locs.printList();
			
			System.out.println("\n\n" + locs.getNearest(new MapLocation(5,5)) + "\n\n");
			
			locs.addLocation(new MapLocation(5,5));
			
			
			locs.addLocation(new MapLocation(15,15));
			
			locs.printList();
			
			System.out.println("\n\n" + locs.getNearest(new MapLocation(5,5)) + "\n\n");
			
			locs.addLocation(new MapLocation(15,15));
			locs.addLocation(new MapLocation(16,16));
			
			locs.printList();
			
			//locs.debug();*/
			

		}
		
        while (true) {
            try {
            	
            	rc.disintegrate();
            	
            	rc.broadcast(GARDENER_COUNT_CHANNEL, rc.readBroadcast(GARDENER_SUM_CHANNEL));
            	rc.broadcast(GARDENER_SUM_CHANNEL, 0);

            	if(BuildQueue.getLength() <= 0)
            	{
            		BuildQueue.enqueue(RobotType.GARDENER);
            	}
            	
            	if(rc.getTeamBullets() > VICTORY_CASH)
            	{
            		int x = (int)rc.getTeamBullets() - VICTORY_CASH;
            		rc.donate(x - x%10);
            	}
            	
            	locateArchon();
            	
            	//Pathfinding.wander();
            	
            	Pathfinding.dodge();
            	
            	RobotInfo[] enemyBots = rc.senseNearbyRobots(5, them);
            	
            	if(enemyBots.length > 2)
            	{
            		broadcastLocation(rc.getLocation(), DEFENSE_LOC_CHANNEL);
            		rc.broadcast(DEFENSE_CHANNEL, 1);
            	}
            	
            	else rc.broadcast(DEFENSE_CHANNEL, 0);
                
            	Direction dir = randomDirection();
            	
                if(BuildQueue.getLength() > 0)
                {
                	if(rc.canBuildRobot(BuildQueue.peak(), dir))
                	{
                		rc.buildRobot(BuildQueue.dequeue(), dir);
                	}
                }
         
                	
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
