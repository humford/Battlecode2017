package better_dodge;
import battlecode.common.*;

public class Micro extends Globals {
		
  public static final int MIN_CHASE_DIST = 4;
  public static final int MAX_CHASE_DIST = 6;
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
	  MapLocation myLoc = rc.getLocation();
	  for(RobotInfo bot : bots)
	  {
		  if(bot.getType() == RobotType.ARCHON)
		  {
			  continue;
		  }
		  else if(myLoc.distanceTo(bot.getLocation()) > MAX_CHASE_DIST)
		  {
			  Direction chase = rc.getLocation().directionTo(bot.getLocation());
			  Pathfinding.tryMove(chase);
			  break;
		  }
		  else if(myLoc.distanceTo(bot.getLocation()) < MIN_CHASE_DIST)
		  {
			  Direction chase = rc.getLocation().directionTo(bot.getLocation()).opposite();
			  Pathfinding.tryMove(chase);
			  break;
		  }
	  }
  }
  
  static int fireOffSet = 0;
  
  public static void SoldierFight() throws GameActionException
  {   
      RobotInfo[] bots = rc.senseNearbyRobots();

      chase();
      
      if(!rc.hasMoved())
      {
    	  SolderMove();
      }
      for (RobotInfo b : bots) {
    	  if (b.getTeam() != myTeam) {
    		  Direction towards = rc.getLocation().directionTo(b.getLocation());
    		  
    		  if(rc.canFirePentadShot() && PentadShotOpen(b))
			  {
				  if(fireOffSet == 0)
            		  towards = towards.rotateLeftDegrees(3.75f);
            	  else if(fireOffSet == 1)
            		  towards = towards.rotateRightDegrees(3.75f);
				  rc.firePentadShot(towards);
			  	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
			  	  break;
			  }
    		  else if(rc.canFireTriadShot() && TriadShotOpen(b))
              {
            	  if(fireOffSet == 0)
            		  towards = towards.rotateLeftDegrees(5f);
            	  else if(fireOffSet == 1)
            		  towards = towards.rotateRightDegrees(5f);
            	  rc.fireTriadShot(towards);
            	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
            	  break;
              }
    		  else if(rc.canFireSingleShot() && SingleShotOpen(b))
              {
            	  rc.fireSingleShot(towards);
            	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
            	  break;
              }
    		  
              if(rc.hasAttacked())
            	  fireOffSet = (fireOffSet + 1) % 2;
          }      
      }
  }
  
  static boolean trySidestep(BulletInfo bullet) throws GameActionException{

      Direction towards = bullet.getDir();
      MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
      MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);
      //return(Pathfinding.tryMove(towards.rotateRightDegrees(90)) || Pathfinding.tryMove(towards.rotateLeftDegrees(90)));
      return false;
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
	  dodge(rc.senseNearbyBullets());
  }

  public static void dodge(BulletInfo[] bullets) throws GameActionException {
      MapLocation[] dodgeLocations = new MapLocation[bullets.length];
      for (int i = 0; i < bullets.length; i++) {
    	  dodgeLocations[i] = getDodgeLocation(bullets[i]);
      }
	  hasDodged = true;
      Pathfinding.moveTo(Globals.averageLocations(dodgeLocations));
  }
  
  //If three robots sense each other then stop firing trishots, start firing singleshots
  
  
  //use archon sensors how dense map is with trees
  public static void SolderMove() throws GameActionException
  {
	  
	  if(!rc.hasMoved())
	  {
		  RobotInfo[] enemies = rc.senseNearbyRobots(-1, them);
		  if(rc.readBroadcastBoolean(DEFENSE_CHANNEL) && enemies.length == 0)
  		  	Pathfinding.moveTo(Messaging.recieveLocation(DEFENSE_LOC_CHANNEL));
  			
		  else
	      {
		  	if(rc.readBroadcast(ARCHON_TARGETING_CHANNEL) == -1) 
		  		Pathfinding.wander();
	    	else
	    	  	Pathfinding.moveTo(Messaging.recieveLocation(STRIKE_LOC_CHANNEL));
	      }
	  }
  }
}
