package better_dodge;
import battlecode.common.*;

public class Micro extends Globals {
  
 
  
  //First scout always faces outward, makes sure the penta shot does not hit the archons
  //
  public static void firstScoutShots(){}
  
  //Only attack while their obstacle is still there.  When enemy only needs one shot, flag it.  Select final two shots.
  public static void finishHim(){}
  
  //if two robots sense each other stop firing pentashots, start firing trishots
  public static int isCondensed(){
	  RobotInfo[] near = rc.senseNearbyRobots(-1, myTeam);
	  int len = near.length;
	  
	  if(len < 2) return -1;
	  else if(len == 2) return 0;
	  else return 1;
  }
  
  public static void chase() throws GameActionException
  {
	  RobotInfo[] bots = rc.senseNearbyRobots(-1, them);
	  if(bots.length > 0)
	  {
		  Direction chase = rc.getLocation().directionTo(bots[0].getLocation());
		  Pathfinding.tryMove(chase);
	  }
  }
  
  public static void SoldierFight() throws GameActionException
  {   
      RobotInfo[] bots = rc.senseNearbyRobots();

      chase();
      
      if(!rc.hasMoved())
      {
    	  SolderMove();
      }
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
  }
  
  static boolean trySidestep(BulletInfo bullet) throws GameActionException{

      Direction towards = bullet.getDir();
      MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
      MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);

      return(Pathfinding.tryMove(towards.rotateRightDegrees(90)) || Pathfinding.tryMove(towards.rotateLeftDegrees(90)));
  }
  
  public static final float dodgeDistance = 2;
  
  public static MapLocation getDodgeLocation(BulletInfo bullet) {
	  MapLocation bulletLocation = bullet.getLocation();
	  MapLocation myLocation = rc.getLocation();
	  float x = bulletLocation.x - myLocation.x;
	  float y = bulletLocation.y - myLocation.y;
	  
	  Direction bulletDir = bullet.getDir();
	  float c = (float)Math.cos(bulletDir.radians);
	  float s = (float)Math.sin(bulletDir.radians);
	  //float s = Math.sqrt(1 - c*c); // This might be more efficient
	  
	  // Rotate the coordinates so that bulletDir coincides with the y-axis
	  float newX = c * x + -s * y;
	  float newY = s * x + c * y;
	  
	  float reducedX = newX / bullet.getSpeed();
	  float reducedY = newY / rc.getType().strideRadius;
	  
	  Direction dir = new Direction(-reducedX, -reducedY);
	  return myLocation.add(dir, rc.getType().strideRadius * dodgeDistance);
  }

  public static void dodge() throws GameActionException {
      BulletInfo[] bullets = rc.senseNearbyBullets();
      MapLocation[] dodgeLocations = new MapLocation[bullets.length];
      for (int i = 0; i < bullets.length; i++) {
    	  dodgeLocations[i] = getDodgeLocation(bullets[i]);
      }
      Pathfinding.moveTo(Globals.averageLocations(dodgeLocations));
  }
  
  //If three robots sense each other then stop firing trishots, start firing singleshots
  
  
  //use archon sensors how dense map is with trees
  public static void SolderMove() throws GameActionException
  {
	  if(!rc.hasMoved())
	  {
		  if(rc.readBroadcast(ARCHON_TARGETING_CHANNEL) == -1) Pathfinding.wander();
		  else
	      {
	    	  if(rc.readBroadcast(DEFENSE_CHANNEL) == 1)
	    		  Pathfinding.moveTo(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL));
	    	  else
	    		  Pathfinding.moveTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL));
	      }
	  }
  }

  
  
  
  
}
