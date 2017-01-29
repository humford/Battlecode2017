package scoutmania;
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
    	  if (b.getTeam() != myTeam) {
    		  Direction towards = rc.getLocation().directionTo(b.getLocation());
    		  switch(Micro.isCondensed())
              {
    		  case -1:
    			  if(rc.canFirePentadShot() && PentadShotOpen(b))
    			  {
    				  rc.firePentadShot(towards);
    			  	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
    			  }
    		  case 0:
                  if(rc.canFireTriadShot() && TriadShotOpen(b))
                  {
                	  rc.fireTriadShot(towards);
                	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
                  }
              case 1:
                  if(rc.canFireSingleShot() && SingleShotOpen(b))
                  {
                	  rc.fireSingleShot(towards);
                	  rc.setIndicatorLine(rc.getLocation(), b.getLocation(), 127, 0, 0);
                  }
                  break; //FALL THROUGH UNTIL CAN FIRE
              }
              if(rc.hasAttacked())
            	  break;
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

  public static void dodge() throws GameActionException {
      BulletInfo[] bullets = rc.senseNearbyBullets();
      for (BulletInfo bi : bullets) {
          if (willCollideWith(bi, rc.getLocation())) {
              trySidestep(bi);
          }
      }
  }
  
  //If three robots sense each other then stop firing trishots, start firing singleshots
  
  
  //use archon sensors how dense map is with trees
  public static void SolderMove() throws GameActionException
  {
	  if(!rc.hasMoved())
	  {
		  if(rc.readBroadcastBoolean(DEFENSE_CHANNEL))
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
